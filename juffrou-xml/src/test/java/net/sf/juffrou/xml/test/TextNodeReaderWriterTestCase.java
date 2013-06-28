package net.sf.juffrou.xml.test;

import net.sf.juffrou.xml.JuffrouXml;
import net.sf.juffrou.xml.test.dom.Person;

import org.junit.Assert;
import org.junit.Test;

public class TextNodeReaderWriterTestCase {
	
	
	@Test
	public void testTextNodeRoundTrip() {
		JuffrouXml juffrouXml = new JuffrouXml("classpath:text-node-xml-mapping.xml");
		
		
		Person person = new Person();
		person.setFirstName("Carlos");
		
		String xmlString = juffrouXml.toXml(person);
		
		System.out.println(xmlString);

		Object object = juffrouXml.fromXml(xmlString);
		Assert.assertTrue(object instanceof Person);
		Person newPerson = (Person) object;
		Assert.assertEquals("Carlos", newPerson.getFirstName());
	}
}
