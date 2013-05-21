package net.sf.juffrou.xml.serializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.juffrou.util.reflect.BeanWrapper;
import net.sf.juffrou.util.reflect.BeanWrapperContext;
import net.sf.juffrou.xml.error.NonParameterizedGenericType;
import net.sf.juffrou.xml.internal.JuffrouBeanMetadata;
import net.sf.juffrou.xml.internal.NodeType;
import net.sf.juffrou.xml.internal.ValueHolder;
import net.sf.juffrou.xml.internal.binding.BeanClassBinding;
import net.sf.juffrou.xml.internal.io.JuffrouReader;
import net.sf.juffrou.xml.internal.io.JuffrouWriter;

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
			keyClassBinding = xmlBeanMetadata.getXmlBeanWrapperContextCreator().newBeanWrapperContext(firstEntry.getKey().getClass());

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
			valueClassBinding = xmlBeanMetadata.getBeanClassBindingFromClass(firstEntry.getValue().getClass());
			valueWrapper = new BeanWrapper(valueClassBinding);
		}
		// write everything
		for(Entry<?,?> entry : map.entrySet()) {
			writer.startNode("entry", NodeType.ELEMENT);
			// write the key
			if(isSimpleKey) {
				keyWrapper.setValue("value", entry.getKey());
				writer.startNode(firstEntry.getKey().getClass().getSimpleName().toLowerCase(), NodeType.ELEMENT);
				keySerializer.serialize(writer, keyWrapper, "value");
				writer.endNode();
			}
			else {
				keyWrapper.setBean(entry.getKey());
				writer.startNode(keyClassBinding.getXmlElementName(), NodeType.ELEMENT);
				xmlBeanMetadata.getDefaultSerializer().serializeBeanProperties(writer, keyWrapper);
				writer.endNode();
			}
			// write the value
			if(isSimpleValue) {
				valueWrapper.setValue("value", entry.getValue());
				writer.startNode(firstEntry.getValue().getClass().getSimpleName().toLowerCase(), NodeType.ELEMENT);
				valueSerializer.serialize(writer, valueWrapper, "value");
				writer.endNode();
			}
			else {
				valueWrapper.setBean(entry.getValue());
				writer.startNode(valueClassBinding.getXmlElementName(), NodeType.ELEMENT);
				xmlBeanMetadata.getDefaultSerializer().serializeBeanProperties(writer, valueWrapper);
				writer.endNode();
			}
			writer.endNode();
		}
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void deserialize(JuffrouReader reader, BeanWrapper valueOwner, String valuePropertyName) {
		Map map = new HashMap();
		boolean isSimpleKey = false;
		boolean isSimpleValue = false;
		String[] mapXmlElementNames = getMapXmlElementNames(reader);
		
		// analyze the key and value types
		Type[] collectionElementTypes = valueOwner.getTypeArguments(valuePropertyName);
		if(collectionElementTypes == null || collectionElementTypes.length < 0)
			throw new NonParameterizedGenericType("Cannot deserialize Map because its element types are undetermined.");
		Class keyType = (Class) collectionElementTypes[0];
		Class valueType = (Class) collectionElementTypes[1];
		
		Serializer keySerializer = xmlBeanMetadata.getSerializerForClass(keyType);
		BeanWrapper keyWrapper = null;
		BeanClassBinding keyClassBinding = null;
		if(keySerializer != null) {
			isSimpleKey = true;
			keyWrapper = new BeanWrapper(valueHolderWrapperContext);
		}
		else {
			keyClassBinding = xmlBeanMetadata.getBeanClassBindingFromXmlElement(mapXmlElementNames[0]);
		}
		Serializer valueSerializer = xmlBeanMetadata.getSerializerForClass(valueType);
		BeanWrapper valueWrapper = null;
		BeanClassBinding valueClassBinding = null;
		if(valueSerializer != null) {
			isSimpleValue = true;
			valueWrapper = new BeanWrapper(valueHolderWrapperContext);
		}
		else {
			valueClassBinding = xmlBeanMetadata.getBeanClassBindingFromXmlElement(mapXmlElementNames[1]);
		}

		String nodeName = reader.enterNode(); // entry
		while(nodeName != null) {
			reader.enterNode();	// key
			Object key;
			if(isSimpleKey) {
				keySerializer.deserialize(reader, keyWrapper, "value");
				key = keyWrapper.getValue("value");
			}
			else {
				keyWrapper = new BeanWrapper(keyClassBinding);
				xmlBeanMetadata.getDefaultSerializer().deserializeBeanProperties(reader, keyWrapper);
				key = keyWrapper.getBean();
			}
			reader.next();	// value
			Object value;
			if(isSimpleValue) {
				valueSerializer.deserialize(reader, valueWrapper, "value");
				value = valueWrapper.getValue("value");
			}
			else {
				valueWrapper = new BeanWrapper(valueClassBinding);
				xmlBeanMetadata.getDefaultSerializer().deserializeBeanProperties(reader, valueWrapper);
				value = valueWrapper.getBean();
			}
			map.put(key, value);
			reader.exitNode();	// entry
			nodeName = reader.next();	// next entry
		}
		reader.exitNode();
		
		valueOwner.setValue(valuePropertyName, map);
	}
	
	private String[] getMapXmlElementNames(JuffrouReader reader) {
		String[] xmlElementNames = new String[2];
		reader.enterNode(); // entry
		xmlElementNames[0] = reader.enterNode(); // key
		xmlElementNames[1] = reader.next();	// value
		reader.exitNode();	// entry
		reader.exitNode();	// top
		return xmlElementNames;
	}
}
