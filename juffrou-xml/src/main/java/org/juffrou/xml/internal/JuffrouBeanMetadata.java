package org.juffrou.xml.internal;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.util.reflect.BeanWrapperContext;
import org.juffrou.xml.converter.Converter;
import org.juffrou.xml.converter.ToStringConverter;
import org.juffrou.xml.internal.binding.BeanClassBinding;
import org.juffrou.xml.internal.binding.BeanPropertyBinding;

public class JuffrouBeanMetadata {

	private Map<Type, Converter> generalConverters = new HashMap<Type, Converter>();
	private Map<Class<?>, BeanClassBinding> classToBindingMap = new HashMap<Class<?>, BeanClassBinding>();
	
	public JuffrouBeanMetadata() {
		setDefaultConverters();
	}
	
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
	
	private void setDefaultConverters() {
		ToStringConverter toStringConverter = new ToStringConverter();
		generalConverters.put(Boolean.class, toStringConverter);
		generalConverters.put(Byte.class, toStringConverter);
		generalConverters.put(Character.class, toStringConverter);
		generalConverters.put(Double.class, toStringConverter);
		generalConverters.put(Float.class, toStringConverter);
		generalConverters.put(Integer.class, toStringConverter);
		generalConverters.put(Long.class, toStringConverter);
		generalConverters.put(Short.class, toStringConverter);
		generalConverters.put(String.class, toStringConverter);
		generalConverters.put(BigInteger.class, toStringConverter);
		generalConverters.put(BigDecimal.class, toStringConverter);
		generalConverters.put(Enum.class, toStringConverter);
	}
}
