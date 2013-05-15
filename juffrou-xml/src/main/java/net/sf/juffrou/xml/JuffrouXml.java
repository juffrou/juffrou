package net.sf.juffrou.xml;

import net.sf.juffrou.xml.internal.JuffrouBeanMetadata;
import net.sf.juffrou.xml.internal.JuffrouXmlMarshaller;
import net.sf.juffrou.xml.internal.config.ConfigReader;

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
		ConfigReader.readConfigFile(this.xmlBeanMetadata, mappingUrlSpec);
		this.xmlMarshaller = new JuffrouXmlMarshaller(this.xmlBeanMetadata);
	}
	
	public String toXml(Object object) {
		return xmlMarshaller.toXml(object);
	}
	
	public Object fromXml(String xml) {
		return xmlMarshaller.fromXml(xml);
	}
}
