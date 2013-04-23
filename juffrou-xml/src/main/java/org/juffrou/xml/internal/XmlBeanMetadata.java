package org.juffrou.xml.internal;

import java.util.HashMap;
import java.util.Map;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.util.reflect.BeanWrapperContext;

public class XmlBeanMetadata {

	private Map<Class<?>, BeanClassBinding> classToBindingMap = new HashMap<Class<?>, BeanClassBinding>();
	
	public BeanClassBinding getBeanClassBinding(Class<?> beanClass) {
		return classToBindingMap.get(beanClass);
	}
	
	public BeanClassBinding addBeanClassBinding(Object bean) {
		Class<? extends Object> BeanClass = bean.getClass();
		BeanClassBinding beanClassBinding = new BeanClassBinding();
		BeanWrapperContext beanWrapperContext = new BeanWrapperContext(BeanClass);
		beanClassBinding.setBeanWrapperContext(beanWrapperContext);
		beanClassBinding.setXmlElementName(BeanClass.getName());
		
		// add all the properties
		BeanWrapper bw = new BeanWrapper(beanWrapperContext);
		for(String propertyName : bw.getPropertyNames()) {
			BeanPropertyBinding beanPropertyBinding = new BeanPropertyBinding();
			beanPropertyBinding.setBeanPropertyName(propertyName);
			beanPropertyBinding.setXmlElementName(propertyName);
			beanClassBinding.putBeanPropertyBinding(beanPropertyBinding);
		}
		
		classToBindingMap.put(BeanClass, beanClassBinding);
		return beanClassBinding;
	}
}
