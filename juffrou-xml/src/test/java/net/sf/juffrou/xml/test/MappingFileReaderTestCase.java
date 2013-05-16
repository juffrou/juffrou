package net.sf.juffrou.xml.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.juffrou.xml.JuffrouXml;
import net.sf.juffrou.xml.test.dom.Country;
import net.sf.juffrou.xml.test.dom.Person;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MappingFileReaderTestCase {
	
	private static String COUNTRY_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><Country><founded>1147-01-01</founded><president><lastName>Sampaio</lastName><firstName>Jorge</firstName></president><name>Portugal</name><partyLeaders><entry><string>PS</string><Person><lastName>Sampaio</lastName><firstName>Jorge</firstName></Person></entry><entry><string>PCP</string><Person><lastName>Cunhal</lastName><firstName>Alvaro</firstName></Person></entry></partyLeaders><provinces size=\"9\"><string>Estremadura</string><string>Alentejo</string><string>Algarve</string><string>Beira Baixa</string><string>Beira Alta</string><string>Ribatejo</string><string>Douro</string><string>Minho</string><string>Trás os Montes</string></provinces><people size=\"3\"><Person><lastName>Martins</lastName><birthDay>1967-10-01</birthDay><firstName>Carlos</firstName></Person><Person><lastName>Sampaio</lastName><firstName>Jorge</firstName></Person><Person><lastName>Cunhal</lastName><firstName>Alvaro</firstName></Person></people></Country>";
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
	public void testMapReader() {
		
		JuffrouXml juffrouXml = new JuffrouXml("classpath:juffrou-xml-mapping.xml");
		
		String xmlString = juffrouXml.toXml(country);
		System.out.println(xmlString);
	}

	@Test
	public void testUnmarshallXml() {
		JuffrouXml juffrouXml = new JuffrouXml("classpath:juffrou-xml-mapping.xml");
		
		Object object = juffrouXml.fromXml(COUNTRY_XML);
		Assert.assertTrue(object instanceof Country);
		Country unmarshalledCountry = (Country) object;
		Assert.assertEquals(country.getName(), unmarshalledCountry.getName());
		Assert.assertEquals(country.getFounded(), unmarshalledCountry.getFounded());
		Assert.assertEquals(country.getPartyLeaders().size(), unmarshalledCountry.getPartyLeaders().size());
		Assert.assertEquals(country.getPeople().size(), unmarshalledCountry.getPeople().size());
		Assert.assertEquals(country.getProvinces().size(), unmarshalledCountry.getProvinces().size());
	}
	
	@Test
	public void testNestedPathsRoundTrip() {
		JuffrouXml juffrouXml = new JuffrouXml("classpath:nestedpaths-xml-mapping.xml");
		String xmlString = juffrouXml.toXml(country);
		
		System.out.println(xmlString);

		Object object = juffrouXml.fromXml(xmlString);
		Assert.assertTrue(object instanceof Country);
		Country unmarshalledCountry = (Country) object;
		Assert.assertEquals(country.getName(), unmarshalledCountry.getName());
		Assert.assertEquals(country.getFounded(), unmarshalledCountry.getFounded());
		Assert.assertEquals(country.getPresident().getFirstName(), unmarshalledCountry.getPresident().getFirstName());
		Assert.assertEquals(country.getPartyLeaders().size(), unmarshalledCountry.getPartyLeaders().size());
		Assert.assertEquals(country.getPeople().size(), unmarshalledCountry.getPeople().size());
		Assert.assertEquals(country.getProvinces().size(), unmarshalledCountry.getProvinces().size());

	}
}
