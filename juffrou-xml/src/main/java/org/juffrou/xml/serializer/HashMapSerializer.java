package org.juffrou.xml.serializer;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.util.reflect.BeanWrapperContext;
import org.juffrou.xml.internal.JuffrouBeanMetadata;
import org.juffrou.xml.internal.ValueHolder;
import org.juffrou.xml.internal.binding.BeanClassBinding;
import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;

public class HashMapSerializer implements Serializer {
	
	private final JuffrouBeanMetadata xmlBeanMetadata;
	private final BeanWrapperContext valueHolderWrapperContext;
	
	public HashMapSerializer(JuffrouBeanMetadata xmlBeanMetadata) {
		this.xmlBeanMetadata = xmlBeanMetadata;
		this.valueHolderWrapperContext = new BeanWrapperContext(ValueHolder.class);
	}

	public void serialize(JuffrouWriter writer, BeanWrapper valueOwner, String valuePropertyName) {
		boolean isSimpleKey = false;
		boolean isSimpleValue = false;
		Map<?,?> map = (Map<?,?>) valueOwner.getValue(valuePropertyName);
		if(map.isEmpty())
			return;
		// analyze the entry types
		Entry<?, ?> firstEntry = map.entrySet().iterator().next();
		Serializer keySerializer = xmlBeanMetadata.getSerializerForClass(firstEntry.getKey().getClass());
		BeanWrapper keyWrapper;
		BeanClassBinding keyClassBinding = null;
		if(keySerializer != null) {
			isSimpleKey = true;
			keyWrapper = new BeanWrapper(valueHolderWrapperContext);
		}
		else {
			keyClassBinding = xmlBeanMetadata.getBeanClassBindingFromClass(firstEntry.getKey());
			keyWrapper = new BeanWrapper(keyClassBinding);
		}
		Serializer valueSerializer = xmlBeanMetadata.getSerializerForClass(firstEntry.getValue().getClass());
		BeanWrapper valueWrapper;
		BeanClassBinding valueClassBinding = null;
		if(valueSerializer != null) {
			isSimpleValue = true;
			valueWrapper = new BeanWrapper(valueHolderWrapperContext);
		}
		else {
			valueClassBinding = xmlBeanMetadata.getBeanClassBindingFromClass(firstEntry.getValue());
			valueWrapper = new BeanWrapper(valueClassBinding);
		}
		// write everything
		for(Entry<?,?> entry : map.entrySet()) {
			writer.startNode("entry");
			// write the key
			if(isSimpleKey) {
				keyWrapper.setValue("value", entry.getKey());
				writer.startNode(firstEntry.getKey().getClass().getSimpleName().toLowerCase());
				keySerializer.serialize(writer, keyWrapper, "value");
				writer.endNode();
			}
			else {
				keyWrapper.setBean(entry.getKey());
				writer.startNode(keyClassBinding.getXmlElementName());
				xmlBeanMetadata.getDefaultSerializer().serializeBeanProperties(writer, keyWrapper);
				writer.endNode();
			}
			// write the value
			if(isSimpleValue) {
				valueWrapper.setValue("value", entry.getValue());
				writer.startNode(firstEntry.getValue().getClass().getSimpleName().toLowerCase());
				valueSerializer.serialize(writer, valueWrapper, "value");
				writer.endNode();
			}
			else {
				valueWrapper.setBean(entry.getValue());
				writer.startNode(valueClassBinding.getXmlElementName());
				xmlBeanMetadata.getDefaultSerializer().serializeBeanProperties(writer, valueWrapper);
				writer.endNode();
			}
			writer.endNode();
		}
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void deserialize(JuffrouReader reader, BeanWrapper valueOwner, String valuePropertyName) {
		Type[] collectionElementTypes = valueOwner.getTypeArguments(valuePropertyName);
		Collection collection = null;
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
}
