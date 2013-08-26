package net.sf.juffrou.reflect.error;

public class InvalidPropertyException extends ReflectionException {

	private static final long serialVersionUID = -6922159731513540328L;
	
	private final Class<?> clazz;
	private final String propertyName;

	public InvalidPropertyException(Class<?> clazz, String propertyName) {
		super("The class " + clazz.getName() + " does not have a field with name " + propertyName);
		this.clazz = clazz;
		this.propertyName = propertyName;
	}

	public InvalidPropertyException(Class<?> clazz, String propertyName, Throwable reason) {
		super("The class " + clazz.getName() + " does not have a field with name " + propertyName, reason);
		this.clazz = clazz;
		this.propertyName = propertyName;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getPropertyName() {
		return propertyName;
	}

}
