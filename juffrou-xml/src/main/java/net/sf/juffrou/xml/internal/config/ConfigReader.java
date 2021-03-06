package net.sf.juffrou.xml.internal.config;

import net.sf.juffrou.reflect.BeanWrapperContext;
import net.sf.juffrou.reflect.JuffrouBeanWrapper;
import net.sf.juffrou.xml.error.JuffrouXmlConfigReaderException;
import net.sf.juffrou.xml.error.XmlMappingReaderException;
import net.sf.juffrou.xml.internal.JuffrouBeanMetadata;
import net.sf.juffrou.xml.internal.NodeType;
import net.sf.juffrou.xml.internal.XmlConstants;
import net.sf.juffrou.xml.internal.binding.BeanClassBinding;
import net.sf.juffrou.xml.internal.binding.BeanPropertyBinding;
import net.sf.juffrou.xml.internal.config.protocols.classpath.Handler;
import net.sf.juffrou.xml.serializer.Serializer;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * Reads the mapping file and sets the metadata values
 * 
 * @author cemartins
 */
public class ConfigReader {
	
	protected static String PREFERENCES_NODENAME = "preferences";
	protected static String ROOT_ELEMENT_NODENAME = "root-element";
	protected static String ATTRIBUTE_ELEMENT_NODENAME = "attribute";
	protected static String TEXT_ELEMENT_NODENAME = "text";
	protected static String ELEMENT_ELEMENT_NODENAME = "element";
	protected static String SERIALIZER_ELEMENT_NODENAME = "serializer";
	protected static String TYPE_ELEMENT_NODENAME = "type";
	protected static String XML_ELEMENT_NODENAME = "xml";
	protected static String REF_ELEMENT_NODENAME = "ref";
	protected static String CLASS_ELEMENT_NODENAME = "class";
	protected static String ID_ELEMENT_NODENAME = "id";
	protected static String PROPERTY_ELEMENT_NODENAME = "property";
	
	public void readConfigFile(JuffrouBeanMetadata metadata, String urlSpec) {
		try {
			URL url = new URL(null, urlSpec, new Handler(ClassLoader.getSystemClassLoader()));
			InputStream stream = url.openStream();
			readConfigFile(metadata, stream);
			stream.close();

		} catch (UnsupportedEncodingException e) {
			throw new JuffrouXmlConfigReaderException("Cannot create a reader of the XML string passed", e);
		} catch (FileNotFoundException e) {
			throw new JuffrouXmlConfigReaderException("Cannot create a reader of the XML string passed", e);
		} catch (IOException e) {
			throw new JuffrouXmlConfigReaderException("Cannot create a reader of the XML string passed", e);
		}
	}

	public void readConfigFile(JuffrouBeanMetadata metadata, InputStream stream) {
		try {
			
			Document doc;
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(stream);
	        // normalize text representation
			doc.getDocumentElement().normalize();
			
			processMappingElement(metadata, doc.getFirstChild());
			
		} catch (UnsupportedEncodingException e) {
			throw new JuffrouXmlConfigReaderException("Cannot create a reader of the XML string passed", e);
		} catch (ParserConfigurationException e) {
			throw new JuffrouXmlConfigReaderException("Cannot create a reader of the XML string passed", e);
		} catch (SAXException e) {
			throw new JuffrouXmlConfigReaderException("Cannot create a reader of the XML string passed", e);
		} catch (IOException e) {
			throw new JuffrouXmlConfigReaderException("Cannot create a reader of the XML string passed", e);
		}

	}

