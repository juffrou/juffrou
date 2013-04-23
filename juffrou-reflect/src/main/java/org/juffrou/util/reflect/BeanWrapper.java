package org.juffrou.util.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.juffrou.error.ReflectionException;
import org.juffrou.util.reflect.internal.BeanFieldHandler;


/**
 * Bean handling through property names.<p>
 * Allows access to the wrapped bean's properties and also to the properties of beans referenced by them. For example
 * the property path <code>"pro1.prop2"</code> will access the property prop2 from the nested bean referenced by prop1.<br>
 * This bean wrapper auto grows nested paths, so for each nested bean referenced in this manner, a nested bean wrapper is automatically created. In the previous
 * example, a bean wrapper would be created for the bean referenced by property prop1. The nested bean wrappers can be
 * obtained by calling the <code>getNestesWrappers()</code> method.<br>
 * You can reference nested properties as deep as you like as long as the bean path exists.
 * 
 * @author cemartins
 */
public class BeanWrapper {

	private final BeanWrapperContext context;
	private Object instance;

	/**
	 * Construct a bean wrapper using the metadata and preferences of an existing BeanWrapperContext.<br>
	 * Performance is better, because there is no need to do introspection.
	 * @param context metadata information about the class to instantiate the wrapped bean
	 */
	public BeanWrapper(BeanWrapperContext context) {
		this.context = context;
		if(context.isEagerInstatiation())
			this.instance = context.newBeanInstance();
		else
			this.instance = null;
	}

	/**
	 * Construct a bean wrapper using the metadata and preferences of an existing BeanWrapperContext and initializes it with an instance value.<br>
	 * Performance is better, because there is no need to do introspection.
	 * @param context metadata and preferences information about the class
	 * @param instance bean instance
	 */
	public BeanWrapper(BeanWrapperContext context, Object instance) {
		this.context = context;
		setBean(instance);
	}
	
	/**
	 * Construct a bean wrapper around an existing bean instance.<br>
	 * This constructor will have to create a BeanWrapperContext to get introspection metadata. You can use {@link #BeanWrapper(BeanWrapperContext, Object)} instead.
	 * @param instance
	 * @deprecated
	 */
	public BeanWrapper(Object instance) {
		this.instance = instance;
		this.context = new BeanWrapperContext(instance.getClass());
	}

	/**
	 * Construct a bean wrapper around a class. Bean instances will be instances of that class.<br>
	 * This constructor will have to create a BeanWrapperContext to get introspection metadata. You can use {@link #BeanWrapper(BeanWrapperContext, Object)} instead.
	 * @param clazz class to instantiate the wrapped bean
	 * @deprecated
	 */
	public BeanWrapper(Class<?> clazz) {
		this.context = new BeanWrapperContext(clazz);
		if(context.isEagerInstatiation())
			this.instance = context.newBeanInstance();
		else
			this.instance = null;
	}

	public BeanWrapperContext getContext() {
		return context;
	}

	/**
	 * Get the wrapped bean
	 * 
	 * @return the wrapped bean
	 */
	public Object getBean() {
		if(instance == null)
			instance = context.newBeanInstance();
		return instance;
	}
	
	/**
	 * Replaces the wrapped bean with another instance of the same type
	 * @param bean instance of the new bean to wrap
	 * @throws InvalidParameterException if the new bean is not of the same type of the initially wrapped bean.
	 */
	public void setBean(Object bean) {
		if(! context.getBeanClass().equals(bean.getClass())) {
			throw new InvalidParameterException("Bean must be of type " + context.getBeanClass().getSimpleName());
		}
		instance = bean;
	}

	/**
	 * Get the wrapped bean class
	 * @return
	 */
	public Class<?> getBeanClass() {
		return context.getBeanClass();
	}

	/**
	 * Returns all the nested bean wrappers that have been created inside this bean wrapper.<br>
	 * Nested bean wrappers are created when you access a nested property (i.e. getValue("prop1.prop2"))
	 * @return a Map where the keys are property names and the values are bean wrappers
	 */
	public Map<String, BeanWrapper> getNestedWrappers() {
		return context.getNestedWrappers();
	}
	
	/**
	 * sets all properties to null in this instance and in all nested bean instances
	 */
	public void reset() {
		for(BeanWrapper bw : context.getNestedWrappers().values()) {
			bw.reset();
		}
		if(context.isEagerInstatiation())
			this.instance = context.newBeanInstance();
		else
			this.instance = null;
	}
	
