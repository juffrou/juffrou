package org.juffrou.xml.internal;

import java.io.IOException;

import org.juffrou.xml.internal.binding.BeanClassBinding;
import org.juffrou.xml.internal.io.JuffrouWriter;

public class JuffrouMarshaller {
	
	private JuffrouBeanMetadata xmlBeanMetadata;
	
	public JuffrouMarshaller() {
		this.xmlBeanMetadata = new JuffrouBeanMetadata();
	}

	public void marshallBean(JuffrouWriter writer, Object bean) {
		BeanClassBinding beanClassBinding = xmlBeanMetadata.getBeanClassBinding(bean.getClass());
		if(beanClassBinding == null)
			beanClassBinding = xmlBeanMetadata.addBeanClassBinding(bean);
		try {
			writer.startNode(beanClassBinding.getXmlElementName());
			beanClassBinding.getConverter().toXml(writer, bean);
			writer.endNode();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
}
