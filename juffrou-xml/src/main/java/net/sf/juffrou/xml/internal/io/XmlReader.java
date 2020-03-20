package net.sf.juffrou.xml.internal.io;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

public class XmlReader implements JuffrouReader {

	private Document doc;
	private Node currentNode = null;
	private Stack<Node> parentNodes = new Stack<Node>();
	private Deque<Node> parentAttributes = new ArrayDeque<Node>();
	
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

	public XmlReader(InputSource xml) {
        
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

	public XmlReader(Node node) {
		this.currentNode = node;
		if (node instanceof Document) {
			doc = (Document) node;
		}
		else {
			doc = node.getOwnerDocument();
		}
	}
	
	public String next() {
		if( ! parentAttributes.isEmpty()) {
			currentNode = parentAttributes.removeFirst();
			return currentNode.getNodeName();
		}

		if(currentNode == null)
			return null;

		currentNode = currentNode.getNextSibling();
		while(currentNode != null && currentNode.getNodeType() != Node.ELEMENT_NODE)
			currentNode = currentNode.getNextSibling();

		return currentNode == null ? null : currentNode.getNodeName();
	}
	
	public String enterNode() {
		parentAttributes.clear();
		NamedNodeMap attributes = currentNode.getAttributes();
		if(attributes != null)
			for(int i=0; i < attributes.getLength(); i++)
				parentAttributes.add(attributes.item(i));

		parentNodes.push(currentNode);
		currentNode = currentNode.getFirstChild();
		Node textChild = null;
		if(currentNode != null && currentNode.getNodeType() == Node.TEXT_NODE) {
			textChild = currentNode;
		}
		while(currentNode != null && currentNode.getNodeType() != Node.ELEMENT_NODE)
			currentNode = currentNode.getNextSibling();

		if(currentNode == null)
			currentNode = textChild;
		if(parentAttributes.isEmpty())
			return currentNode != null ? currentNode.getNodeName() : null;
		else {
			parentAttributes.add(currentNode);
			currentNode = parentAttributes.removeFirst();
			return currentNode.getNodeName();
		}
	}

	public void exitNode() {
		parentAttributes.clear();
		currentNode = parentNodes.pop();
	}

	public String getText() {
		if(currentNode == null)
			return null;
		
		String nodeText;
		switch(currentNode.getNodeType()) {
		case Node.ELEMENT_NODE:
			nodeText = currentNode.getTextContent();
			break;
		case Node.ATTRIBUTE_NODE:
			nodeText = currentNode.getNodeValue();
			break;
		case Node.TEXT_NODE:
			nodeText = currentNode.getNodeValue();
			break;
			default:
				nodeText = null;
		}
		return nodeText;
	}
	
	@Override
	public String getNodeName() {
		return currentNode != null ? currentNode.getNodeName() : null;
	}
}
