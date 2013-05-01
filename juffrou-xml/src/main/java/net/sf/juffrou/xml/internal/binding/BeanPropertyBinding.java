package net.sf.juffrou.xml.internal.binding;

import net.sf.juffrou.xml.serializer.Serializer;

public class BeanPropertyBinding {

	private Class<?> propertyType;
	private String beanPropertyName;
	private String xmlElementName;
	private Serializer converter;
	
	public Class<?> getPropertyType() {
		return propertyType;
	}
	public void setPropertyType(Class<?> propertyType) {
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
	public Serializer getConverter() {
		return converter;
	}
	public void setConverter(Serializer converter) {
		this.converter = converter;
	}
}
