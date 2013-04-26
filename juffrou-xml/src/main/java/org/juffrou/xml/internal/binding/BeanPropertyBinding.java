package org.juffrou.xml.internal.binding;

public class BeanPropertyBinding {

	private String beanPropertyName;
	private String xmlElementName;
	private boolean isBeanClass = false;
	
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
	public boolean isBeanClass() {
		return isBeanClass;
	}
	public void setBeanClass(boolean isBeanClass) {
		this.isBeanClass = isBeanClass;
	}
}
