package net.sf.juffrou.xml;

import net.sf.juffrou.xml.internal.JuffrouBeanMetadata;
import net.sf.juffrou.xml.internal.JuffrouXmlMarshaller;
import net.sf.juffrou.xml.internal.NodeType;
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

	public void registerSerializer(String serializerId, Serializer serializer) {
		xmlBeanMetadata.registerSerializer(serializerId, serializer);
	}

	/**
	 * Defines the name that the root element corresponding to the bean class will have
	 * @param beanClazz the class of the java bean
	 * @param elementName the xml element name to use
	 */
	public void registerRootElement(Class beanClazz, String elementName) {
		xmlBeanMetadata.registerRootElement(beanClazz, elementName, null);
	}

	public void registerRootElement(Class beanClazz, String elementName, String serializerId) {
		xmlBeanMetadata.registerRootElement(beanClazz, elementName, serializerId);
	}

	public void registerElement(Class beanClazz, String beanPropertyName, String elementName) {
		xmlBeanMetadata.registerProperty(beanClazz, beanPropertyName, elementName, null, NodeType.ELEMENT);
	}

	public void registerElement(Class beanClazz, String beanPropertyName, String elementName, String serializerId) {
		xmlBeanMetadata.registerProperty(beanClazz, beanPropertyName, elementName, serializerId, NodeType.ELEMENT);
	}

	public void registerAttribute(Class beanClazz, String beanPropertyName, String attributeName) {
		xmlBeanMetadata.registerProperty(beanClazz, beanPropertyName, attributeName, null, NodeType.ATTRIBUTE);
	}

	public void registerAttribute(Class beanClazz, String beanPropertyName, String attributeName, String serializerId) {
		xmlBeanMetadata.registerProperty(beanClazz, beanPropertyName, attributeName, serializerId, NodeType.ATTRIBUTE);
	}
	
}
