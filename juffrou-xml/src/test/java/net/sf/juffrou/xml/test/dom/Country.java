package net.sf.juffrou.xml.test.dom;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Country {

	private Date founded;
	private String name;
	private Person president;
	
	private List<String> provinces;
	private Set<Person> people;
	private Map<String, Person> partyLeaders;
	
	
	public Date getFounded() {
		return founded;
	}
	public void setFounded(Date founded) {
		this.founded = founded;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Person getPresident() {
		return president;
	}
	public void setPresident(Person president) {
		this.president = president;
	}
	public List<String> getProvinces() {
		return provinces;
	}
	public void setProvinces(List<String> provinces) {
		this.provinces = provinces;
	}
	public Set<Person> getPeople() {
		return people;
	}
	public void setPeople(Set<Person> people) {
		this.people = people;
	}
	public Map<String, Person> getPartyLeaders() {
		return partyLeaders;
	}
	public void setPartyLeaders(Map<String, Person> partyLeaders) {
		this.partyLeaders = partyLeaders;
	}
}
