package net.sf.juffrou.reflect.error;

public class NonCollectionPropertyException extends ReflectionException {

	private static final long serialVersionUID = -3175449791272081884L;

	private final Class<?> clazz;
	private final String propertyName;

	public NonCollectionPropertyException(Class<?> clazz, String propertyName) {
		super("The property " + propertyName + " of class "+ clazz.getSimpleName() + " is not a collection");
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
