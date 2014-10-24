package net.sf.juffrou.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.juffrou.reflect.error.NonCollectionPropertyException;
import net.sf.juffrou.reflect.error.ReflectionException;
import net.sf.juffrou.reflect.internal.BeanCollectionFieldHandler;
import net.sf.juffrou.reflect.internal.BeanFieldHandler;

/**
 * Bean handling through property names.
 * <p>
 * Allows access to the wrapped bean's properties and also to the properties of
 * beans referenced by them. For example the property path
 * <code>"pro1.prop2"</code> will access the property prop2 from the nested bean
 * referenced by prop1.<br>
 * This bean wrapper auto grows nested paths, so for each nested bean referenced
 * in this manner, a nested bean wrapper is automatically created. In the
 * previous example, a bean wrapper would be created for the bean referenced by
 * property prop1. The nested bean wrappers can be obtained by calling the
 * <code>getNestesWrappers()</code> method.<br>
 * You can reference nested properties as deep as you like as long as the bean
 * path exists.
 * 
 * @author cemartins
 */
public class JuffrouBeanWrapper {

	private final BeanWrapperContext context;
	private final JuffrouBeanWrapper parentBeanWrapper;
	private final String parentBeanProperty;
	private Object wrappedInstance;
	private final Map<String, JuffrouBeanWrapper> nestedWrappers = new HashMap<String, JuffrouBeanWrapper>();

	/**
	 * Construct a bean wrapper using the metadata and preferences of an
	 * existing BeanWrapperContext.<br>
	 * Performance is better, because there is no need to do introspection.
	 * 
	 * @param context
	 *            metadata information about the class to instantiate the
	 *            wrapped bean
	 */
	public JuffrouBeanWrapper(BeanWrapperContext context) {
		this.context = context;
		this.wrappedInstance = null;
		this.parentBeanWrapper = null;
		this.parentBeanProperty = null;
	}

	/**
	 * Construct a bean wrapper using the metadata and preferences of an
	 * existing BeanWrapperContext and initializes it with an instance value.<br>
	 * Performance is better, because there is no need to do introspection.
	 * 
	 * @param context
	 *            metadata and preferences information about the class
	 * @param instance
	 *            bean instance
	 */
	public JuffrouBeanWrapper(BeanWrapperContext context, Object instance) {

		if (!context.getBeanClass().isAssignableFrom(instance.getClass()))
			throw new InvalidParameterException("Bean must be of type "
					+ context.getBeanClass().getSimpleName());

		this.context = context;
		this.parentBeanWrapper = null;
		this.parentBeanProperty = null;
		this.wrappedInstance = instance;
	}

	public JuffrouBeanWrapper(BeanWrapperContext context,
			JuffrouBeanWrapper parentBeanWrapper, String parentBeanProperty) {
		this.context = context;
		this.wrappedInstance = null;
		this.parentBeanWrapper = parentBeanWrapper;
		this.parentBeanProperty = parentBeanProperty;
	}

	/**
	 * Construct a bean wrapper around an existing bean instance.<br>
	 * This constructor will have to create a BeanWrapperContext to get
	 * introspection metadata. You can use
	 * {@link #JuffrouBeanWrapper(BeanWrapperContext, Object)} instead.
	 * 
	 * @param instance object to wrap.
	 */
	public JuffrouBeanWrapper(Object instance) {
		this.wrappedInstance = instance;
		this.context = BeanWrapperContext.create(instance.getClass());
		this.parentBeanWrapper = null;
		this.parentBeanProperty = null;
	}

	/**
	 * Construct a bean wrapper around a class. Bean instances will be instances
	 * of that class.<br>
	 * This constructor will have to create a BeanWrapperContext to get
	 * introspection metadata. You can use
	 * {@link #JuffrouBeanWrapper(BeanWrapperContext, Object)} instead.
	 * 
	 * @param clazz
	 *            class to instantiate the wrapped bean
	 */
	public JuffrouBeanWrapper(Class<?> clazz) {
		this.context = BeanWrapperContext.create(clazz);
		this.wrappedInstance = null;
		this.parentBeanWrapper = null;
		this.parentBeanProperty = null;
	}

