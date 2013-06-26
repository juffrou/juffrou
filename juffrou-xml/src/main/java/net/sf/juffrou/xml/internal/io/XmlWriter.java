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
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

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

	public XmlWriter(Node node) {
		this.parentNode = node;
		if (node instanceof Document) {
			doc = (Document) node;
		}
		else {
			doc = node.getOwnerDocument();
		}
	}

	public void startNode(String nodeName, NodeType nodeType) {
		Node node = null;
		switch(nodeType) {
		case ATTRIBUTE:
			node = doc.createAttribute(nodeName);
			((Element)parentNode).setAttributeNode((Attr) node);
			break;
		case TEXT:
			node = doc.createTextNode("");
			parentNode.appendChild(node);
			break;
		case ELEMENT:
			node = doc.createElement(nodeName);
			parentNode.appendChild(node);
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
	
	public void setProcessingInstruction(String target, String data) throws SAXException {
		ProcessingInstruction pi = doc.createProcessingInstruction(target, data);
		parentNode.appendChild(pi);
	}

	public void write(String value) {
		
		if (parentNode.getNodeType() == Node.TEXT_NODE)
			((Text) parentNode).appendData(value);
		else if(parentNode.getNodeType() == Node.ATTRIBUTE_NODE)
			parentNode.setNodeValue(value);
		else
			parentNode.appendChild(doc.createTextNode(value));
	}
	
	public Transformer getTransformer() {

		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			return transformer;
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String toString() {
		try {
		// write the content into xml file
			Transformer transformer = getTransformer();
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
	
	public DOMSource getSource() {
		
		DOMSource source = new DOMSource(doc);
		return source;
	}
	
}
