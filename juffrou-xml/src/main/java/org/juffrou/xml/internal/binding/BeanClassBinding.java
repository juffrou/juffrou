package org.juffrou.xml.internal.binding;

import java.util.HashMap;
import java.util.Map;

import org.juffrou.util.reflect.BeanWrapperContext;
import org.juffrou.xml.converter.BeanClassConverter;
import org.juffrou.xml.converter.Converter;

public class BeanClassBinding {

	private BeanClassConverter converter;
	private String xmlElementName;
	private Map<String, BeanPropertyBinding> beanPropertiesToMarshall = new HashMap<String, BeanPropertyBinding>();
	
	public BeanClassBinding(Class<?> clazz) {
		
		this.converter = new BeanClassConverter(this, new BeanWrapperContext(clazz));
		xmlElementName = clazz.getName();
	}
	
	public String getXmlElementName() {
		return xmlElementName;
	}
	public void setXmlElementName(String xmlElementName) {
		this.xmlElementName = xmlElementName;
	}
	public Converter getConverter() {
		return converter;
	}
	public BeanWrapperContext getBeanWrapperContext() {
		return converter.getBeanWrapperContext();
	}
	public Map<String, BeanPropertyBinding> getBeanPropertiesToMarshall() {
		return beanPropertiesToMarshall;
	}
	public void setBeanPropertiesToMarshall(
			Map<String, BeanPropertyBinding> beanPropertiesToMarshall) {
		this.beanPropertiesToMarshall = beanPropertiesToMarshall;
	}
	public void putBeanPropertyBinding(BeanPropertyBinding beanPropertyBinding) {
		this.beanPropertiesToMarshall.put(beanPropertyBinding.getBeanPropertyName(), beanPropertyBinding);
	}
	
}
