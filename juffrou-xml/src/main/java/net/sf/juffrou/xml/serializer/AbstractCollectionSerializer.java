package net.sf.juffrou.xml.serializer;

import net.sf.juffrou.reflect.BeanWrapperContext;
import net.sf.juffrou.reflect.JuffrouBeanWrapper;
import net.sf.juffrou.xml.internal.JuffrouBeanMetadata;
import net.sf.juffrou.xml.internal.NodeType;
import net.sf.juffrou.xml.internal.ValueHolder;
import net.sf.juffrou.xml.internal.binding.BeanClassBinding;
import net.sf.juffrou.xml.internal.io.JuffrouReader;
import net.sf.juffrou.xml.internal.io.JuffrouWriter;

import java.lang.reflect.Type;
import java.util.Collection;

public abstract class AbstractCollectionSerializer implements Serializer {
	
	private final JuffrouBeanMetadata xmlBeanMetadata;
	private final BeanWrapperContext valueHolderWrapperContext;
	
	public AbstractCollectionSerializer(JuffrouBeanMetadata xmlBeanMetadata) {
		this.xmlBeanMetadata = xmlBeanMetadata;
		this.valueHolderWrapperContext = BeanWrapperContext.create(ValueHolder.class);
	}

	public void serialize(JuffrouWriter writer, JuffrouBeanWrapper valueOwner, String valuePropertyName) {
		Collection<?> collection = (Collection<?>) valueOwner.getValue(valuePropertyName);
		if(xmlBeanMetadata.getPreferences().isCollectionWithSizeAttribute())
			writer.setAttribute("size", String.valueOf(collection.size()));
		if(collection.isEmpty())
			return;
		Object bean = collection.iterator().next();
		Serializer serializer = xmlBeanMetadata.getSerializerForClass(bean.getClass());
		if(serializer != null)
			serializeSimpleType(writer, collection, serializer, bean.getClass().getSimpleName().toLowerCase());
		else
			serializeBeanType(writer, collection, bean);
	}
	
	private void serializeSimpleType(JuffrouWriter writer, Collection<?> collection, Serializer serializer, String elementName) {
		JuffrouBeanWrapper valueHolderWrapper = new JuffrouBeanWrapper(valueHolderWrapperContext);
		for(Object object : collection) {
			valueHolderWrapper.setValue("value", object);
			writer.startNode(elementName, NodeType.ELEMENT);
			serializer.serialize(writer, valueHolderWrapper, "value");
			writer.endNode();
		}
	}

	private void serializeBeanType(JuffrouWriter writer, Collection<?> collection, Object firstBean) {
		BeanClassBinding beanClassBinding = (BeanClassBinding) xmlBeanMetadata.getBeanWrapperFactory().getBeanWrapperContext(firstBean.getClass());

		JuffrouBeanWrapper bw = new JuffrouBeanWrapper(beanClassBinding);
		for(Object object : collection) {
			bw.setBean(object);
			writer.startNode(beanClassBinding.getXmlElementName(), NodeType.ELEMENT);
			xmlBeanMetadata.getDefaultSerializer().serializeBeanProperties(writer, bw);
			writer.endNode();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void deserialize(JuffrouReader reader, JuffrouBeanWrapper valueOwner, String valuePropertyName) {
		Type[] collectionElementTypes = valueOwner.getTypeArguments(valuePropertyName);
		Collection collection = instantiateCollection();
		if(collectionElementTypes == null || collectionElementTypes.length == 0)
			deserializeBeanType(reader, collection);
		else {
			Serializer serializer = xmlBeanMetadata.getSerializerForClass((Class)collectionElementTypes[0]);
			if(serializer == null)
				deserializeBeanType(reader, collection);
			else
				deserializeSimpleType(reader, collection, serializer);
		}
		valueOwner.setValue(valuePropertyName, collection);
	}

	private void deserializeSimpleType(JuffrouReader reader, Collection collection, Serializer serializer) {
		String next = reader.enterNode(); // <value>
		while(next != null) {
			JuffrouBeanWrapper valueHolderWrapper = new JuffrouBeanWrapper(valueHolderWrapperContext);
			serializer.deserialize(reader, valueHolderWrapper, "value");
			collection.add(valueHolderWrapper.getValue("value"));
			next = reader.next();
		}
		reader.exitNode();
	}

	private void deserializeBeanType(JuffrouReader reader, Collection collection) {
		String next = reader.enterNode();
		BeanClassBinding beanClassBinding = xmlBeanMetadata.getBeanClassBindingFromXmlElement(next);
		while(next != null) {
			JuffrouBeanWrapper beanWrapper = new JuffrouBeanWrapper(beanClassBinding);
			xmlBeanMetadata.getDefaultSerializer().deserializeBeanProperties(reader, beanWrapper);
			collection.add(beanWrapper.getBean());
			next= reader.next();
		}
		reader.exitNode();
	}
	
	protected abstract Collection<?> instantiateCollection();
	
}
