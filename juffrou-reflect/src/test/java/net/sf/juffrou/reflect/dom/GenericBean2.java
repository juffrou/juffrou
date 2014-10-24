package net.sf.juffrou.reflect.dom;

public class GenericBean2<T, V> {

	private T genericProperty;
	
	private V otherGenericProperty;

	public T getGenericProperty() {
		return genericProperty;
	}
	public void setGenericProperty(T genericProperty) {
		this.genericProperty = genericProperty;
	}
	public V getOtherGenericProperty() {
		return otherGenericProperty;
	}
	public void setOtherGenericProperty(V otherGenericProperty) {
		this.otherGenericProperty = otherGenericProperty;
	}
	
}
