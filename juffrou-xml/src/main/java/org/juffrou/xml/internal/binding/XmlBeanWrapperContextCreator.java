package org.juffrou.xml.internal.binding;

import java.lang.reflect.Type;

import org.juffrou.util.reflect.BeanContextCreator;
import org.juffrou.xml.internal.JuffrouBeanMetadata;

public class XmlBeanWrapperContextCreator implements BeanContextCreator<BeanClassBinding> {

	private final JuffrouBeanMetadata juffrouBeanMetadata;
	
	public XmlBeanWrapperContextCreator(JuffrouBeanMetadata juffrouBeanMetadata) {
		this.juffrouBeanMetadata = juffrouBeanMetadata;
	}
	
	@Override
	public BeanClassBinding newBeanWrapperContext(Class clazz) {
		BeanClassBinding xmlBeanWrapperContext = new BeanClassBinding(clazz);
		xmlBeanWrapperContext.setBeanContextCreator(this);
		juffrouBeanMetadata.putBeanClassBinding(xmlBeanWrapperContext);
		return xmlBeanWrapperContext;
	}

	@Override
	public BeanClassBinding newBeanWrapperContext(Class clazz, Type... types) {
		BeanClassBinding xmlBeanWrapperContext = new BeanClassBinding(clazz, types);
		xmlBeanWrapperContext.setBeanContextCreator(this);
		juffrouBeanMetadata.putBeanClassBinding(xmlBeanWrapperContext);
		return xmlBeanWrapperContext;
	}

}