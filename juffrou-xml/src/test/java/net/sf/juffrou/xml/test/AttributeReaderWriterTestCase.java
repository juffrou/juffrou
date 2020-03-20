package net.sf.juffrou.xml.test;

import net.sf.juffrou.xml.JuffrouXml;
import net.sf.juffrou.xml.test.dom.Person;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AttributeReaderWriterTestCase {
	
	
	@Test
	public void testAttributesRoundTrip() {
		JuffrouXml juffrouXml = new JuffrouXml("classpath:attributes-xml-mapping.xml");
		
		
		Person person = new Person();
		person.setFirstName("Carlos");
		person.setLastName("Martins");
		try {
			person.setBirthDay(new SimpleDateFormat("yyyy-MM-dd").parse("1967-10-01"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		String xmlString = juffrouXml.toXml(person);
		
		System.out.println(xmlString);

		Object object = juffrouXml.fromXml(xmlString);
		Assert.assertTrue(object instanceof Person);
		Person newPerson = (Person) object;
		Assert.assertEquals("Carlos", newPerson.getFirstName());
		Assert.assertEquals("Martins", newPerson.getLastName());
	}
}
