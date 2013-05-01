package net.sf.juffrou.xml;

import net.sf.juffrou.xml.internal.JuffrouBeanMetadata;
import net.sf.juffrou.xml.internal.JuffrouXmlMarshaller;

public class JuffrouXml {

	private final JuffrouBeanMetadata xmlBeanMetadata;
	private final JuffrouXmlMarshaller xmlMarshaller;
	
	public JuffrouXml() {
		this.xmlBeanMetadata = new JuffrouBeanMetadata();
		this.xmlMarshaller = new JuffrouXmlMarshaller(this.xmlBeanMetadata);
	}
	
	public String toXml(Object object) {
		return xmlMarshaller.toXml(object);
	}
	
	public Object fromXml(String xml) {
		return xmlMarshaller.fromXml(xml);
	}
}
