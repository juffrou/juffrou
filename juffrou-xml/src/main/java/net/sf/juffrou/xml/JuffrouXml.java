package net.sf.juffrou.xml;

import net.sf.juffrou.xml.internal.JuffrouBeanMetadata;
import net.sf.juffrou.xml.internal.JuffrouXmlMarshaller;
import net.sf.juffrou.xml.internal.NodeType;
import net.sf.juffrou.xml.internal.XmlConstants;
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
	 * Defines the name of the xml element that corresponds to a bean class.<p>
	 * For example a class with name net.sf.juffrou.xml.test.dom.Person with an elementName=Person will be represented by the XML element<br>
	 * &lt;Person&gt;<br>
	 * &nbsp;&nbsp;...<br>
	 * &lt;/Person&gt;<br>
	 * @param beanClazz the class of the java bean
	 * @param elementName the xml element name to use
	 */
	public void registerRootElement(Class beanClazz, String elementName) {
		xmlBeanMetadata.registerRootElement(beanClazz, elementName, null);
	}

	/**
	 * @param beanClazz
	 * @param elementName
	 * @param serializerId
	 */
	public void registerRootElement(Class beanClazz, String elementName, String serializerId) {
		xmlBeanMetadata.registerRootElement(beanClazz, elementName, serializerId);
	}

	/**
	 * Configures a bean property to be represented by an xml element and defines the element name.<p>
	 * For example a class Person with a firstName property defined with elementName=name will be represented by<br>
	 * &lt;Person&gt;<br>
	 * &nbsp;&nbsp;&lt;name&gt;...&lt;/name&gt;<br>
	 * &lt;/Person&gt;<br>
	 * @param beanClazz
	 * @param beanPropertyName
	 * @param elementName
	 */
	public void registerElement(Class beanClazz, String beanPropertyName, String elementName) {
		xmlBeanMetadata.registerProperty(beanClazz, beanPropertyName, elementName, null, NodeType.ELEMENT);
	}

	/**
	 * @param beanClazz
	 * @param beanPropertyName
	 * @param elementName
	 * @param serializerId
	 */
	public void registerElement(Class beanClazz, String beanPropertyName, String elementName, String serializerId) {
		xmlBeanMetadata.registerProperty(beanClazz, beanPropertyName, elementName, serializerId, NodeType.ELEMENT);
	}

	/**
	 * Configures a bean property to be represented by an xml attribute and defines the attribute name.<p>
	 * For example a class Person with a firstName property defined with attributeName=name will be represented by<br>
	 * &lt;Person name="..."&gt;<br>
	 * &nbsp;&nbsp;...<br>
	 * &lt;/Person&gt;<br>
	 * @param beanClazz
	 * @param beanPropertyName
	 * @param attributeName
	 */
	public void registerAttribute(Class beanClazz, String beanPropertyName, String attributeName) {
		xmlBeanMetadata.registerProperty(beanClazz, beanPropertyName, attributeName, null, NodeType.ATTRIBUTE);
	}

	/**
	 * @param beanClazz
	 * @param beanPropertyName
	 * @param attributeName
	 * @param serializerId
	 */
	public void registerAttribute(Class beanClazz, String beanPropertyName, String attributeName, String serializerId) {
		xmlBeanMetadata.registerProperty(beanClazz, beanPropertyName, attributeName, serializerId, NodeType.ATTRIBUTE);
	}

	/**
	 * Configures a bean property to be represented by an xml text node.<p>
	 * For example a class Person with a firstName property defined as a text node will be represented by<br>
	 * &lt;Person&gt;...&lt;/Person&gt;<br>
	 * @param beanClazz
	 * @param beanPropertyName
	 */
	public void registerText(Class beanClazz, String beanPropertyName) {
		xmlBeanMetadata.registerProperty(beanClazz, beanPropertyName, XmlConstants.CDATA_ELEMENT_NAME, null, NodeType.TEXT);
	}

	/**
	 * @param beanClazz
	 * @param beanPropertyName
	 * @param serializerId
	 */
	public void registerText(Class beanClazz, String beanPropertyName, String serializerId) {
		xmlBeanMetadata.registerProperty(beanClazz, beanPropertyName, XmlConstants.CDATA_ELEMENT_NAME, serializerId, NodeType.TEXT);
	}

}