	/**
	 * This bean wrapper can either be a wrapper around a root bean or a wrapper
	 * around a property of a root bean. This method tells which.
	 * 
	 * @return true if this wrapper is around a root bean, false if it wraps a
	 *         property's bean.
	 */
	private boolean isRoot() {
		return (this.parentBeanWrapper == null);
	}

	/**
	 * @return the BeanWrapperContext containing introspection information about
	 *         this bean
	 */
	public BeanWrapperContext getContext() {
		return context;
	}

	/**
	 * @return The BeanWrapperFactory responsible for creating and caching the
	 *         BeanWrapperContext for this bean and all its nested beans.
	 */
	public BeanWrapperFactory getFactory() {
		return context.getFactory();
	}

	/**
	 * Get the wrapped bean Get the wrapped bean instance. If the wrapped
	 * instance is null, this method may instantiate it before returning.
	 * 
	 * @param instantiateIfNull
	 *            is true, this method will force the instantiation of the bean
	 *            in case it is null.
	 * @return The wrapped bean instance or null.
	 */
	public Object getBean(boolean instantiateIfNull) {
		if (isRoot()) {
			if (wrappedInstance == null && instantiateIfNull)
				createInstance();
			return wrappedInstance;
		}
		return parentBeanWrapper.getContext()
				.getBeanFieldHandler(parentBeanProperty)
				.getValue(parentBeanWrapper);
	}

	/**
	 * Get the wrapped bean instance. This method will always return a non null
	 * value. If the instance is null it will be instantiated.<br>
	 * This is the same as {@link #getBean(boolean)} with TRUE passed as
	 * parameter.
	 *
	 * @return the wrapped bean
	 * @see #getBean(boolean)
	 */
	public Object getBean() {
		if (isRoot()) {
			if (wrappedInstance == null)
				createInstance();
			return wrappedInstance;
		}
		return parentBeanWrapper.getContext()
				.getBeanFieldHandler(parentBeanProperty)
				.getValue(parentBeanWrapper);
	}

	/**
	 * Replaces the wrapped bean with another instance of the same type
	 * 
	 * @param bean
	 *            instance of the new bean to wrap
	 * @throws InvalidParameterException
	 *             if the new bean is not of the same type of the initially
	 *             wrapped bean.
	 */
	public void setBean(Object bean) {

		if (bean != null
				&& !context.getBeanClass().isAssignableFrom(bean.getClass()))
			throw new InvalidParameterException("Bean must be of type "
					+ context.getBeanClass().getSimpleName());

		if (isRoot())
			wrappedInstance = bean;
		else
			parentBeanWrapper.setValue(parentBeanProperty, bean);
	}

	/**
	 * Get the wrapped bean class
	 * 
	 * @return the class of the wrapped object
	 */
	public Class<?> getBeanClass() {
		return context.getBeanClass();
	}

	/**
	 * Returns all the nested bean wrappers that have been created inside this
	 * bean wrapper.<br>
	 * Nested bean wrappers are created when you access a nested property (i.e.
	 * getValue("prop1.prop2"))
	 * 
	 * @return a Map where the keys are property names and the values are bean
	 *         wrappers
	 */
	public Map<String, JuffrouBeanWrapper> getNestedWrappers() {
		return nestedWrappers;
	}

