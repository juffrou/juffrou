package net.sf.juffrou.xml;

import net.sf.juffrou.xml.internal.JuffrouBeanMetadata;
import net.sf.juffrou.xml.internal.JuffrouXmlMarshaller;
import net.sf.juffrou.xml.internal.config.ConfigReader;
import net.sf.juffrou.xml.serializer.Serializer;

public class JuffrouXml {

	private final JuffrouBeanMetadata xmlBeanMetadata;
	private final JuffrouXmlMarshaller xmlMarshaller;
	
	public JuffrouXml() {
		this.xmlBeanMetadata = new JuffrouBeanMetadata();
		this.xmlMarshaller = new JuffrouXmlMarshaller(this.xmlBeanMetadata);
	}
	
	/**
	 * @param mappingUrlSpec (example "classpath:juffrou-xml-mapping.xml")
	 */
	public JuffrouXml(String mappingUrlSpec) {
		this.xmlBeanMetadata = new JuffrouBeanMetadata();
		readConfigFile(mappingUrlSpec);
		this.xmlMarshaller = new JuffrouXmlMarshaller(this.xmlBeanMetadata);
	}
	
	/**
	 * @param mappingUrlSpec (example "classpath:juffrou-xml-mapping.xml")
	 */
	public void readConfigFile(String mappingUrlSpec) {
		ConfigReader.readConfigFile(this.xmlBeanMetadata, mappingUrlSpec);
	}
	
	public String toXml(Object object) {
		return xmlMarshaller.toXml(object);
	}

	public JuffrouXmlMarshaller getXmlMarshaller() {
		return xmlMarshaller;
	}
	
	public Object fromXml(String xml) {
		return xmlMarshaller.fromXml(xml);
	}
	
	/**
	 * Registers a bean with juffrou serializer and defines the name to use in tags representing this bean
	 * @param beanClazz the class of the java bean
	 * @param elementName the xml element name to use
	 */
	public void registerRootElement(Class beanClazz, String elementName) {
		xmlBeanMetadata.registerRootElement(beanClazz, elementName);
	}
	
	public void registerElement(Class beanClazz, String beanPropertyName, String elementName, String serializerId) {
		xmlBeanMetadata.registerProperty(beanClazz, beanPropertyName, elementName, serializerId);
	}
	
	public void registerSerializer(String serializerId, Serializer serializer) {
		xmlBeanMetadata.registerSerializer(serializerId, serializer);
	}

}
