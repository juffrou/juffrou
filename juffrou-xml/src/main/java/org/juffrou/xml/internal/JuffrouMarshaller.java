package org.juffrou.xml.internal;

<<<<<<< HEAD
import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.xml.converter.Converter;
import org.juffrou.xml.internal.binding.BeanClassBinding;
import org.juffrou.xml.internal.io.JuffrouReader;
=======
import java.io.IOException;

import org.juffrou.xml.internal.binding.BeanClassBinding;
>>>>>>> origin/master
import org.juffrou.xml.internal.io.JuffrouWriter;

public class JuffrouMarshaller {
	
	private JuffrouBeanMetadata xmlBeanMetadata;
	
	public JuffrouMarshaller() {
		this.xmlBeanMetadata = new JuffrouBeanMetadata();
	}

	public void marshallBean(JuffrouWriter writer, Object bean) {
<<<<<<< HEAD
		BeanClassBinding beanClassBinding = xmlBeanMetadata.getBeanClassBinding(bean);
		writer.startNode(beanClassBinding.getXmlElementName());
		beanClassBinding.getConverter().toXml(this, writer, bean);
		writer.endNode();
	}
	
	public void marshallProperty(JuffrouWriter writer, Object bean) {
		Converter converter = xmlBeanMetadata.getConverterForType(bean.getClass());
		if(converter == null) {
			BeanClassBinding beanClassBinding = xmlBeanMetadata.getBeanClassBinding(bean.getClass());
			converter = beanClassBinding.getConverter();
		}
		converter.toXml(this, writer, bean);
	}
	
	public Object unmarshall(JuffrouReader reader) {
		
		String next = reader.next();
		BeanClassBinding beanClassBinding = xmlBeanMetadata.getBeanClassBinding(next);
		BeanWrapper beanWrapper = new BeanWrapper(beanClassBinding.getBeanWrapperContext());
		Object object = beanClassBinding.getConverter().fromXml(this, reader, beanWrapper);
		return object;
	}

=======
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
		
>>>>>>> origin/master
}