	/**
	 * Checks whether a property exists in the wrapped bean. If that property
	 * references another bean (a nested bean) the method verifies if the
	 * property of the nested bean also exists.<br>
	 * For example <code>hasProperty("pro1.prop2")</code> returns true only if
	 * prop1 exists is this bean and prop2 exists in the bean referenced by
	 * prop1.<br>
	 * For each nested bean referenced in this manner, a nested bean wrapper is
	 * automatically created. In the previous example, a bean wrapper would be
	 * created for the bean referenced by property prop1.<br>
	 * 
	 * @param propertyName property name to search for
	 * @return true if the bean has a property with the specified property name
	 */
	public boolean hasProperty(String propertyName) {

		int nestedIndex = propertyName.indexOf('.');
		if (nestedIndex == -1) {
			// not a nested property
			return context.getFields().containsKey(propertyName);
		} else {
			// its a nested property
			String thisProperty = propertyName.substring(0, nestedIndex);
			String nestedProperty = propertyName.substring(nestedIndex + 1);
			JuffrouBeanWrapper nestedWrapper = getLocalNestedWrapper(thisProperty);
			return nestedWrapper.hasProperty(nestedProperty);
		}

	}

	/**
	 * Get the names of all properties found in this bean and base ascending
	 * hierarchy
	 * 
	 * @return unordered list of property names.
	 */
	public String[] getPropertyNames() {
		List<String> propertyNames = new ArrayList<String>();
		propertyNames.addAll(context.getFields().keySet());

		return propertyNames.toArray(new String[0]);
	}

	/**
	 * Returns the string representation of the wrapped bean instance, or and
	 * empty string if the instance is null.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Object instance = getBean();
		return instance == null ? "" : instance.toString();
	}

	/**
	 * Gets the value of a property in the wrapped bean. If that property
	 * references another bean (a nested bean) Its property values can also be
	 * obtained by specifying a property path.<br>
	 * For example <code>getValue("pro1.prop2")</code> will get the value of
	 * prop2 from the nested bean referenced by prop1.<br>
	 * For each nested bean referenced in this manner, a nested bean wrapper is
	 * automatically created. In the previous example, a bean wrapper would be
	 * created for the bean referenced by property prop1.<br>
	 * 
	 * @param propertyName property to get the value of.
	 * @return the value held in the bean property
	 */
	public Object getValue(String propertyName) {
		if (getBean(false) == null)
			return null;
		int nestedIndex = propertyName.indexOf('.');
		if (nestedIndex == -1) {
			return context.getBeanFieldHandler(propertyName).getValue(this);
		} else {
			// its a nested property
			String thisProperty = propertyName.substring(0, nestedIndex);
			String nestedProperty = propertyName.substring(nestedIndex + 1);
			JuffrouBeanWrapper nestedWrapper = getLocalNestedWrapper(thisProperty);
			return nestedWrapper.getValue(nestedProperty);
		}
	}

	/**
	 * Gets the class of a property in the wrapped bean. If that property
	 * references another bean (a nested bean) Its property types can also be
	 * obtained by specifying a property path.<br>
	 * For example <code>getType("pro1.prop2")</code> will get the type of prop2
	 * from the nested bean referenced by prop1.<br>
	 * For each nested bean referenced in this manner, a nested bean wrapper is
	 * automatically created. In the previous example, a bean wrapper would be
	 * created for the bean referenced by property prop1.<br>
	 * 
	 * @param propertyName the property name to search
	 * @return the class of the object referenced by the specified property
	 */
	public Class<?> getClazz(String propertyName) {
		return ReflectionUtil.getClass(getType(propertyName));
	}

	/**
	 * Gets the type of a property in the wrapped bean. If that property
	 * references another bean (a nested bean) Its property types can also be
	 * obtained by specifying a property path.<br>
	 * For example <code>getType("pro1.prop2")</code> will get the type of prop2
	 * from the nested bean referenced by prop1.<br>
	 * For each nested bean referenced in this manner, a nested bean wrapper is
	 * automatically created. In the previous example, a bean wrapper would be
	 * created for the bean referenced by property prop1.<br>
	 * 
	 * @param propertyName the property name
	 * @return the type of the object referenced by the specified property
	 */
	public Type getType(String propertyName) {
		int nestedIndex = propertyName.indexOf('.');
		if (nestedIndex == -1) {
			Object value = getBean() == null ? null : context
					.getBeanFieldHandler(propertyName).getValue(this);
			if (value != null)
				return value.getClass();
			else
				return context.getBeanFieldHandler(propertyName)
						.getGenericType();
		} else {
			// its a nested property
			String thisProperty = propertyName.substring(0, nestedIndex);
			Object value = context.getBeanFieldHandler(thisProperty).getValue(
					this);
			String nestedProperty = propertyName.substring(nestedIndex + 1);
			BeanWrapperContext nestedContext = context.getNestedContext(
					thisProperty, value);
			return nestedContext.getType(nestedProperty);
		}
	}

