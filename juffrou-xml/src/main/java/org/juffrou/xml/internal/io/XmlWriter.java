package org.juffrou.xml.internal.io;

<<<<<<< HEAD
=======
import java.io.File;
>>>>>>> origin/master
import java.io.StringWriter;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

<<<<<<< HEAD
=======
import org.w3c.dom.Attr;
>>>>>>> origin/master
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
	
	public void startNode(String nodeName) {
		Element element = doc.createElement(nodeName);
		parentNode.appendChild(element);
		grandParents.push(parentNode);
		parentNode = element;
	}
	
	public void endNode() {
		parentNode = grandParents.pop();
	}
	
	public void write(String value) {
		parentNode.appendChild(doc.createTextNode(value));
	}
	
	public String toString() {
		try {
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
			transformer = transformerFactory.newTransformer();
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
<<<<<<< HEAD
=======

	public void test() {

		try {
			Element rootElement = doc.createElement("company");
			doc.appendChild(rootElement);

			// staff elements
			Element staff = doc.createElement("Staff");
			rootElement.appendChild(staff);

			// set attribute to staff element
			Attr attr = doc.createAttribute("id");
			attr.setValue("1");
			staff.setAttributeNode(attr);

			// shorten way
			// staff.setAttribute("id", "1");

			// firstname elements
			Element firstname = doc.createElement("firstname");
			firstname.appendChild(doc.createTextNode("yong"));
			staff.appendChild(firstname);

			// lastname elements
			Element lastname = doc.createElement("lastname");
			lastname.appendChild(doc.createTextNode("mook kim"));
			staff.appendChild(lastname);

			// nickname elements
			Element nickname = doc.createElement("nickname");
			nickname.appendChild(doc.createTextNode("mkyong"));
			staff.appendChild(nickname);

			// salary elements
			Element salary = doc.createElement("salary");
			salary.appendChild(doc.createTextNode("100000"));
			staff.appendChild(salary);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			StreamResult result = new StreamResult(new File("C:\\testing.xml"));
			transformer.transform(source, result);

			System.out.println("Done");

		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
>>>>>>> origin/master
}
