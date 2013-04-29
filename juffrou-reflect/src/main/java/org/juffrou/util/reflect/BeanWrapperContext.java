package org.juffrou.util.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.juffrou.error.BeanInstanceCreatorException;
import org.juffrou.error.ReflectionException;
import org.juffrou.util.reflect.internal.BeanFieldHandler;


/**
 * Holds metadata and preferences for a BeanWrapper.<p>
 * Performs introspection and holds metadata information about a class used by the {@link BeanWrapper}.<br>
 * If you have to create several BeanWrappers for the same java class, use this and save the introspection overhead.<br>
 * This class is thread safe.
 * @author cemartins
 *
 */
public class BeanWrapperContext {

	// metadata info
	private final Class clazz;
	private final Map<TypeVariable<?>, Type> typeArgumentsMap;
	private final Map<String, BeanFieldHandler> fields;
	private final Map<String, BeanWrapper> nestesWrappers;

	// preferences info
	private BeanInstanceCreator beanInstanceCreator;
	private BeanContextCreator<? extends BeanWrapperContext> beanContextCreator;
	private boolean eagerInstatiation;


	@SuppressWarnings("unchecked")
	public BeanWrapperContext(Class clazz) {
		this(clazz, null);
	}

	public BeanWrapperContext(Class clazz, Type...types) {
		this.typeArgumentsMap = new HashMap<TypeVariable<?>, Type>();
		if(types != null) {
			TypeVariable<?>[] typeParameters = clazz.getTypeParameters();
			for(int i = 0; i < types.length; i++) {
				this.typeArgumentsMap.put(typeParameters[i], types[i]);
			}
		}
		this.clazz = clazz;
		this.typeArgumentsMap.putAll(ReflectionUtil.getTypeArgumentsMap(Class.class, clazz));
		if( ! this.typeArgumentsMap.keySet().containsAll(Arrays.asList(clazz.getTypeParameters()))) {
			if(types == null)
				throw new ReflectionException(clazz.getSimpleName() + " is a parameterized type. Please use the BeanWrapperContext(Class clazz, Type...types) constructor.");
			else
				throw new ReflectionException(clazz.getSimpleName() + " has more parameterized types than those specified.");
		}
		this.fields = new HashMap<String, BeanFieldHandler>();
		initFieldInfo(this.clazz, this.fields);
		this.nestesWrappers = new HashMap<String, BeanWrapper>();
		
		beanInstanceCreator = new DefaultBeanInstanceCreator();
		beanContextCreator = new DefaultBeanContextCreator();
		eagerInstatiation = false;
	}

	private void initFieldInfo(Class<?> clazz, Map<String, BeanFieldHandler> fs) {
		Class<?> superclass = clazz.getSuperclass();
		if(superclass != Object.class) {
			initFieldInfo(superclass, fs);
		}
		for(Field f : clazz.getDeclaredFields()) {
			if( !Modifier.isStatic(f.getModifiers()) )
				fs.put(f.getName(), new BeanFieldHandler(this, f));
		}
	}

	public BeanFieldHandler getBeanFieldHandler(String propertyName) {
		BeanFieldHandler bfh = fields.get(propertyName);
		if (bfh == null) {
			throw new ReflectionException("The class " + clazz.getName() + " does not have a field with name "
					+ propertyName);
		}
		return bfh;
	}

	/**
	 * Get the wrapped bean class
	 * @return
	 */
	public Class<?> getBeanClass() {
		return clazz;
	}
	
	public Object newBeanInstance() {
		try {
			return beanInstanceCreator.newBeanInstance();
		} catch (BeanInstanceCreatorException e) {
			throw new ReflectionException(e);
		}
	}
	
	/**
	 * Returns all the nested bean wrappers that have been created inside this bean wrapper.<br>
	 * Nested bean wrappers are created when you access a nested property (i.e. getValue("prop1.prop2"))
	 * @return a Map where the keys are property names and the values are bean wrappers
	 */
	public Map<String, BeanWrapper> getNestedWrappers() {
		return nestesWrappers;
	}
	
	public Map<String, BeanFieldHandler> getFields() {
		return fields;
	}
	public Map<TypeVariable<?>, Type> getTypeArgumentsMap() {
		return typeArgumentsMap;
	}

	/**
	 * The bean wrapper creates new instances using Class.newIntance(). You can use this this if you want to create class instances yourself.  
	 * @param beanInstanceCreator
	 */
	public void setBeanInstanceCreator(BeanInstanceCreator beanInstanceCreator) {
		this.beanInstanceCreator = beanInstanceCreator;
	}
	
	public BeanContextCreator<? extends BeanWrapperContext> getBeanContextCreator() {
		return beanContextCreator;
	}
	public void setBeanContextCreator(
			BeanContextCreator<? extends BeanWrapperContext> beanContextCreator) {
		this.beanContextCreator = beanContextCreator;
	}

	public boolean isEagerInstatiation() {
		return eagerInstatiation;
	}
	/**
	 * Defines when a new instance is created.<br>If eager is true, a new instance is created when the BeanWrapper is created and reset. If eager is false, a new instance will only be created when setting a property value.<br>Default is false.
	 * @param eagerInstatiation
	 */
	public void setEagerInstatiation(boolean eager) {
		this.eagerInstatiation = eager;
	}



	private class DefaultBeanInstanceCreator implements BeanInstanceCreator {
		@Override
		public Object newBeanInstance() throws BeanInstanceCreatorException {
			Object instance;
			try {
				instance = clazz.newInstance();
			} catch (InstantiationException e) {
				throw new BeanInstanceCreatorException(e);
			} catch (IllegalAccessException e) {
				throw new BeanInstanceCreatorException(e);
			}
			return instance;
		}
	}
	
	private class DefaultBeanContextCreator implements BeanContextCreator<BeanWrapperContext> {

		@Override
		public BeanWrapperContext newBeanWrapperContext(Class clazz) {
			return new BeanWrapperContext(clazz);
		}

		@Override
		public BeanWrapperContext newBeanWrapperContext(Class clazz, Type... types) {
			return new BeanWrapperContext(clazz, types);
		}
		
	}
	
}
