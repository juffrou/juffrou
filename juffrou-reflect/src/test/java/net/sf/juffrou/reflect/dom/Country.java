package net.sf.juffrou.reflect.dom;

public class Country {

	GenericBean<Person> president;
	Programmer programmer;

	public GenericBean<Person> getPresident() {
		return president;
	}
	public void setPresident(GenericBean<Person> president) {
		this.president = president;
		
	}
	public Programmer getProgrammer() {
		return programmer;
	}
	public void setProgrammer(Programmer programmer) {
		this.programmer = programmer;
	}
	
}