	/**
	 * Gets the type arguments of a property in the wrapped bean. If that property
	 * references another bean (a nested bean) Its property types can also be
	 * obtained by specifying a property path.<br>
	 * For example <code>getTypeArguments("pro1.prop2")</code> will get the type arguments of prop2
	 * from the nested bean referenced by prop1.<br>
	 * For each nested bean referenced in this manner, a nested bean wrapper is
	 * automatically created. In the previous example, a bean wrapper would be
	 * created for the bean referenced by property prop1.<br>
	 * 
	 * @param propertyName the property name
	 * @return an array of types
	 */
	public Type[] getTypeArguments(String propertyName) {
		int nestedIndex = propertyName.indexOf('.');
		if (nestedIndex == -1) {
			return context.getBeanFieldHandler(propertyName).getTypeArguments();
		} else {
			// its a nested property
			String thisProperty = propertyName.substring(0, nestedIndex);
			String nestedProperty = propertyName.substring(nestedIndex + 1);
			JuffrouBeanWrapper nestedWrapper = getLocalNestedWrapper(thisProperty);
			return nestedWrapper.getTypeArguments(nestedProperty);
		}
	}

	/**
	 * Gets the field of a property in the wrapped bean. If that property
	 * references another bean (a nested bean) Its property field can also be
	 * obtained by specifying a property path.<br>
	 * For example <code>getField("pro1.prop2")</code> will get the field of prop2
	 * from the nested bean referenced by prop1.<br>
	 * For each nested bean referenced in this manner, a nested bean wrapper is
	 * automatically created. In the previous example, a bean wrapper would be
	 * created for the bean referenced by property prop1.<br>
	 * 
	 * @param propertyName the property
	 * @return the field which represents the specified bean property
	 */
	public Field getField(String propertyName) {
		int nestedIndex = propertyName.indexOf('.');
		if (nestedIndex == -1) {
			return context.getBeanFieldHandler(propertyName).getField();
		} else {
			// its a nested property
			String thisProperty = propertyName.substring(0, nestedIndex);
			String nestedProperty = propertyName.substring(nestedIndex + 1);
			JuffrouBeanWrapper nestedWrapper = getLocalNestedWrapper(thisProperty);
			return nestedWrapper.getField(nestedProperty);
		}
	}

	/**
	 * Same as <code>setValue(String propertyName, Object value)</code> but the
	 * value will be converted from String to whatever type the property
	 * referenced by propertyName is.
	 * 
	 * @param propertyName property to set the value of
	 * @param value
	 *            String representation of the value to be set
	 */
	public void setValueOfString(String propertyName, String value) {
		if (getBean(false) == null)
			if (value == null)
				return;
			else
				createInstance();
		int nestedIndex = propertyName.indexOf('.');
		if (nestedIndex == -1) {
			// not a nested property
			BeanFieldHandler beanFieldHandler = context
					.getBeanFieldHandler(propertyName);
			Class paramType = beanFieldHandler.getType();
			try {
				if (paramType == String.class)
					beanFieldHandler.setValue(this, value);
				else if (value.isEmpty())
					beanFieldHandler.setValue(this, null);
				else if (paramType.isEnum())
					beanFieldHandler.setValue(this,
							Enum.valueOf(paramType, value));
				else {
					Constructor<?> constructor = paramType
							.getConstructor(new Class<?>[] { String.class });
					Object convertedValue = constructor.newInstance(value);
					beanFieldHandler.setValue(this, convertedValue);
				}
			} catch (IllegalArgumentException e) {
				throw new ReflectionException(e);
			} catch (IllegalAccessException e) {
				throw new ReflectionException(e);
			} catch (InvocationTargetException e) {
				throw new ReflectionException(e);
			} catch (SecurityException e) {
				throw new ReflectionException(e);
			} catch (NoSuchMethodException e) {
				throw new ReflectionException(context.getBeanClass().getName()
						+ "." + propertyName
						+ ": Cannot convert from String to "
						+ paramType.getSimpleName() + ". Trying to convert "
						+ value);
			} catch (InstantiationException e) {
				throw new ReflectionException(e);
			}
		} else {
			// its a nested property
			String thisProperty = propertyName.substring(0, nestedIndex);
			String nestedProperty = propertyName.substring(nestedIndex + 1);
			JuffrouBeanWrapper nestedWrapper = getLocalNestedWrapper(thisProperty);
			nestedWrapper.setValueOfString(nestedProperty, value);
		}
	}

