package net.sf.juffrou.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.juffrou.xml.internal.io.XmlReader;
import net.sf.juffrou.xml.internal.io.XmlWriter;

import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.support.AbstractMarshaller;
import org.springframework.util.xml.StaxUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class JuffrouMarshaller extends AbstractMarshaller {

	public static final String DEFAULT_ENCODING = "UTF-8";

	private JuffrouXml juffrouXml;
	
	private String encoding = DEFAULT_ENCODING;
	
	
	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	protected void marshalDomNode(Object graph, Node node) throws XmlMappingException {

		XmlWriter writer = new XmlWriter(node);
		juffrouXml.getXmlMarshaller().marshallBean(writer, graph);
	}

	@Override
	protected void marshalXmlEventWriter(Object graph, XMLEventWriter eventWriter) throws XmlMappingException {
		marshalSaxHandlers(graph, StaxUtils.createContentHandler(eventWriter), null);
		
	}

	@Override
	protected void marshalXmlStreamWriter(Object graph, XMLStreamWriter streamWriter) throws XmlMappingException {
		marshalSaxHandlers(graph, StaxUtils.createContentHandler(streamWriter), null);
		
	}

	@Override
	protected void marshalOutputStream(Object graph, OutputStream outputStream)	throws XmlMappingException, IOException {
		marshalWriter(graph, new OutputStreamWriter(outputStream, encoding));
		
	}

	@Override
	protected void marshalSaxHandlers(Object graph,	ContentHandler contentHandler, LexicalHandler lexicalHandler) throws XmlMappingException {

		XmlWriter jwriter = new XmlWriter();
		juffrouXml.getXmlMarshaller().marshallBean(jwriter, graph);

		DOMSource source = jwriter.getSource();
		Node node = source.getNode();

		try {
			contentHandler.startDocument();
			
			fillContentHandlerFomNode(contentHandler, node);
			
			contentHandler.endDocument();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	private void fillContentHandlerFomNode(ContentHandler contentHandler, Node node) throws SAXException {
		
		while(node != null) {
			contentHandler.startElement(node.getBaseURI(), node.getLocalName(), node.getNodeName(), getAttributesFormNamedNodeMap(node.getAttributes()));
			fillContentHandlerFomNode(contentHandler, node.getFirstChild());
			contentHandler.endElement(node.getBaseURI(), node.getLocalName(), node.getNodeName());
			node = node.getNextSibling();
		}
	}
	
	private Attributes getAttributesFormNamedNodeMap(NamedNodeMap namedNodeMap) {
		AttributesImpl attributes = new AttributesImpl();
		for(int i = 0; i < namedNodeMap.getLength(); i++) {
			Node item = namedNodeMap.item(i);
			attributes.addAttribute(item.getBaseURI(), item.getLocalName(), item.getNodeName(), "CDATA", item.getNodeValue());
		}
		return attributes;
	}

	@Override
	protected void marshalWriter(Object graph, Writer writer) throws XmlMappingException, IOException {
		
		XmlWriter jwriter = new XmlWriter();
		juffrouXml.getXmlMarshaller().marshallBean(jwriter, graph);

		
		Transformer transformer = jwriter.getTransformer();
		DOMSource source = jwriter.getSource();

		StreamResult result = new StreamResult(writer);
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Object unmarshalDomNode(Node node) throws XmlMappingException {
		
		XmlReader xmlReader = new XmlReader(node);
		Object unmarshallBean = juffrouXml.getXmlMarshaller().unmarshallBean(xmlReader);
		return unmarshallBean;
	}

	@Override
	protected Object unmarshalXmlEventReader(XMLEventReader eventReader) throws XmlMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object unmarshalXmlStreamReader(XMLStreamReader streamReader) throws XmlMappingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object unmarshalInputStream(InputStream inputStream) throws XmlMappingException, IOException {

		XmlReader xmlReader = new XmlReader(inputStream);
		Object unmarshallBean = juffrouXml.getXmlMarshaller().unmarshallBean(xmlReader);
		return unmarshallBean;
	}

	@Override
	protected Object unmarshalReader(Reader reader) throws XmlMappingException,	IOException {
		XmlReader xmlReader = new XmlReader(new InputSource(reader));
		Object unmarshallBean = juffrouXml.getXmlMarshaller().unmarshallBean(xmlReader);
		return unmarshallBean;
	}

	@Override
	protected Object unmarshalSaxReader(XMLReader xmlReader, InputSource inputSource) throws XmlMappingException, IOException {
		XmlReader reader = new XmlReader(inputSource);
		Object unmarshallBean = juffrouXml.getXmlMarshaller().unmarshallBean(reader);
		return unmarshallBean;
	}

}
