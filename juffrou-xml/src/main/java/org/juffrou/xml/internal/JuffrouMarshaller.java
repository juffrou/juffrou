package org.juffrou.xml.internal;

import java.util.Collection;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.xml.internal.binding.BeanClassBinding;
import org.juffrou.xml.internal.binding.BeanPropertyBinding;
import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;
import org.juffrou.xml.serializer.Serializer;

public class JuffrouMarshaller {
	
	private JuffrouBeanMetadata xmlBeanMetadata;
	
	public JuffrouMarshaller() {
		this.xmlBeanMetadata = new JuffrouBeanMetadata();
	}

	public void marshallBean(JuffrouWriter writer, Object bean) {
		BeanClassBinding beanClassBinding = xmlBeanMetadata.getBeanClassBindingFromClass(bean);
		BeanWrapper bw = new BeanWrapper(beanClassBinding.getBeanWrapperContext(), bean);
		writer.startNode(beanClassBinding.getXmlElementName());
		marshallBeanProperties(writer, beanClassBinding, bw);
		writer.endNode();
	}
	
	public void marshallBeanProperties(JuffrouWriter writer, BeanClassBinding beanClassBinding, BeanWrapper bw) {
		Collection<BeanPropertyBinding> propertiesToMarshall = beanClassBinding.getBeanPropertiesToMarshall().values();
		for(BeanPropertyBinding beanPropertyBinding : propertiesToMarshall) {
			Object value = bw.getValue(beanPropertyBinding.getBeanPropertyName());
			if(value != null) {
				writer.startNode(beanPropertyBinding.getXmlElementName());
				Serializer converter = beanPropertyBinding.getConverter();
				if(converter == null)
					converter = xmlBeanMetadata.getConverterForType(beanPropertyBinding.getPropertyType());
				if(converter == null) {
					BeanWrapper nestedWrapper = bw.getNestedWrapper(beanPropertyBinding.getBeanPropertyName());
					BeanClassBinding nestedBeanClassBinding = xmlBeanMetadata.getBeanClassBindingFromClass(nestedWrapper.getBean());
					marshallBeanProperties(writer, nestedBeanClassBinding, nestedWrapper);
				}
				else {
					converter.serialize(writer, value);
				}
				writer.endNode();
			}
		}
	}
	
	public Object unmarshallBean(JuffrouReader reader) {
		
		String next = reader.enterNode();
		BeanClassBinding beanClassBinding = xmlBeanMetadata.getBeanClassBindingFromXmlElement(next);
		BeanWrapper beanWrapper = new BeanWrapper(beanClassBinding.getBeanWrapperContext());
		unmarshallBeanProperties(reader, beanClassBinding, beanWrapper);
		Object bean = beanWrapper.getBean();
		return bean;
	}
	
	public void unmarshallBeanProperties(JuffrouReader reader, BeanClassBinding beanClassBinding, BeanWrapper instance) {
		String xmlElementName = reader.enterNode();
		while(xmlElementName != null) {
			BeanPropertyBinding beanPropertyBinding = beanClassBinding.getBeanPropertiesToMarshall().get(xmlElementName);
			Serializer converter = beanPropertyBinding.getConverter();
			if(converter == null)
				converter = xmlBeanMetadata.getConverterForType(beanPropertyBinding.getPropertyType());
			if(converter == null) {
				// treat it as a bean
				BeanWrapper nestedWrapper = instance.getNestedWrapper(beanPropertyBinding.getBeanPropertyName());
				unmarshallBeanProperties(reader, beanClassBinding, nestedWrapper);
			}
			else {
				instance.setValue(beanPropertyBinding.getBeanPropertyName(), converter.deserialize(reader));
			}
			xmlElementName = reader.next();
		}
		reader.exitNode();
	}

}
