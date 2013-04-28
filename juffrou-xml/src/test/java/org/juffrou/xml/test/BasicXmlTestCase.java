package org.juffrou.xml.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.util.reflect.BeanWrapperContext;
import org.juffrou.xml.internal.JuffrouMarshaller;
import org.juffrou.xml.internal.io.JuffrouWriter;
import org.juffrou.xml.internal.io.XmlReader;
import org.juffrou.xml.internal.io.XmlWriter;
import org.junit.Assert;
import org.junit.Test;

public class BasicXmlTestCase {

	@Test
	public void testMarshallBean() {
		BeanWrapperContext bwc = new BeanWrapperContext(Person.class);
		BeanWrapper bw = new BeanWrapper(bwc);
		bw.setValue("firstName", "Carlos");
		bw.setValue("lastName", "Martins");
		
		JuffrouMarshaller marshaller = new JuffrouMarshaller();
		JuffrouWriter writer = new XmlWriter();
		marshaller.marshallBean(writer, bw.getBean());
		
		String xmlString = writer.toString();
		System.out.println(xmlString);
		Object unmarshall = null;
		try {
			InputStream stream = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
			XmlReader reader = new XmlReader(stream);
			
			unmarshall = marshaller.unmarshallBean(reader);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		Assert.assertTrue(unmarshall instanceof Person);
	}
}