	protected void processMappingElement(JuffrouBeanMetadata metadata, Node currentNode) {
		
		currentNode = currentNode.getFirstChild();
		while(currentNode != null) {
			if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
				if(currentNode.getNodeName().equals(ROOT_ELEMENT_NODENAME))
					processRootElement(metadata, currentNode);
				else if(currentNode.getNodeName().equals(SERIALIZER_ELEMENT_NODENAME))
					processSerializer(metadata, currentNode);
				else if(currentNode.getNodeName().equals(PREFERENCES_NODENAME))
					processPreferences(metadata, currentNode);
			}
			currentNode = currentNode.getNextSibling();
		}
	}
	
	protected void processPreferences(JuffrouBeanMetadata metadata, Node currentNode) {
		
		// read tag attributes
		
		// read tag content
		currentNode = currentNode.getFirstChild();
		while(currentNode != null) {
			if(currentNode.getNodeName().equals(PREFERENCES_NODENAME))
				processPreferences(metadata, currentNode);
			currentNode = currentNode.getNextSibling();
		}
		
	}
	
	
	/**
	 * Processes a bean as an XML element root
	 * @param metadata
	 * @param currentNode
	 */
	protected void processRootElement(JuffrouBeanMetadata metadata, Node currentNode) {
		
		// read tag attributes
		NamedNodeMap attributes = currentNode.getAttributes();
		Node attribute = attributes.getNamedItem(TYPE_ELEMENT_NODENAME);
		Class<?> clazz;
		try {
			clazz = Class.forName(attribute.getNodeValue());
		} catch (ClassNotFoundException e) {
			throw new XmlMappingReaderException(e);
		}

		BeanClassBinding xmlBeanWrapperContext = (BeanClassBinding) metadata.getBeanWrapperFactory().getBeanWrapperContext(clazz);
		attribute = attributes.getNamedItem(XML_ELEMENT_NODENAME);
		if(attribute != null)
			xmlBeanWrapperContext.setXmlElementName(attribute.getNodeValue());

		// read tag content
		currentNode = currentNode.getFirstChild();
		while(currentNode != null) {
			if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
				if(currentNode.getNodeName().equals(ELEMENT_ELEMENT_NODENAME))
					processElement(metadata, xmlBeanWrapperContext, currentNode, NodeType.ELEMENT);
				else if(currentNode.getNodeName().equals(ATTRIBUTE_ELEMENT_NODENAME))
					processElement(metadata, xmlBeanWrapperContext, currentNode, NodeType.ATTRIBUTE);
				else if(currentNode.getNodeName().equals(TEXT_ELEMENT_NODENAME))
					processElement(metadata, xmlBeanWrapperContext, currentNode, NodeType.TEXT);
				else if(currentNode.getNodeName().equals(XML_ELEMENT_NODENAME))
					processRootElementXml(metadata, xmlBeanWrapperContext, currentNode);
				else if(currentNode.getNodeName().equals(SERIALIZER_ELEMENT_NODENAME))
					xmlBeanWrapperContext.setSerializer(processSerializer(metadata, currentNode));
			}
			currentNode = currentNode.getNextSibling();
		}
		
		metadata.putBeanClassBinding(xmlBeanWrapperContext);

	}

	protected void processRootElementXml(JuffrouBeanMetadata metadata, BeanClassBinding xmlBeanWrapperContext, Node currentNode) {
		NamedNodeMap attributes = currentNode.getAttributes();
		Node attribute = attributes.getNamedItem(ID_ELEMENT_NODENAME);
		xmlBeanWrapperContext.setXmlElementName(attribute.getNodeValue());
	}

	/**
	 * Processes a bean property as a XML element
	 * @param metadata
	 * @param xmlBeanWrapperContext
	 * @param currentNode
	 */
	protected void processElement(JuffrouBeanMetadata metadata, BeanClassBinding xmlBeanWrapperContext, Node currentNode, NodeType nodeType) {
		
		// read tag attributes
		BeanPropertyBinding propertyBinding = new BeanPropertyBinding();
		propertyBinding.setNodeType(nodeType);
		
		NamedNodeMap attributes = currentNode.getAttributes();
		Node attribute = attributes.getNamedItem(PROPERTY_ELEMENT_NODENAME);
		propertyBinding.setBeanPropertyName(attribute.getNodeValue());
		
		// is the property is a node of type TEXT then its xml element name will be CDATA
		if(nodeType == NodeType.TEXT)
			propertyBinding.setXmlElementName(XmlConstants.CDATA_ELEMENT_NAME);
		else {
			attribute = attributes.getNamedItem(XML_ELEMENT_NODENAME);
			if(attribute != null)
				propertyBinding.setXmlElementName(attribute.getNodeValue());
		}

		attribute = attributes.getNamedItem(TYPE_ELEMENT_NODENAME);
		if(attribute != null) {
			Class<?> clazz;
			try {
				clazz = Class.forName(attribute.getNodeValue());
			} catch (ClassNotFoundException e) {
				throw new XmlMappingReaderException(e);
			}
			propertyBinding.setPropertyType(clazz);
		}
		
		// read tag content
		currentNode = currentNode.getFirstChild();
		while(currentNode != null) {
			if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
				if(currentNode.getNodeName().equals(XML_ELEMENT_NODENAME))
					processElementXml(metadata, propertyBinding, currentNode);
				else if(currentNode.getNodeName().equals(SERIALIZER_ELEMENT_NODENAME))
					propertyBinding.setSerializer(processSerializer(metadata, currentNode));
			}
			currentNode = currentNode.getNextSibling();
		}

		// if the XML element name was not defined, use the property name
		if(propertyBinding.getXmlElementName() == null)
			propertyBinding.setXmlElementName(propertyBinding.getBeanPropertyName());
		
		// if the property type was not defined, use reflection with the help of a bean wrapper
		if(propertyBinding.getPropertyType() == null) {
			JuffrouBeanWrapper bw = new JuffrouBeanWrapper(xmlBeanWrapperContext);
			Class<?> clazz = bw.getClazz(propertyBinding.getBeanPropertyName());
			propertyBinding.setPropertyType(clazz);
		}

		xmlBeanWrapperContext.addBeanPropertyBinding(propertyBinding);
	}

	protected void processElementXml(JuffrouBeanMetadata metadata, BeanPropertyBinding propertyBinding, Node currentNode) {
		NamedNodeMap attributes = currentNode.getAttributes();
		Node attribute = attributes.getNamedItem(ID_ELEMENT_NODENAME);
		propertyBinding.setXmlElementName(attribute.getNodeValue());
	}

	protected Serializer processSerializer(JuffrouBeanMetadata metadata, Node currentNode) {
		// read tag attributes
		String serializerId = null;
		NamedNodeMap attributes = currentNode.getAttributes();

		Node attribute = attributes.getNamedItem(REF_ELEMENT_NODENAME);
		if(attribute != null)
			return metadata.getSerializerWithId(attribute.getNodeValue());

		attribute = attributes.getNamedItem(ID_ELEMENT_NODENAME);
		if(attribute != null)
			serializerId = attribute.getNodeValue();

		attribute = attributes.getNamedItem(CLASS_ELEMENT_NODENAME);
		if(attribute == null)
			throw new XmlMappingReaderException("Serializer must have a 'class' or 'ref' atribute");
		
		Class<?> clazz;
		try {
			clazz = Class.forName(attribute.getNodeValue());
		} catch (ClassNotFoundException e) {
			throw new XmlMappingReaderException("Serializer class not found " + attribute.getNodeValue(), e);
		}
		
		BeanWrapperContext bwContext = BeanWrapperContext.create(clazz);
//		bwContext.setEagerInstatiation(true);
		JuffrouBeanWrapper serializerWrapper = new JuffrouBeanWrapper(bwContext);

		// read tag content
		currentNode = currentNode.getFirstChild();
		while(currentNode != null) {
			if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
				serializerWrapper.setValueOfString(currentNode.getNodeName(), currentNode.getTextContent());
			}
			currentNode = currentNode.getNextSibling();
		}
		
		Serializer serializer = (Serializer) serializerWrapper.getBean();
		
		if(serializerId != null)
			metadata.registerSerializer(serializerId, serializer);
		
		return serializer;
	}
}
