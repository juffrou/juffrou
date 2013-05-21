package net.sf.juffrou.xml.internal.io;

import java.io.StringWriter;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.juffrou.xml.internal.NodeType;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XmlWriter implements JuffrouWriter {
	
	private Document doc;
	private Node parentNode;
	private Stack<Node> grandParents = new Stack<Node>();
	
	public XmlWriter() {
		
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			doc = docBuilder.newDocument();
			parentNode = doc;
		}
		catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
	}
	
	public void startNode(String nodeName, NodeType nodeType) {
		Node node;
		if(nodeType == NodeType.ELEMENT) {
			node = doc.createElement(nodeName);
			parentNode.appendChild(node);
		}
		else {
			node = doc.createAttribute(nodeName);
			((Element)parentNode).setAttributeNode((Attr) node);
		}
		grandParents.push(parentNode);
		parentNode = node;
	}
	
	public void endNode() {
		parentNode = grandParents.pop();
	}
	
	public void setAttribute(String attributeName, String value) {
		((Element)parentNode).setAttribute(attributeName, value);
	}
	
	public void write(String value) {
		if(parentNode.getNodeType() == Node.ATTRIBUTE_NODE)
			parentNode.setNodeValue(value);
		else
			parentNode.appendChild(doc.createTextNode(value));
	}
	
	public String toString() {
		try {
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);

		StreamResult result = new StreamResult(new StringWriter());
		transformer.transform(source, result);
		return result.getWriter().toString();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}
}
