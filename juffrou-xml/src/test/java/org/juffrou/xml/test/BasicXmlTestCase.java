package org.juffrou.xml.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

import com.thoughtworks.xstream.XStream;

public class BasicXmlTestCase {
	
	private Country country;
	
	@Before
	public void setup() {
		country = new Country();
		try {
			country.setFounded(new SimpleDateFormat("yyyy-MM-dd").parse("1147-01-01"));
		} catch (ParseException e) {
		}
		country.setName("Portugal");
		Person president = new Person();
		president.setFirstName("Jorge");
		president.setLastName("Sampaio");
		country.setPresident(president);
		String[] provinces = new String[] {"Estremadura", "Alentejo", "Algarve", "Beira Baixa", "Beira Alta", "Ribatejo", "Douro", "Minho", "Trás os Montes"};
		country.setProvinces(Arrays.asList(provinces));
		Set<Person> people = new HashSet<Person>();
		people.add(president);
		country.setPeople(people);
		Person carlos = new Person();
		try {
			carlos.setBirthDay(new SimpleDateFormat("yyyy-MM-dd").parse("1967-10-01"));
		} catch (ParseException e) {
		}
		carlos.setFirstName("Carlos");
		carlos.setLastName("Martins");
		people.add(carlos);
		Map<String, Person> partyLeaders = new HashMap<String,Person>();
		partyLeaders.put("PS", president);
		country.setPartyLeaders(partyLeaders);
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

	@Test
	public void testXStreamMarshallCountry() {
		XStream xstream = new XStream();
		xstream.setMode(XStream.NO_REFERENCES);
		String xmlString = xstream.toXML(country);
		System.out.println(xmlString);
	}

	@Test
	public void unmarshalCountry() {

		JuffrouMarshaller marshaller = new JuffrouMarshaller();
		marshaller.getJuffrouBeanMetadata().getBeanClassBindingFromClass(country);
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><org.juffrou.xml.test.dom.Country><founded>1147-01-01</founded><president><lastName>Sampaio</lastName><firstName>Jorge</firstName></president><name>Portugal</name><partyLeaders><entry><string>PS</string><org.juffrou.xml.test.dom.Person><lastName>Sampaio</lastName><firstName>Jorge</firstName></org.juffrou.xml.test.dom.Person></entry></partyLeaders><provinces><string>Estremadura</string><string>Alentejo</string><string>Algarve</string><string>Beira Baixa</string><string>Beira Alta</string><string>Ribatejo</string><string>Douro</string><string>Minho</string><string>Trás os Montes</string></provinces><people><org.juffrou.xml.test.dom.Person><lastName>Sampaio</lastName><firstName>Jorge</firstName></org.juffrou.xml.test.dom.Person><org.juffrou.xml.test.dom.Person><lastName>Martins</lastName><birthDay>1967-10-01</birthDay><firstName>Carlos</firstName></org.juffrou.xml.test.dom.Person></people></org.juffrou.xml.test.dom.Country>";
		
		Object unmarshall = null;
		try {
			InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			XmlReader reader = new XmlReader(stream);
			
			unmarshall = marshaller.unmarshallBean(reader);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		Assert.assertTrue(unmarshall instanceof Country);

	}
}
