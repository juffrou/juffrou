package org.juffrou.xml.internal;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.util.reflect.BeanWrapperContext;
import org.juffrou.xml.converter.Converter;

public class XmlBeanMetadata {

	private Map<Type, Converter> generalConverters = new HashMap<Type, Converter>();
	private Map<Class<?>, BeanClassBinding> classToBindingMap = new HashMap<Class<?>, BeanClassBinding>();
	
	public BeanClassBinding getBeanClassBinding(Class<?> beanClass) {
		return classToBindingMap.get(beanClass);
	}
	
	public BeanClassBinding addBeanClassBinding(Object bean) {
		Class<? extends Object> BeanClass = bean.getClass();
		BeanClassBinding beanClassBinding = new BeanClassBinding(BeanClass);
		
		// add all the properties
		BeanWrapperContext beanWrapperContext = beanClassBinding.getBeanWrapperContext();
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
	
	public Converter getConverterForType(Type type) {
		Converter converter = generalConverters.get(type);
		return converter;
	}
	
	public Converter getConverterForBeanClass(BeanClassBinding beanClassBinding) {
		Converter converter = beanClassBinding.getConverter();
		return converter;
	}
}
