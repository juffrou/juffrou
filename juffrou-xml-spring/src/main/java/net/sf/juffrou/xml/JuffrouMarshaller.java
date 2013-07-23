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

import net.sf.juffrou.xml.internal.config.ConfigReader;
import net.sf.juffrou.xml.internal.config.JuffrouSpringConfigReader;
import net.sf.juffrou.xml.internal.io.XmlReader;
import net.sf.juffrou.xml.internal.io.XmlWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.support.AbstractMarshaller;
import org.springframework.util.ObjectUtils;
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

/**
 * 
 * @author Carlos Martins
 */
public class JuffrouMarshaller extends AbstractMarshaller implements ApplicationContextAware, InitializingBean {

	public static final String DEFAULT_ENCODING = "UTF-8";

	protected final Log logger = LogFactory.getLog(getClass());

	private JuffrouXml juffrouXml = null;
	
	private ApplicationContext applicationContext;
	
	private Resource[] mappingLocations;
	
	private String encoding = DEFAULT_ENCODING;
	
	/**
	 * Set the locations of the Juffrou XML Mapping files.
	 */
	public void setMappingLocation(Resource mappingLocation) {
		this.mappingLocations = new Resource[]{mappingLocation};
	}

	/**
	 * Set the locations of the Juffrou XML Mapping files.
	 */
	public void setMappingLocations(Resource[] mappingLocations) {
		this.mappingLocations = mappingLocations;
	}

	public JuffrouXml getJuffrouXml() {
		return juffrouXml;
	}

	public void setJuffrouXml(JuffrouXml juffrouXml) {
		this.juffrouXml = juffrouXml;
	}

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

	@Override
	public void afterPropertiesSet() throws Exception {
		
		if(juffrouXml == null)
			juffrouXml = new JuffrouXml();
		
		if (!ObjectUtils.isEmpty(mappingLocations)) {
			ConfigReader configReader = new JuffrouSpringConfigReader(applicationContext);
			for (Resource mappingLocation : mappingLocations) {
				try {
					InputStream inputStream = mappingLocation.getInputStream();
					juffrouXml.readConfigFile(configReader, inputStream);
					inputStream.close();
				}
				catch(RuntimeException e) {
					if(logger.isErrorEnabled())
						logger.error("Cannot read configuration file " + mappingLocation.toString(), e);
				}
			}
		}
		else {
			if(logger.isWarnEnabled())
				logger.warn("No mapping location defined. Using default configuration for JuffrouXml.");
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		
		this.applicationContext = applicationContext;
		
	}

}
