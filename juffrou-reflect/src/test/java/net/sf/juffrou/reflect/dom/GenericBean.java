package net.sf.juffrou.reflect.dom;

public class GenericBean<T> {

	private T genericProperty;

	public T getGenericProperty() {
		return genericProperty;
	}
	public void setGenericProperty(T genericProperty) {
		this.genericProperty = genericProperty;
	}
	
}
