package org.juffrou.xml.serializer;

import java.util.Collection;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.xml.internal.JuffrouBeanMetadata;
import org.juffrou.xml.internal.JuffrouMarshaller;
import org.juffrou.xml.internal.binding.BeanClassBinding;
import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;

public abstract class AbstractCollectionSerializer implements Serializer {
	
	private JuffrouBeanMetadata xmlBeanMetadata;
	private JuffrouMarshaller marshaller;
	
	public AbstractCollectionSerializer(JuffrouBeanMetadata xmlBeanMetadata) {
		this.xmlBeanMetadata = xmlBeanMetadata;
	}

	public void serialize(JuffrouWriter writer, Object instance) {
		if(instance == null)
			return;
		Collection<?> collection = (Collection<?>) instance;
		if(collection.isEmpty())
			return;
		Object bean = collection.iterator().next();
		BeanClassBinding beanClassBinding = xmlBeanMetadata.getBeanClassBindingFromClass(bean);
		BeanWrapper bw = new BeanWrapper(beanClassBinding.getBeanWrapperContext());
		for(Object object : collection) {
			bw.setBean(object);
			writer.startNode(beanClassBinding.getXmlElementName());
			marshaller.marshallBeanProperties(writer, beanClassBinding, bw);
			writer.endNode();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object deserialize(JuffrouReader reader) {
		Collection collection = instantiateCollection();
		String next = reader.enterNode();
		BeanClassBinding beanClassBinding = xmlBeanMetadata.getBeanClassBindingFromXmlElement(next);
		BeanWrapper beanWrapper = new BeanWrapper(beanClassBinding.getBeanWrapperContext());
		marshaller.unmarshallBeanProperties(reader, beanClassBinding, beanWrapper);
		collection.add(beanWrapper.getBean());
		reader.exitNode();
		next = reader.next();
		while(next != null) {
			beanWrapper.reset();
			reader.enterNode();
			marshaller.unmarshallBeanProperties(reader, beanClassBinding, beanWrapper);
			collection.add(beanWrapper.getBean());
			reader.exitNode();
			next = reader.next();
		}
		return collection;
	}
	
	protected abstract Collection<?> instantiateCollection();
}
