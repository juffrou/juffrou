package net.sf.juffrou.xml.internal.binding;

import net.sf.juffrou.xml.serializer.Serializer;

public class BeanPropertyBinding {

	private Class<?> propertyType;
	private String beanPropertyName;
	private String xmlElementName;
	private Serializer serializer;
	
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
	public Serializer getSerializer() {
		return serializer;
	}
	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}
}
