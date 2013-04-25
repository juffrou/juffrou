package org.juffrou.xml.converter;

import java.util.Collection;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.util.reflect.BeanWrapperContext;
import org.juffrou.xml.internal.JuffrouMarshaller;
import org.juffrou.xml.internal.binding.BeanClassBinding;
import org.juffrou.xml.internal.binding.BeanPropertyBinding;
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

	public void toXml(JuffrouMarshaller marshaller, JuffrouWriter writer, Object instance) {
		if(instance == null)
			return;
		Collection<BeanPropertyBinding> propertiesToMarshall = beanClassBinding.getBeanPropertiesToMarshall().values();
		BeanWrapper bw = new BeanWrapper(beanWrapperContext, instance);
		for(BeanPropertyBinding beanPropertyBinding : propertiesToMarshall) {
			Object value = bw.getValue(beanPropertyBinding.getBeanPropertyName());
			if(value != null) {
				writer.startNode(beanPropertyBinding.getXmlElementName());
				marshaller.marshallProperty(writer, value);
				writer.endNode();
			}
		}
	}
}
