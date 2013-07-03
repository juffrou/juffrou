package net.sf.juffrou.reflect.dom;

public class AddressCircular {

	private PersonCircular person;
	private String street;
	private String town;
	
	
	public PersonCircular getPerson() {
		return person;
	}
	public void setPerson(PersonCircular person) {
		this.person = person;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getTown() {
		return town;
	}
	public void setTown(String town) {
		this.town = town;
	}
	
}
