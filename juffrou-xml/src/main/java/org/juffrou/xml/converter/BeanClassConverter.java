package org.juffrou.xml.converter;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.util.reflect.BeanWrapperContext;
import org.juffrou.xml.internal.BeanClassBinding;
import org.juffrou.xml.internal.BeanPropertyBinding;

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

	public void toXml(Writer writer, Object instance) throws IOException {
		if(instance == null)
			return;
		Collection<BeanPropertyBinding> propertiesToMarshall = beanClassBinding.getBeanPropertiesToMarshall().values();
		BeanWrapper bw = new BeanWrapper(beanWrapperContext, instance);
		for(BeanPropertyBinding beanPropertyBinding : propertiesToMarshall) {
			Object value = bw.getValue(beanPropertyBinding.getBeanPropertyName());
			if(value != null) {
				writer.write("<" + beanPropertyBinding.getXmlElementName() + ">");
				writer.write(value.toString());
				writer.write("</" + beanPropertyBinding.getXmlElementName() + ">");
			}
		}

	}
}
