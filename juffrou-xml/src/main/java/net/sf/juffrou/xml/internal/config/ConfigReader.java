package net.sf.juffrou.xml.internal.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.juffrou.xml.JuffrouXmlException;
import net.sf.juffrou.xml.internal.JuffrouBeanMetadata;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Reads the mapping file and sets the metadata values
 * 
 * @author cemartins
 */
public class ConfigReader {

	public void readConfigFile(JuffrouBeanMetadata metadata, String fileName) {
		try {
			InputStream stream = new FileInputStream(fileName);
			Document doc;
			Node currentNode = null;
			
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(stream);
	        // normalize text representation
			doc.getDocumentElement ().normalize();
			currentNode = doc;
			
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
}
