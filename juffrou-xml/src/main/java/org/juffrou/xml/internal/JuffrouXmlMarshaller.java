package org.juffrou.xml.internal;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import org.juffrou.util.reflect.BeanWrapper;

public class JuffrouXmlMarshaller {
	
	private XmlBeanMetadata xmlBeanMetadata;
	
	public JuffrouXmlMarshaller() {
		this.xmlBeanMetadata = new XmlBeanMetadata();
	}

	public void marshallBean(Writer writer, Object bean) {
		BeanClassBinding beanClassBinding = xmlBeanMetadata.getBeanClassBinding(bean.getClass());
		if(beanClassBinding == null)
			beanClassBinding = xmlBeanMetadata.addBeanClassBinding(bean);
		BeanWrapper bw = new BeanWrapper(beanClassBinding.getBeanWrapperContext(), bean);
		marshallBean(writer, beanClassBinding, bw);
	}
	
	private void marshallBean(Writer writer, BeanClassBinding beanClassBinding, BeanWrapper bw) {
		try {
			writer.write("<" + beanClassBinding.getXmlElementName() + ">");
			Collection<BeanPropertyBinding> propertiesToMarshall = beanClassBinding.getBeanPropertiesToMarshall().values();
			for(BeanPropertyBinding beanPropertyBinding : propertiesToMarshall) {
				Object value = bw.getValue(beanPropertyBinding.getBeanPropertyName());
				if(value != null) {
					writer.write("<" + beanPropertyBinding.getXmlElementName() + ">");
					writer.write(value.toString());
					writer.write("</" + beanPropertyBinding.getXmlElementName() + ">");
				}
			}
			writer.write("</" + beanClassBinding.getXmlElementName() + ">");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