	/**
	 * Checks whether a property exists in the wrapped bean. If that property references another bean (a nested bean) 
	 * the method verifies if the property of the nested bean also exists.<br>
	 * For example <code>hasProperty("pro1.prop2")</code> returns true only if prop1 exists is this bean and prop2 exists in the bean referenced by prop1.<br>
	 * For each nested bean referenced in this manner, a nested bean wrapper is automatically created. In the previous
	 * example, a bean wrapper would be created for the bean referenced by property prop1.<br>
	 * @param propertyName
	 * @return
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
			BeanWrapper nestedWrapper = getNestedWrapper(thisProperty);
			return nestedWrapper.hasProperty(nestedProperty);
		}

	}
	
	/**
	 * Get the names of all properties found in this bean and base ascending hierarchy
	 * @return unordered list of property names.
	 */
	public String[] getPropertyNames() {
		List<String> propertyNames = new ArrayList<String>();
		propertyNames.addAll(context.getFields().keySet());
		
		return propertyNames.toArray(new String[0]);
	}

	/**
	 * Gets the value of a property in the wrapped bean. If that property references another bean (a nested bean) Its
	 * property values can also be obtained by specifying a property path.<br>
	 * For example <code>getValue("pro1.prop2")</code> will get the value of prop2 from the nested bean referenced by
	 * prop1.<br>
	 * For each nested bean referenced in this manner, a nested bean wrapper is automatically created. In the previous
	 * example, a bean wrapper would be created for the bean referenced by property prop1.<br>
	 * 
	 * @param propertyName
	 * @return the value held in the bean property
	 */
	public Object getValue(String propertyName) {
		if(instance == null)
			return null;
		int nestedIndex = propertyName.indexOf('.');
		if (nestedIndex == -1) {
			return context.getBeanFieldHandler(propertyName).getValue(this);
		}
		 else {
				// its a nested property
				String thisProperty = propertyName.substring(0, nestedIndex);
				String nestedProperty = propertyName.substring(nestedIndex + 1);
				BeanWrapper nestedWrapper = getNestedWrapper(thisProperty);
				return nestedWrapper.getValue(nestedProperty);
			}
	}

	/**
	 * Gets the class of a property in the wrapped bean. If that property references another bean (a nested bean) Its
	 * property types can also be obtained by specifying a property path.<br>
	 * For example <code>getType("pro1.prop2")</code> will get the type of prop2 from the nested bean referenced by
	 * prop1.<br>
	 * For each nested bean referenced in this manner, a nested bean wrapper is automatically created. In the previous
	 * example, a bean wrapper would be created for the bean referenced by property prop1.<br>
	 * 
	 * @param propertyName
	 * @return
	 */
	public Class<?> getClazz(String propertyName) {
		return ReflectionUtil.getClass(getType(propertyName));
	}

	/**
	 * Gets the type of a property in the wrapped bean. If that property references another bean (a nested bean) Its
	 * property types can also be obtained by specifying a property path.<br>
	 * For example <code>getType("pro1.prop2")</code> will get the type of prop2 from the nested bean referenced by
	 * prop1.<br>
	 * For each nested bean referenced in this manner, a nested bean wrapper is automatically created. In the previous
	 * example, a bean wrapper would be created for the bean referenced by property prop1.<br>
	 * 
	 * @param propertyName
	 * @return
	 */
	public Type getType(String propertyName) {
		int nestedIndex = propertyName.indexOf('.');
		if (nestedIndex == -1) {
			return context.getBeanFieldHandler(propertyName).getType();
		} else {
			// its a nested property
			String thisProperty = propertyName.substring(0, nestedIndex);
			String nestedProperty = propertyName.substring(nestedIndex + 1);
			BeanWrapper nestedWrapper = getNestedWrapper(thisProperty);
			return nestedWrapper.getType(nestedProperty);
		}
	}

	/**
	 * Gets the type of a property in the wrapped bean. If that property references another bean (a nested bean) Its
	 * property types can also be obtained by specifying a property path.<br>
	 * For example <code>getType("pro1.prop2")</code> will get the type of prop2 from the nested bean referenced by
	 * prop1.<br>
	 * For each nested bean referenced in this manner, a nested bean wrapper is automatically created. In the previous
	 * example, a bean wrapper would be created for the bean referenced by property prop1.<br>
	 * 
	 * @param propertyName
	 * @return
	 */
	public Type[] getTypeArguments(String propertyName) {
		int nestedIndex = propertyName.indexOf('.');
		if (nestedIndex == -1) {
			return context.getBeanFieldHandler(propertyName).getTypeArguments();
		} else {
			// its a nested property
			String thisProperty = propertyName.substring(0, nestedIndex);
			String nestedProperty = propertyName.substring(nestedIndex + 1);
			BeanWrapper nestedWrapper = getNestedWrapper(thisProperty);
			return nestedWrapper.getTypeArguments(nestedProperty);
		}
	}

