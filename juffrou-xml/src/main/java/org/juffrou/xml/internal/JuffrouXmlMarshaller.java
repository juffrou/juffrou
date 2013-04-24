package org.juffrou.xml.internal;

import java.io.IOException;
import java.io.Writer;

public class JuffrouXmlMarshaller {
	
	private XmlBeanMetadata xmlBeanMetadata;
	
	public JuffrouXmlMarshaller() {
		this.xmlBeanMetadata = new XmlBeanMetadata();
	}

	public void marshallBean(Writer writer, Object bean) {
		BeanClassBinding beanClassBinding = xmlBeanMetadata.getBeanClassBinding(bean.getClass());
		if(beanClassBinding == null)
			beanClassBinding = xmlBeanMetadata.addBeanClassBinding(bean);
		try {
			writer.write("<" + beanClassBinding.getXmlElementName() + ">");
			beanClassBinding.getConverter().toXml(writer, bean);
			writer.write("</" + beanClassBinding.getXmlElementName() + ">");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
}
