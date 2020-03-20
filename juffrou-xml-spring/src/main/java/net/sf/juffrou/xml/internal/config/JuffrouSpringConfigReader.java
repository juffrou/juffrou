package net.sf.juffrou.xml.internal.config;

import net.sf.juffrou.xml.internal.JuffrouBeanMetadata;
import net.sf.juffrou.xml.serializer.Serializer;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class JuffrouSpringConfigReader extends ConfigReader {

	protected static String BEAN_ELEMENT_NODENAME = "bean";
	
	private final ApplicationContext applicationContext;
	
	public JuffrouSpringConfigReader(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	protected Serializer processSerializer(JuffrouBeanMetadata metadata, Node currentNode) {
		
		// read tag attributes
		NamedNodeMap attributes = currentNode.getAttributes();

		Node attribute = attributes.getNamedItem(BEAN_ELEMENT_NODENAME);
		if(attribute != null) {
			return applicationContext.getBean(attribute.getNodeValue(), Serializer.class);
		}

		return super.processSerializer(metadata, currentNode);
	}
}
