package net.sf.juffrou.xml.internal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import net.sf.juffrou.util.reflect.BeanWrapper;
import net.sf.juffrou.xml.JuffrouXmlException;
import net.sf.juffrou.xml.internal.binding.BeanClassBinding;
import net.sf.juffrou.xml.internal.io.JuffrouReader;
import net.sf.juffrou.xml.internal.io.JuffrouWriter;
import net.sf.juffrou.xml.internal.io.XmlReader;
import net.sf.juffrou.xml.internal.io.XmlWriter;

public class JuffrouXmlMarshaller {
	
	private JuffrouBeanMetadata xmlBeanMetadata;
	
	public JuffrouXmlMarshaller(JuffrouBeanMetadata xmlBeanMetadata) {
		this.xmlBeanMetadata = xmlBeanMetadata;
	}

	
	public String toXml(Object object) {
		JuffrouWriter writer = new XmlWriter();
		marshallBean(writer, object);
		String xml = writer.toString();
		return xml;
	}
	
	public Object fromXml(String xml) {
		Object object = null;
		try {
			InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			XmlReader reader = new XmlReader(stream);
			
			object = unmarshallBean(reader);
			
			return object;
			
		} catch (UnsupportedEncodingException e) {
			throw new JuffrouXmlException("Cannot create a reader of the XML string passed", e);
		}
	}

	private void marshallBean(JuffrouWriter writer, Object bean) {
		BeanClassBinding beanClassBinding = xmlBeanMetadata.getXmlBeanWrapperContextCreator().newBeanWrapperContext(bean.getClass());

		BeanWrapper bw = new BeanWrapper(beanClassBinding, bean);
		writer.startNode(beanClassBinding.getXmlElementName());
		xmlBeanMetadata.getDefaultSerializer().serializeBeanProperties(writer, bw);
		writer.endNode();
	}
	
	private Object unmarshallBean(JuffrouReader reader) {
		
		String next = reader.enterNode();
		BeanClassBinding beanClassBinding = xmlBeanMetadata.getBeanClassBindingFromXmlElement(next);
		BeanWrapper beanWrapper = new BeanWrapper(beanClassBinding);
		xmlBeanMetadata.getDefaultSerializer().deserializeBeanProperties(reader, beanWrapper);
		Object bean = beanWrapper.getBean();
		return bean;
	}
}
