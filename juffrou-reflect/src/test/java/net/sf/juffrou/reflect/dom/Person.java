package net.sf.juffrou.reflect.dom;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Person {

	private String firstName;
	private String lastName;
	private Date birthDay;
	private Address home;
	private List<Address> otherAddresses;
	
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFullName() {
		return firstName + " " + lastName;
	}
	public Date getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}
	public Address getHome() {
		return home;
	}
	public void setHome(Address home) {
		this.home = home;
	}
	public List<Address> getOtherAddresses() {
		return otherAddresses;
	}
	public void setOtherAddresses(List<Address> otherAddresses) {
		this.otherAddresses = otherAddresses;
	}
	public void addOtherAddress(Address address) {
		if(otherAddresses == null)
			otherAddresses = new ArrayList<Address>();
		otherAddresses.add(address);
	}
	public void removeOtherAddress(Address address) {
		if(otherAddresses != null)
			otherAddresses.remove(address);
	}
}
