package org.juffrou.xml.internal;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.xml.internal.binding.BeanClassBinding;
import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;

public class JuffrouMarshaller {
	
	private JuffrouBeanMetadata xmlBeanMetadata;
	
	public JuffrouMarshaller() {
		this.xmlBeanMetadata = new JuffrouBeanMetadata();
	}
	
	public JuffrouBeanMetadata getJuffrouBeanMetadata() {
		return xmlBeanMetadata;
	}

	public void marshallBean(JuffrouWriter writer, Object bean) {
		BeanClassBinding beanClassBinding = xmlBeanMetadata.getBeanClassBindingFromClass(bean);
		BeanWrapper bw = new BeanWrapper(beanClassBinding, bean);
		writer.startNode(beanClassBinding.getXmlElementName());
		xmlBeanMetadata.getDefaultSerializer().serializeBeanProperties(writer, bw);
		writer.endNode();
	}
	
	public Object unmarshallBean(JuffrouReader reader) {
		
		String next = reader.enterNode();
		BeanClassBinding beanClassBinding = xmlBeanMetadata.getBeanClassBindingFromXmlElement(next);
		BeanWrapper beanWrapper = new BeanWrapper(beanClassBinding);
		xmlBeanMetadata.getDefaultSerializer().deserializeBeanProperties(reader, beanWrapper);
		Object bean = beanWrapper.getBean();
		return bean;
	}
}
