package org.juffrou.xml.internal.binding;

import java.lang.reflect.Type;

import org.juffrou.xml.converter.Converter;

public class BeanPropertyBinding {

	private Type propertyType;
	private String beanPropertyName;
	private String xmlElementName;
	private Converter converter;
	
	public Type getPropertyType() {
		return propertyType;
	}
	public void setPropertyType(Type propertyType) {
		this.propertyType = propertyType;
	}
	public String getBeanPropertyName() {
		return beanPropertyName;
	}
	public void setBeanPropertyName(String beanPropertyName) {
		this.beanPropertyName = beanPropertyName;
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
	public void setConverter(Converter converter) {
		this.converter = converter;
	}
}
