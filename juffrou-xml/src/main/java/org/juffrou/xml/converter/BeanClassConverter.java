package org.juffrou.xml.converter;

<<<<<<< HEAD
=======
import java.io.IOException;
>>>>>>> origin/master
import java.util.Collection;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.util.reflect.BeanWrapperContext;
<<<<<<< HEAD
import org.juffrou.xml.internal.JuffrouMarshaller;
import org.juffrou.xml.internal.binding.BeanClassBinding;
import org.juffrou.xml.internal.binding.BeanPropertyBinding;
import org.juffrou.xml.internal.io.JuffrouReader;
=======
import org.juffrou.xml.internal.binding.BeanClassBinding;
import org.juffrou.xml.internal.binding.BeanPropertyBinding;
>>>>>>> origin/master
import org.juffrou.xml.internal.io.JuffrouWriter;

public class BeanClassConverter implements Converter {

	private final BeanClassBinding beanClassBinding;
	private final BeanWrapperContext beanWrapperContext;

	public BeanClassConverter(BeanClassBinding beanClassBinding, BeanWrapperContext beanWrapperContext) {
		this.beanClassBinding = beanClassBinding;
		this.beanWrapperContext = beanWrapperContext;
	}
	
	public BeanWrapperContext getBeanWrapperContext() {
		return beanWrapperContext;
	}

<<<<<<< HEAD
	public void toXml(JuffrouMarshaller marshaller, JuffrouWriter writer, Object instance) {
=======
	public void toXml(JuffrouWriter writer, Object instance) throws IOException {
>>>>>>> origin/master
		if(instance == null)
			return;
		Collection<BeanPropertyBinding> propertiesToMarshall = beanClassBinding.getBeanPropertiesToMarshall().values();
		BeanWrapper bw = new BeanWrapper(beanWrapperContext, instance);
		for(BeanPropertyBinding beanPropertyBinding : propertiesToMarshall) {
			Object value = bw.getValue(beanPropertyBinding.getBeanPropertyName());
			if(value != null) {
				writer.startNode(beanPropertyBinding.getXmlElementName());
<<<<<<< HEAD
				marshaller.marshallProperty(writer, value);
=======
				writer.write(value.toString());
>>>>>>> origin/master
				writer.endNode();
			}
		}
	}
	
	public Object fromXml(JuffrouMarshaller marshaller, JuffrouReader reader, BeanWrapper instance) {
		String propertyName = reader.enterNode();
		BeanPropertyBinding beanPropertyBinding = beanClassBinding.getBeanPropertiesToMarshall().get(propertyName);
		if(beanPropertyBinding.isBeanClass()) {
			
		}
		else {
			
		}
		reader.exitNode();
		return instance.getBean();
	}
}
