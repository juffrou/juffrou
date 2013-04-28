package org.juffrou.xml.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.util.reflect.BeanWrapperContext;
import org.juffrou.xml.internal.JuffrouMarshaller;
import org.juffrou.xml.internal.io.JuffrouWriter;
import org.juffrou.xml.internal.io.XmlReader;
import org.juffrou.xml.internal.io.XmlWriter;
import org.juffrou.xml.test.dom.Country;
import org.juffrou.xml.test.dom.Person;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BasicXmlTestCase {
	
	private Country country;
	
	@Before
	public void setup() {
		country = new Country();
		try {
			country.setFounded(new SimpleDateFormat("yyyy-MM-dd").parse("1147-01-01"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		country.setName("Portugal");
		Person president = new Person();
		president.setFirstName("Jorge");
		president.setLastName("Sampaio");
		country.setPresident(president);
	}

	@Test
	public void testSimpleRoundetrip() {
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
	
	@Test
	public void testMarshallCountry() {
		JuffrouMarshaller marshaller = new JuffrouMarshaller();
		JuffrouWriter writer = new XmlWriter();
		marshaller.marshallBean(writer, country);
		String xmlString = writer.toString();
		System.out.println(xmlString);
	}
}
