package net.sf.juffrou.reflect;

import net.sf.juffrou.reflect.error.BeanInstanceBuilderException;
import net.sf.juffrou.reflect.error.CannotWrapInterfaceException;
import net.sf.juffrou.reflect.error.InvalidPropertyException;
import net.sf.juffrou.reflect.error.ReflectionException;
import net.sf.juffrou.reflect.internal.BeanCollectionFieldHandler;
import net.sf.juffrou.reflect.internal.BeanFieldHandler;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Holds introspection information for a java bean class.
 * <p>
 * Performs introspection and holds metadata information about a class used by the {@link JuffrouBeanWrapper}.<br>
 * If you have to create several BeanWrappers for the same java class, use BeanWrapperContext and save the introspection
 * overhead.<br>
 * This class is thread safe.
 * 
 * @author cemartins
 */
public class BeanWrapperContext {

	// metadata info
	private final Class clazz;
	private final Map<TypeVariable<?>, Type> typeArgumentsMap;
	private final Map<String, BeanFieldHandler> fields;
	private final BeanWrapperFactory bwFactory;

	public static final BeanWrapperContext create(Class clazz) {
		BeanWrapperFactory factory = new DefaultBeanWrapperFactory();
		return factory.getBeanWrapperContext(clazz);
	}

	public static final BeanWrapperContext create(Class clazz, Type... types) {
		BeanWrapperFactory factory = new DefaultBeanWrapperFactory();
		return factory.getBeanWrapperContext(clazz, types);
	}

	protected BeanWrapperContext(BeanWrapperFactory factory, Class clazz, Type... types) {
		if (factory == null)
			throw new IllegalArgumentException("BeanWrapperFactory cannot be null");
		if (clazz.isInterface())
			throw new CannotWrapInterfaceException("Cannot create a bean wrapper around an object of type "
					+ clazz.getSimpleName());
		this.bwFactory = factory;
		this.typeArgumentsMap = new HashMap<TypeVariable<?>, Type>();
		TypeVariable<?>[] typeParameters = clazz.getTypeParameters();
		//TODO find a way to resolve the generic parameter types
		if (types != null)
			for (int i = 0; i < types.length; i++)
				this.typeArgumentsMap.put(typeParameters[i], types[i]);
		else
			if(typeParameters != null && typeParameters.length > 0)
				for(TypeVariable typeVariable :  typeParameters)
					this.typeArgumentsMap.put(typeVariable, Object.class);
		
		this.clazz = clazz;
		this.typeArgumentsMap.putAll(ReflectionUtil.getTypeArgumentsMap(Class.class, clazz));
		this.fields = new LinkedHashMap<String, BeanFieldHandler>();
		initFieldInfo(this.clazz, this.fields);
	}

	private void initFieldInfo(Class<?> clazz, Map<String, BeanFieldHandler> fs) {
		Class<?> superclass = clazz.getSuperclass();
		if (superclass != Object.class) {
			initFieldInfo(superclass, fs);
		}
		for (Field f : clazz.getDeclaredFields()) {
			if (!Modifier.isStatic(f.getModifiers()))
				if (Collection.class.isAssignableFrom(f.getType()))
					fs.put(f.getName(), new BeanCollectionFieldHandler(this, f));
				else
					fs.put(f.getName(), new BeanFieldHandler(this, f));
		}
	}

	/**
	 * Obtains the BeanWrapperContext that corresponds to the bean type of this property type.
	 * 
	 * @param thisProperty
	 *            property name in this bean wrapper context (bean class). It must be of bean type.
	 * @param propertyValue the value of thisProperty or null. If present, propertyValue.getClass() will be used to infer the type of the underlying field.
	 * @return a bean wrapper context with bean metadata
	 */
	public BeanWrapperContext getNestedContext(String thisProperty, Object propertyValue) {
		Type propertyType;
		if (propertyValue != null)
			propertyType = propertyValue.getClass();
		else
			propertyType = getBeanFieldHandler(thisProperty).getGenericType();
		BeanWrapperContext nestedContext;
		if (propertyType instanceof ParameterizedType)
			nestedContext = bwFactory.getBeanWrapperContext((Class<?>) ((ParameterizedType) propertyType).getRawType(), ((ParameterizedType) propertyType).getActualTypeArguments());
		else
			nestedContext = bwFactory.getBeanWrapperContext((Class<?>) propertyType);
		return nestedContext;
	}

	public BeanFieldHandler getBeanFieldHandler(String propertyName) {
		BeanFieldHandler bfh = fields.get(propertyName);
		if (bfh == null) {
			// try and inspect getter and setter methods anyway
			Method getter = null;
			Method setter = null;
			try {
				getter = BeanFieldHandler.inspectReadMethod(clazz, propertyName, null);
			}
			catch (ReflectionException e) {
			}
			try {
				setter = BeanFieldHandler.inspectWriteMethod(clazz, propertyName, null);
			}
			catch (ReflectionException e) {
			}
			if(getter == null && setter == null)
				throw new InvalidPropertyException(clazz, propertyName);
			else {
				bfh = new BeanFieldHandler(this, getter, setter);
				fields.put(propertyName, bfh);
			}
		}
		return bfh;
	}

	/**
	 * Gets the type of a property in the wrapped bean. If that property references another bean (a nested bean) Its
	 * property types can also be obtained by specifying a property path.<br>
	 * For example <code>getType("pro1.prop2")</code> will get the type of prop2 from the nested bean referenced by
	 * prop1.<br>
	 * For each nested bean referenced in this manner, a nested bean wrapper is automatically created. In the previous
	 * example, a bean wrapper would be created for the bean referenced by property prop1.<br>
	 * 
	 * @param propertyName name of the property to get the type of.
	 * @return the type of the specified property
	 */
	public Type getType(String propertyName) {
		int nestedIndex = propertyName.indexOf('.');
		if (nestedIndex == -1) {
			return getBeanFieldHandler(propertyName).getGenericType();
		} else {
			// its a nested property
			String thisProperty = propertyName.substring(0, nestedIndex);
			String nestedProperty = propertyName.substring(nestedIndex + 1);
			BeanWrapperContext nestedContext = getNestedContext(thisProperty, null);
			return nestedContext.getType(nestedProperty);
		}
	}

	/**
	 * Get the wrapped bean class
	 * 
	 * @return the class of the wrapped bean
	 */
	public Class<?> getBeanClass() {
		return clazz;
	}

	public Object newBeanInstance() {
		try {
			return bwFactory.getBeanInstanceBuilder().build(clazz);
		} catch (BeanInstanceBuilderException e) {
			throw new ReflectionException(e);
		}
	}

	public Map<String, BeanFieldHandler> getFields() {
		return fields;
	}

	public Map<TypeVariable<?>, Type> getTypeArgumentsMap() {
		return typeArgumentsMap;
	}

	public BeanWrapperFactory getFactory() {
		return bwFactory;
	}
}
