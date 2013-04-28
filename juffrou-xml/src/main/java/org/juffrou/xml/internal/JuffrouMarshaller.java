package org.juffrou.xml.internal;

import java.util.Collection;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.xml.converter.Converter;
import org.juffrou.xml.internal.binding.BeanClassBinding;
import org.juffrou.xml.internal.binding.BeanPropertyBinding;
import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;

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
//		beanClassBinding.getConverter().toXml(this, writer, bean);
		writer.endNode();
	}
	
	private void marshallBeanProperties(JuffrouWriter writer, BeanClassBinding beanClassBinding, BeanWrapper bw) {
		Collection<BeanPropertyBinding> propertiesToMarshall = beanClassBinding.getBeanPropertiesToMarshall().values();
		for(BeanPropertyBinding beanPropertyBinding : propertiesToMarshall) {
			Object value = bw.getValue(beanPropertyBinding.getBeanPropertyName());
			if(value != null) {
				writer.startNode(beanPropertyBinding.getXmlElementName());
				Converter converter = beanPropertyBinding.getConverter();
				if(converter == null)
					converter = xmlBeanMetadata.getConverterForType(beanPropertyBinding.getPropertyType());
				if(converter == null) {
					BeanWrapper nestedWrapper = bw.getNestedWrapper(beanPropertyBinding.getBeanPropertyName());
					marshallBeanProperties(writer, beanClassBinding, nestedWrapper);
				}
				else {
					converter.toXml(writer, value);
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
	
	private void unmarshallBeanProperties(JuffrouReader reader, BeanClassBinding beanClassBinding, BeanWrapper instance) {
		String xmlElementName = reader.enterNode();
		while(xmlElementName != null) {
			BeanPropertyBinding beanPropertyBinding = beanClassBinding.getBeanPropertiesToMarshall().get(xmlElementName);
			Converter converter = beanPropertyBinding.getConverter();
			if(converter == null)
				converter = xmlBeanMetadata.getConverterForType(beanPropertyBinding.getPropertyType());
			if(converter == null) {
				// treat it as a bean
				BeanWrapper nestedWrapper = instance.getNestedWrapper(beanPropertyBinding.getBeanPropertyName());
				unmarshallBeanProperties(reader, beanClassBinding, nestedWrapper);
			}
			else {
				instance.setValue(beanPropertyBinding.getBeanPropertyName(), reader.getText());
			}
			xmlElementName = reader.next();
		}
		reader.exitNode();
	}

}
