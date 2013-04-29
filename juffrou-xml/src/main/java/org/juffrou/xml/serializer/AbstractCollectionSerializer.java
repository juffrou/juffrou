package org.juffrou.xml.serializer;

import java.lang.reflect.Type;
import java.util.Collection;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.util.reflect.BeanWrapperContext;
import org.juffrou.xml.internal.JuffrouBeanMetadata;
import org.juffrou.xml.internal.binding.BeanClassBinding;
import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;

public abstract class AbstractCollectionSerializer implements Serializer {
	
	private final JuffrouBeanMetadata xmlBeanMetadata;
	private final BeanWrapperContext valueHolderWrapperContext;
	
	public AbstractCollectionSerializer(JuffrouBeanMetadata xmlBeanMetadata) {
		this.xmlBeanMetadata = xmlBeanMetadata;
		this.valueHolderWrapperContext = new BeanWrapperContext(ValueHolder.class);
	}

	public void serialize(JuffrouWriter writer, BeanWrapper valueOwner, String valuePropertyName) {
		Collection<?> collection = (Collection<?>) valueOwner.getValue(valuePropertyName);
		if(collection.isEmpty())
			return;
		Object bean = collection.iterator().next();
		Serializer serializer = xmlBeanMetadata.getSerializerForClass(bean.getClass());
		if(serializer != null)
			serializeSimpleType(writer, collection, serializer);
		else
			serializeBeanType(writer, collection, bean);
	}
	
	private void serializeSimpleType(JuffrouWriter writer, Collection<?> collection, Serializer serializer) {
		BeanWrapper valueHolderWrapper = new BeanWrapper(valueHolderWrapperContext);
		for(Object object : collection) {
			valueHolderWrapper.setValue("value", object);
			writer.startNode("value");
			serializer.serialize(writer, valueHolderWrapper, "value");
			writer.endNode();
		}
	}

	private void serializeBeanType(JuffrouWriter writer, Collection<?> collection, Object firstBean) {
		BeanClassBinding beanClassBinding = xmlBeanMetadata.getBeanClassBindingFromClass(firstBean);
		BeanWrapper bw = new BeanWrapper(beanClassBinding);
		for(Object object : collection) {
			bw.setBean(object);
			writer.startNode(beanClassBinding.getXmlElementName());
			xmlBeanMetadata.getDefaultSerializer().serializeBeanProperties(writer, bw);
			writer.endNode();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void deserialize(JuffrouReader reader, BeanWrapper valueOwner, String valuePropertyName) {
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
		String next = reader.getNodeName();
		next = reader.enterNode(); // <value>
		while(next != null) {
			BeanWrapper valueHolderWrapper = new BeanWrapper(valueHolderWrapperContext);
			serializer.deserialize(reader, valueHolderWrapper, "value");
			collection.add(valueHolderWrapper.getValue("value"));
			next = reader.next();
		}
		reader.exitNode();
	}

	private void deserializeBeanType(JuffrouReader reader, Collection collection) {
		String next = reader.enterNode();
		BeanClassBinding beanClassBinding = xmlBeanMetadata.getBeanClassBindingFromXmlElement(next);
		reader.exitNode();
		while(next != null) {
			next = reader.enterNode();
			BeanWrapper beanWrapper = new BeanWrapper(beanClassBinding);
			xmlBeanMetadata.getDefaultSerializer().deserializeBeanProperties(reader, beanWrapper);
			collection.add(beanWrapper.getBean());
			reader.exitNode();
			next= reader.next();
		}

	}
	
	protected abstract Collection<?> instantiateCollection();
	
	public static class ValueHolder {
		private Object value;
		
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
	}
}
