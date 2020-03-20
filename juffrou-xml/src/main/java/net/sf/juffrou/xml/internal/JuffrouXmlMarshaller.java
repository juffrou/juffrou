package net.sf.juffrou.xml.internal;

import net.sf.juffrou.reflect.JuffrouBeanWrapper;
import net.sf.juffrou.xml.error.JuffrouXmlException;
import net.sf.juffrou.xml.internal.binding.BeanClassBinding;
import net.sf.juffrou.xml.internal.io.JuffrouReader;
import net.sf.juffrou.xml.internal.io.JuffrouWriter;
import net.sf.juffrou.xml.internal.io.XmlReader;
import net.sf.juffrou.xml.internal.io.XmlWriter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class JuffrouXmlMarshaller {
	
	private final JuffrouBeanMetadata xmlBeanMetadata;
	
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

	public void marshallBean(JuffrouWriter writer, Object bean) {
		BeanClassBinding beanClassBinding = (BeanClassBinding) xmlBeanMetadata.getBeanWrapperFactory().getBeanWrapperContext(bean.getClass());

		JuffrouBeanWrapper bw = new JuffrouBeanWrapper(beanClassBinding, bean);
		writer.startNode(beanClassBinding.getXmlElementName(), NodeType.ELEMENT);
		xmlBeanMetadata.getDefaultSerializer().serializeBeanProperties(writer, bw);
		writer.endNode();
	}
	
	public Object unmarshallBean(JuffrouReader reader) {
		
		String next = reader.enterNode();
		BeanClassBinding beanClassBinding = xmlBeanMetadata.getBeanClassBindingFromXmlElement(next);
		JuffrouBeanWrapper beanWrapper = new JuffrouBeanWrapper(beanClassBinding);
		xmlBeanMetadata.getDefaultSerializer().deserializeBeanProperties(reader, beanWrapper);
		Object bean = beanWrapper.getBean();
		return bean;
	}
}
