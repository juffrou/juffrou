package net.sf.juffrou.xml.test;

import com.thoughtworks.xstream.XStream;
import net.sf.juffrou.xml.JuffrouXml;
import net.sf.juffrou.xml.test.dom.Address;
import net.sf.juffrou.xml.test.dom.Country;
import net.sf.juffrou.xml.test.dom.Person;
import net.sf.juffrou.xml.test.dom.SimpleDateSerializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
		
		Person cunhal = new Person();
		cunhal.setFirstName("Alvaro");
		cunhal.setLastName("Cunhal");
		people.add(cunhal);
		partyLeaders.put("PCP", cunhal);
		
		country.setPeople(people);
		country.setPartyLeaders(partyLeaders);
	}

	@Test
	public void testSimpleRoundetrip() {
		
		Person person = new Person();
		person.setFirstName("Carlos");
		person.setLastName("Martins");
		try {
			person.setBirthDay(new SimpleDateFormat("yyyy-MM-dd").parse("1967-10-01"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JuffrouXml juffrouXml = new JuffrouXml();
		
		String xmlString = juffrouXml.toXml(person);
		System.out.println(xmlString);
		Object object = juffrouXml.fromXml(xmlString);
		Assert.assertTrue(object instanceof Person);
		Person newPerson = (Person) object;
		Assert.assertEquals("Carlos", newPerson.getFirstName());
		Assert.assertEquals("Martins", newPerson.getLastName());
	}
	
	@Test
	public void testTwoClassesRoundTrip() {
		
		Person person = new Person();
		person.setFirstName("Carlos");
		person.setLastName("Martins");
		try {
			person.setBirthDay(new SimpleDateFormat("yyyy-MM-dd").parse("1967-10-01"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Address address = new Address();
		address.setStreet("Bean street, No 1");
		address.setCity("Lisboa");
		
		person.setHome(address);
		
		JuffrouXml juffrouXml = new JuffrouXml();
		
		String xmlString = juffrouXml.toXml(person);
		System.out.println(xmlString);

	}

	@Test
	public void testRegisterElement() {
		
		Person person = new Person();
		person.setFirstName("Carlos");
		person.setLastName("Martins");
		try {
			person.setBirthDay(new SimpleDateFormat("yyyy-MM-dd").parse("1967-10-01"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Address address = new Address();
		address.setStreet("Bean street, No 1");
		address.setCity("Lisboa");
		
		person.setHome(address);
		
		JuffrouXml juffrouXml = new JuffrouXml();
		juffrouXml.registerRootElement(Person.class, "Person");
		juffrouXml.registerSerializer("simpledate", new SimpleDateSerializer());
		juffrouXml.registerElement(Person.class, "birthDay", "birthDay", "simpledate");
		
		String xmlString = juffrouXml.toXml(person);
		System.out.println(xmlString);

	}

	@Test
	public void testNestedProperty() {
		
		Person person = new Person();
		person.setFirstName("Carlos");
		person.setLastName("Martins");
		try {
			person.setBirthDay(new SimpleDateFormat("yyyy-MM-dd").parse("1967-10-01"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Address address = new Address();
		address.setStreet("Bean street, No 1");
		address.setCity("Lisboa");
		
		person.setHome(address);
		
		JuffrouXml juffrouXml = new JuffrouXml();
		juffrouXml.registerRootElement(Person.class, "Person");
		juffrouXml.registerElement(Person.class, "home.city", "homeTown", null);
		
		String xmlString = juffrouXml.toXml(person);
		System.out.println(xmlString);

	}

	@Test
	public void testMarshallCountry() {
		JuffrouXml juffrouXml = new JuffrouXml();
		
		String xmlString = juffrouXml.toXml(country);
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

		JuffrouXml juffrouXml = new JuffrouXml();
		
		String xml = juffrouXml.toXml(country);
		
		Object object = juffrouXml.fromXml(xml);
		Assert.assertTrue(object instanceof Country);
		Country unmarshalledCountry = (Country) object;
		Assert.assertEquals(country.getName(), unmarshalledCountry.getName());
		Assert.assertEquals(country.getFounded(), unmarshalledCountry.getFounded());
		Assert.assertEquals(country.getPartyLeaders().size(), unmarshalledCountry.getPartyLeaders().size());
		Assert.assertEquals(country.getPeople().size(), unmarshalledCountry.getPeople().size());
		Assert.assertEquals(country.getProvinces().size(), unmarshalledCountry.getProvinces().size());
	}
}
