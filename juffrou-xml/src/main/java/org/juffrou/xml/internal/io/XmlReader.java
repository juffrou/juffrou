package org.juffrou.xml.internal.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
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
			currentNode = doc.getFirstChild();
		else
			currentNode = currentNode.getNextSibling();
		return currentNode.getNodeName();
	}
	
	public String enterNode() {
		currentNode = currentNode.getFirstChild();
		return currentNode.getNodeName();
	}

	public void exitNode() {
		currentNode = currentNode.getParentNode();
	}

	public String getValue() {
		currentNode = currentNode.getFirstChild();
		return currentNode.getNodeName();
	}
}
