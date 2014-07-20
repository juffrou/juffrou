package net.sf.juffrou.reflect.dom;

import java.util.ArrayList;
import java.util.List;

public class BeanWithCollection {

	private List<Address> addresses;
	
	public void addAddress(Address address) {
		if(addresses == null)
			addresses = new ArrayList<Address>();
		addresses.add(address);
	}
	
	public void removeAddress(Address address) {
		if(addresses != null)
			addresses.remove(address);
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}
}