	/**
	 * Sets the value of a property in the wrapped bean. If that property
	 * references another bean (a nested bean) Its property values can also be
	 * set by specifying a property path.<br>
	 * For example <code>setValue("pro1.prop2", Boolean.TRUE)</code> will set
	 * the value of prop2 from the nested bean referenced by prop1. If the value
	 * of prop1 was originally null, it would also be set to reference the new
	 * bean holding the value of prop2<br>
	 * For each nested bean referenced in this manner, a nested bean wrapper is
	 * automatically created. In the previous example, a bean wrapper would be
	 * created for the bean referenced by property prop1.<br>
	 * 
	 * @param propertyName property to set the value of.
	 * @param value
	 *            value to be set
	 */
	public void setValue(String propertyName, Object value) {
		if (getBean(false) == null)
			if (value == null)
				return;
			else
				createInstance();
		int nestedIndex = propertyName.indexOf('.');
		if (nestedIndex == -1)
			context.getBeanFieldHandler(propertyName).setValue(this, value);
		else {
			// its a nested property
			String thisProperty = propertyName.substring(0, nestedIndex);
			String nestedProperty = propertyName.substring(nestedIndex + 1);
			JuffrouBeanWrapper nestedWrapper = getLocalNestedWrapper(thisProperty);
			nestedWrapper.setValue(nestedProperty, value);
		}
	}
	
	/**
	 * Add an element to a collection property.<p>
	 * The collection must be a parameterized collection, for example {@code List<ElementBean>}.<br>
	 * This method will first try to find an add method in the bean that has the collection with the pattern {@code addElementBean(ElementBean arg)}. 
	 * If no such method is found then the element will be added directly to the collection field using the {@code Collection.add} method.

	 * @param propertyName name of the collection type property. Can be a nested property path.
	 * @param element element to add
	 */
	public void addElement(String propertyName, Object element) {
		if (getBean(false) == null)
			if (element == null)
				return;
			else
				createInstance();
		int nestedIndex = propertyName.indexOf('.');
		if (nestedIndex == -1) {
			BeanFieldHandler beanFieldHandler = context.getBeanFieldHandler(propertyName);
			try {
				((BeanCollectionFieldHandler)beanFieldHandler).addElement(this, element);
			}
			catch(ClassCastException e) {
				throw new NonCollectionPropertyException(beanFieldHandler.getType(), propertyName);
			}
		}
		else {
			// its a nested property
			String thisProperty = propertyName.substring(0, nestedIndex);
			String nestedProperty = propertyName.substring(nestedIndex + 1);
			JuffrouBeanWrapper nestedWrapper = getLocalNestedWrapper(thisProperty);
			nestedWrapper.addElement(nestedProperty, element);
		}
	}

