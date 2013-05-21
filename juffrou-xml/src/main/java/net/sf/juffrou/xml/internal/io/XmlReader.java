package net.sf.juffrou.xml.internal.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XmlReader implements JuffrouReader {

	private Document doc;
	private Node currentNode = null;
	private Stack<Node> parentNodes = new Stack<Node>();
	private Deque<Node> attributeNodes = new ArrayDeque<Node>();
	
	public XmlReader(InputStream xml) {
        
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(xml);
	        // normalize text representation
			doc.getDocumentElement ().normalize();
			currentNode = doc;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String next() {
		if(currentNode == null)
			return null;
		if( ! attributeNodes.isEmpty()) {
			return attributeNodes.removeFirst().getNodeName();
		}
		
		currentNode = currentNode.getNextSibling();
		while(currentNode != null && currentNode.getNodeType() != Node.ELEMENT_NODE && currentNode.getNodeType() != Node.ATTRIBUTE_NODE)
			currentNode = currentNode.getNextSibling();

		if(currentNode != null) {
			NamedNodeMap attributes = currentNode.getAttributes();
			for(int i=0; i < attributes.getLength(); i++)
				attributeNodes.add(attributes.item(i));
			
			return currentNode.getNodeName();
		}
		
		return null;
	}
	
	public String enterNode() {
		parentNodes.push(currentNode);
		currentNode = currentNode.getFirstChild();
		while(currentNode != null && currentNode.getNodeType() != Node.ELEMENT_NODE)
			currentNode = currentNode.getNextSibling();
		attributeNodes.clear();
		if(currentNode != null) {
			NamedNodeMap attributes = currentNode.getAttributes();
			for(int i=0; i < attributes.getLength(); i++)
				attributeNodes.add(attributes.item(i));
		}
		return currentNode != null ? currentNode.getNodeName() : null;
	}

	public void exitNode() {
		currentNode = parentNodes.pop();
	}

	public String getText() {
		if(currentNode == null)
			return null;
		if(currentNode.getNodeType() == Node.ELEMENT_NODE)
			return currentNode.getTextContent();
		else if(currentNode.getNodeType() == Node.ATTRIBUTE_NODE) {
			return currentNode.getNodeValue();
		}
		else
			return null;
	}
	
	@Override
	public String getNodeName() {
		return currentNode != null ? currentNode.getNodeName() : null;
	}
}
