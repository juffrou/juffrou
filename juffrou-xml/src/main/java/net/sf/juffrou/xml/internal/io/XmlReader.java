package net.sf.juffrou.xml.internal.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
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
		currentNode = currentNode.getNextSibling();
		while(currentNode != null && currentNode.getNodeType() != Node.ELEMENT_NODE)
			currentNode = currentNode.getNextSibling();
		return currentNode != null ? currentNode.getNodeName() : null;
	}
	
	public String enterNode() {
		parentNodes.push(currentNode);
		currentNode.getNodeType();
		currentNode = currentNode.getFirstChild();
		while(currentNode != null && currentNode.getNodeType() != Node.ELEMENT_NODE)
			currentNode = currentNode.getNextSibling();
		return currentNode != null ? currentNode.getNodeName() : null;
	}

	public void exitNode() {
		currentNode = parentNodes.pop();
	}

	public Map<String, Object> getAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();
		NamedNodeMap nodeMap = currentNode.getAttributes();
		for(int i = 0; i < nodeMap.getLength(); i++) {
			Node item = nodeMap.item(i);
			attributes.put(item.getNodeName(), item.getNodeValue());
		}
		return attributes;
	}
	
	public String getText() {
		return currentNode != null ? currentNode.getTextContent() : null;
	}
	
	@Override
	public String getNodeName() {
		return currentNode != null ? currentNode.getNodeName() : null;
	}
}