	/**
	 * Remove an element from a collection property.<p>
	 * The collection must be a parameterized collection, for example {@code List<ElementBean>}.<br>
	 * This method will first try to find a remove method in the bean that has the collection with the pattern {@code removeElementBean(ElementBean arg)}. 
	 * If no such method is found then the element will be removed directly from the collection field using the {@code Collection.remove} method.
	 * @param propertyName name of the collection type property. Can be a nested property path.
	 * @param element element to remove
	 */
	public void removeElement(String propertyName, Object element) {
		if (getBean() == null)
			if (element == null)
				return;
			else
				createInstance();
		int nestedIndex = propertyName.indexOf('.');
		if (nestedIndex == -1) {
			BeanFieldHandler beanFieldHandler = context.getBeanFieldHandler(propertyName);
			try {
				((BeanCollectionFieldHandler)beanFieldHandler).removeElement(this, element);
			}
			catch(ClassCastException e) {
				throw new NonCollectionPropertyException(beanFieldHandler.getType(), propertyName);
			}
		}
		else {
			// its a nested property
			String thisProperty = propertyName.substring(0, nestedIndex);
			String nestedProperty = propertyName.substring(nestedIndex + 1);
			JuffrouBeanWrapper nestedWrapper = getLocalNestedWrapper(thisProperty);
			nestedWrapper.removeElement(nestedProperty, element);
		}
	}

	/**
	 * Obtains the JuffrouBeanWrapper corresponding to the bean referred by
	 * propertyName. If that property references a nested bean, the method will
	 * delegate to that nested bean.<br>
	 * For example <code>getNestedWrapper("prop1.prop2")</code> will ask the
	 * bean wrapper on prop1 to return the bean wrapper of prop2. If the value
	 * of prop1 was originally null, it would also be set to reference the new
	 * bean holding the value of prop2<br>
	 * For each nested bean referenced in this manner, a nested bean wrapper is
	 * automatically created. In the previous example, a bean wrapper would be
	 * created for the bean referenced by property prop1.<br>
	 * 
	 * @param propertyName
	 *            property name in this bean wrapper or inside a nested bean. It
	 *            must be of bean type.
	 * @return JuffrouBeanWrapper instance
	 * @see #getLocalNestedWrapper(String) to obtain a bean wrapper
	 *      corresponding to a non nested property.
	 */
	public JuffrouBeanWrapper getNestedWrapper(String propertyName) {
		int nestedIndex = propertyName.indexOf('.');
		if (nestedIndex == -1) {
			return getLocalNestedWrapper(propertyName);
		} else {
			// its a nested property
			String thisProperty = propertyName.substring(0, nestedIndex);
			String nestedProperty = propertyName.substring(nestedIndex + 1);
			JuffrouBeanWrapper localNestedWrapper = getLocalNestedWrapper(thisProperty);
			return localNestedWrapper.getNestedWrapper(nestedProperty);
		}
	}

	/**
	 * Obtains the BeanWrapper that corresponds to the bean instance of this
	 * property type.
	 * 
	 * @param thisProperty
	 *            property name in this bean wrapper. The property type must be
	 *            another bean and nested properties are not allowed.
	 * @return JuffrouBeanWrapper instance
	 * @see #getNestedWrapper(String) to obtain nested wrappers of
	 *      nested wrappers.
	 */
	public JuffrouBeanWrapper getLocalNestedWrapper(String thisProperty) {
		JuffrouBeanWrapper nestedWrapper = nestedWrappers.get(thisProperty);
		if (nestedWrapper == null) {
			Object value = getValue(thisProperty);

			BeanWrapperContext bwc = context.getNestedContext(thisProperty,
					value);

			nestedWrapper = new JuffrouBeanWrapper(bwc, this, thisProperty);

			nestedWrappers.put(thisProperty, nestedWrapper);
		}
		return nestedWrapper;
	}

	protected JuffrouBeanWrapper getParentBeanWrapper() {
		return parentBeanWrapper;
	}

	protected String getParentBeanProperty() {
		return parentBeanProperty;
	}

	private void createInstance() {
		Object instance = context.newBeanInstance();
		if (isRoot())
			wrappedInstance = instance;
		else
			parentBeanWrapper.setValue(parentBeanProperty, instance);
	}
}
