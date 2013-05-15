package net.sf.juffrou.xml.internal.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.juffrou.xml.JuffrouXmlException;
import net.sf.juffrou.xml.error.XmlMappingReaderException;
import net.sf.juffrou.xml.internal.JuffrouBeanMetadata;
import net.sf.juffrou.xml.internal.binding.BeanClassBinding;
import net.sf.juffrou.xml.internal.config.protocols.classpath.Handler;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Reads the mapping file and sets the metadata values
 * 
 * @author cemartins
 */
public class ConfigReader {
	
	private static String PREFERENCES_NODENAME = "preferences";
	private static String ROOT_ELEMENT_NODENAME = "root-element";
	private static String TYPE_ELEMENT_NODENAME = "type";
	private static String XML_ELEMENT_NODENAME = "xml";
	private static String NAME_ELEMENT_NODENAME = "name";

	public static void readConfigFile(JuffrouBeanMetadata metadata, String urlSpec) {
		try {
			URL url = new URL(null, urlSpec, new Handler(ClassLoader.getSystemClassLoader()));
			InputStream stream = url.openStream();
//			InputStream stream = ConfigReader.class.getResourceAsStream(fileName);
			Document doc;
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(stream);
	        // normalize text representation
			doc.getDocumentElement().normalize();
			
			processMappingElement(metadata, doc.getFirstChild());
			
		} catch (UnsupportedEncodingException e) {
			throw new JuffrouXmlException("Cannot create a reader of the XML string passed", e);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static void processMappingElement(JuffrouBeanMetadata metadata, Node currentNode) {
		
		currentNode = currentNode.getFirstChild();
		while(currentNode != null) {
			if(currentNode.getNodeType() == Node.ELEMENT_NODE) {
				if(currentNode.getNodeName().equals(ROOT_ELEMENT_NODENAME))
					processRootElement(metadata, currentNode);
				else if(currentNode.getNodeName().equals(PREFERENCES_NODENAME))
					processPreferences(metadata, currentNode);
			}
			currentNode = currentNode.getNextSibling();
		}
	}
	
	private static void processPreferences(JuffrouBeanMetadata metadata, Node currentNode) {
		
		// read tag attributes
		
		// read tag content
		currentNode = currentNode.getFirstChild();
		while(currentNode != null) {
			if(currentNode.getNodeName().equals(PREFERENCES_NODENAME))
				processPreferences(metadata, currentNode);
			currentNode = currentNode.getNextSibling();
		}
		
	}
	
	
	private static void processRootElement(JuffrouBeanMetadata metadata, Node currentNode) {
		
		// read tag attributes
		NamedNodeMap attributes = currentNode.getAttributes();
		Node attribute = attributes.getNamedItem(TYPE_ELEMENT_NODENAME);
		Class<?> clazz;
		try {
			clazz = Class.forName(attribute.getNodeValue());
		} catch (ClassNotFoundException e) {
			throw new XmlMappingReaderException(e);
		}

		BeanClassBinding xmlBeanWrapperContext = new BeanClassBinding(clazz);
		attribute = attributes.getNamedItem(XML_ELEMENT_NODENAME);
		if(attribute != null)
			xmlBeanWrapperContext.setXmlElementName(attribute.getNodeValue());

		// read tag content
		currentNode = currentNode.getFirstChild();
		while(currentNode != null) {
			if(currentNode.getNodeName().equals(XML_ELEMENT_NODENAME))
				processRootElementXml(metadata, xmlBeanWrapperContext, currentNode);
			currentNode = currentNode.getNextSibling();
		}
		
		metadata.getXmlBeanWrapperContextCreator().registerBeanClassBinding(xmlBeanWrapperContext);
	}

	private static void processRootElementXml(JuffrouBeanMetadata metadata, BeanClassBinding xmlBeanWrapperContext, Node currentNode) {
		NamedNodeMap attributes = currentNode.getAttributes();
		Node attribute = attributes.getNamedItem(NAME_ELEMENT_NODENAME);
		xmlBeanWrapperContext.setXmlElementName(attribute.getNodeValue());
	}
}
