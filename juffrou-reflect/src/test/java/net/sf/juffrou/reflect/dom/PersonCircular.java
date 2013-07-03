package net.sf.juffrou.reflect.dom;

import java.util.Date;

public class PersonCircular {

	private String firstName;
	private String lastName;
	private Date birthDay;
	private AddressCircular address;
	
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
	public Date getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}
	public AddressCircular getAddress() {
		return address;
	}
	public void setAddress(AddressCircular address) {
		address.setPerson(this);
		this.address = address;
	}
}
