package net.sf.juffrou.util.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.juffrou.error.BeanInstanceCreatorException;
import net.sf.juffrou.error.ReflectionException;
import net.sf.juffrou.util.reflect.internal.BeanFieldHandler;



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
	private final Map<String, BeanWrapperContext> nestedContexts;
	private BeanWrapperContextHierarchy hierarchyContext = null;

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
		this.fields = new LinkedHashMap<String, BeanFieldHandler>();
		initFieldInfo(this.clazz, this.fields);
		this.nestedContexts = new HashMap<String, BeanWrapperContext>();
		
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
	
	/**
	 * Obtains the BeanWrapperContext that corresponds to the bean type of this property type.
	 * @param thisProperty property name in this bean wrapper context (bean class). It must be of bean type.
	 * @return
	 */
	public BeanWrapperContext getNestedContext(String thisProperty) {
		BeanWrapperContext nestedContext = nestedContexts.get(thisProperty);
		if (nestedContext == null) {
			Type propertyType = getBeanFieldHandler(thisProperty).getType();
			BeanWrapperContextHierarchy hierarchyCtx = getHierarchyContext();
			nestedContext = hierarchyCtx.getTypeContext(propertyType);
			if(nestedContext == null) {
				if(propertyType instanceof ParameterizedType)
					nestedContext = hierarchyCtx.getBeanContextCreator().newBeanWrapperContext((Class<?>)((ParameterizedType) propertyType).getRawType(), ((ParameterizedType) propertyType).getActualTypeArguments());
				else
					nestedContext = hierarchyCtx.getBeanContextCreator().newBeanWrapperContext((Class<?>) propertyType);
				nestedContext.setHierarchyContext(hierarchyCtx);
				hierarchyCtx.addTypeContext(propertyType, nestedContext);
				nestedContexts.put(thisProperty, nestedContext);
			}
		}
		return nestedContext;
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
			return getBeanFieldHandler(propertyName).getType();
		} else {
			// its a nested property
			String thisProperty = propertyName.substring(0, nestedIndex);
			String nestedProperty = propertyName.substring(nestedIndex + 1);
			BeanWrapperContext nestedContext = getNestedContext(thisProperty);
			return nestedContext.getType(nestedProperty);
		}
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
			return getHierarchyContext().getBeanInstanceCreator().newBeanInstance(clazz);
		} catch (BeanInstanceCreatorException e) {
			throw new ReflectionException(e);
		}
	}
	
	public Map<String, BeanFieldHandler> getFields() {
		return fields;
	}
	public Map<TypeVariable<?>, Type> getTypeArgumentsMap() {
		return typeArgumentsMap;
	}

	public BeanWrapperContextHierarchy getHierarchyContext() {
		if(hierarchyContext == null) {
			hierarchyContext = new BeanWrapperContextHierarchy();
			hierarchyContext.addTypeContext(clazz, this);
		}
		return hierarchyContext;
	}

	public void setHierarchyContext(BeanWrapperContextHierarchy hierarchyContext) {
		this.hierarchyContext = hierarchyContext;
	}

	public BeanInstanceCreator getBeanInstanceCreator() {
		return getHierarchyContext().getBeanInstanceCreator();
	}

	/**
	 * The bean wrapper creates new instances using Class.newIntance(). You can use this this if you want to create class instances yourself.  
	 * @param beanInstanceCreator
	 */
	public void setBeanInstanceCreator(BeanInstanceCreator beanInstanceCreator) {
		getHierarchyContext().setBeanInstanceCreator(beanInstanceCreator);
	}
	
	public BeanContextCreator<? extends BeanWrapperContext> getBeanContextCreator() {
		return getHierarchyContext().getBeanContextCreator();
	}
	public void setBeanContextCreator(BeanContextCreator<? extends BeanWrapperContext> beanContextCreator) {
		getHierarchyContext().setBeanContextCreator(beanContextCreator);
	}

}