	/**
	 * Gets the type of a property in the wrapped bean. If that property references another bean (a nested bean) Its
	 * property types can also be obtained by specifying a property path.<br>
	 * For example <code>getType("pro1.prop2")</code> will get the type of prop2 from the nested bean referenced by
	 * prop1.<br>
	 * For each nested bean referenced in this manner, a nested bean wrapper is automatically created. In the previous
	 * example, a bean wrapper would be created for the bean referenced by property prop1.<br>
	 * 
	 * @param propertyName
	 * @return
	 */
	public Field getField(String propertyName) {
		int nestedIndex = propertyName.indexOf('.');
		if (nestedIndex == -1) {
			return context.getBeanFieldHandler(propertyName).getField();
		} else {
			// its a nested property
			String thisProperty = propertyName.substring(0, nestedIndex);
			String nestedProperty = propertyName.substring(nestedIndex + 1);
			BeanWrapper nestedWrapper = getNestedWrapper(thisProperty);
			return nestedWrapper.getField(nestedProperty);
		}
	}

	/**
	 * Same as <code>setValue(String propertyName, Object value)</code> but the value will be converted from String to
	 * whatever type the property referenced by propertyName is.
	 * 
	 * @param propertyName
	 * @param value
	 *            String representation of the value to be set
	 */
	public void setValueOfString(String propertyName, String value) {
		if(instance == null)
			instance = context.newBeanInstance();
		int nestedIndex = propertyName.indexOf('.');
		if (nestedIndex == -1) {
			// not a nested property
			BeanFieldHandler beanFieldHandler = context.getBeanFieldHandler(propertyName);
			Class<?> paramType = (Class<?>) beanFieldHandler.getType();
			try {
				if (paramType.equals(String.class)) {
					beanFieldHandler.setValue(this, value);
				} else {
					Constructor<?> constructor = paramType.getConstructor(new Class<?>[] { String.class });
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
				throw new ReflectionException(context.getBeanClass().getName() + "." + propertyName + ": Cannot convert from String to " + paramType.getSimpleName() + ". Trying to convert " + value);
			} catch (InstantiationException e) {
				throw new ReflectionException(e);
			}
		} else {
			// its a nested property
			String thisProperty = propertyName.substring(0, nestedIndex);
			String nestedProperty = propertyName.substring(nestedIndex + 1);
			BeanWrapper nestedWrapper = getNestedWrapper(thisProperty);
			nestedWrapper.setValueOfString(nestedProperty, value);
		}
	}

	
	/**
	 * Sets the value of a property in the wrapped bean. If that property references another bean (a nested bean) Its
	 * property values can also be set by specifying a property path.<br>
	 * For example <code>setValue("pro1.prop2", Boolean.TRUE)</code> will set the value of prop2 from the nested bean
	 * referenced by prop1. If the value of prop1 was originally null, it would also be set to reference the new bean
	 * holding the value of prop2<br>
	 * For each nested bean referenced in this manner, a nested bean wrapper is automatically created. In the previous
	 * example, a bean wrapper would be created for the bean referenced by property prop1.<br>
	 * 
	 * @param propertyName
	 * @param value
	 *            value to be set
	 */
	public void setValue(String propertyName, Object value) {
		if(instance == null)
			instance = context.newBeanInstance();
		int nestedIndex = propertyName.indexOf('.');
		if (nestedIndex == -1) {
			// not a nested property
			context.getBeanFieldHandler(propertyName).setValue(this, value);
		} else {
			// its a nested property
			String thisProperty = propertyName.substring(0, nestedIndex);
			String nestedProperty = propertyName.substring(nestedIndex + 1);
			BeanWrapper nestedWrapper = getNestedWrapper(thisProperty);
			nestedWrapper.setValue(nestedProperty, value);
		}
	}

	private BeanWrapper getNestedWrapper(String thisProperty) {
		BeanWrapper nestedWrapper = context.getNestedWrappers().get(thisProperty);
		if (nestedWrapper == null) {
			Type propertyType = getType(thisProperty);
			BeanWrapperContext bwc;
			if(propertyType instanceof ParameterizedType)
				bwc = new BeanWrapperContext((Class<?>)((ParameterizedType) propertyType).getRawType(), ((ParameterizedType) propertyType).getActualTypeArguments());
			else
				bwc = new BeanWrapperContext((Class<?>) propertyType);
			
			Object value = getValue(thisProperty);
			if (value != null)
				nestedWrapper = new BeanWrapper(bwc, value);
			else
				nestedWrapper = new BeanWrapper(bwc);
			
			setValue(thisProperty, nestedWrapper.getBean());
			context.getNestedWrappers().put(thisProperty, nestedWrapper);
		}
		return nestedWrapper;
	}
	
}
