package net.sf.juffrou.util.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Utility methods to provide information over generic types.<p>
 * When a generic class is extended we often need to know which types were used to typify the generic.
 * @author cemartins
 */
public final class ReflectionUtil {
	
	/**
	 * Get the underlying class for a type, or null if the type is a variable type.
	 * 
	 * @param type
	 *            the type
	 * @return the underlying class
	 */
	public static Class<?> getClass(Type type) {
		if (type instanceof Class) {
			return (Class) type;
		} else if (type instanceof ParameterizedType) {
			return getClass(((ParameterizedType) type).getRawType());
		} else if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			Class<?> componentClass = getClass(componentType);
			if (componentClass != null) {
				return Array.newInstance(componentClass, 0).getClass();
			} else {
				return null;
			}
		} else {
			if(type instanceof TypeVariable) {
				TypeVariable typeVariable = (TypeVariable) type;
				GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
				return type.getClass();
			}
			else {
				return null;
			}
		}
	}

	/**
	 * Get the actual type arguments a child class has used to extend a generic base class.
	 * 
	 * @param baseClass
	 *            the base class
	 * @param childClass
	 *            the child class
	 * @return a list of the raw classes for the actual type arguments.
	 */
	public static <T> List<Class<?>> getTypeArguments(Class<T> baseClass, Class<? extends T> childClass) {
		Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
		Type type = childClass;
		// start walking up the inheritance hierarchy until we hit baseClass
		while (type != null && !getClass(type).equals(baseClass)) {
			if (type instanceof Class) {
				// there is no useful information for us in raw types, so just keep going.
				type = ((Class) type).getGenericSuperclass();
			} else {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Class<?> rawType = (Class) parameterizedType.getRawType();

				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
				for (int i = 0; i < actualTypeArguments.length; i++) {
					resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
				}

				if (!rawType.equals(baseClass)) {
					type = rawType.getGenericSuperclass();
				}
			}
		}

		// finally, for each actual type argument provided to baseClass, determine (if possible)
		// the raw class for that type argument.
		Type[] actualTypeArguments;
		if (type instanceof Class) {
			actualTypeArguments = ((Class) type).getTypeParameters();
		} else {
			if(type != null) {
				actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
			}
			else {
				actualTypeArguments = null;
			}
		}
		List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
		if(actualTypeArguments == null || actualTypeArguments.length == 0) {
			for(Type baseType : resolvedTypes.values()) {
				typeArgumentsAsClasses.add(getClass(baseType));
			}
		}
		else {
			// resolve types by chasing down type variables.
			for (Type baseType : actualTypeArguments) {
				while (resolvedTypes.containsKey(baseType)) {
					baseType = resolvedTypes.get(baseType);
				}
				typeArgumentsAsClasses.add(getClass(baseType));
			}
		}
		return typeArgumentsAsClasses;
	}
	
	
	/**
	 * Get the type parameters and the corresponding actual type arguments a child class has used to extend a generic base class.
	 * 
	 * @param baseClass
	 *            the base class
	 * @param childClass
	 *            the child class
	 * @return A map where the keys are TypeVariable (the type variables declared by the generic declaration) and the values are the Type objects representing the actual type argument the corresponds.
	 */
	public static <T> Map<TypeVariable<?>, Type> getTypeArgumentsMap(Class<T> baseClass, Class<? extends T> childClass) {
		Map<TypeVariable<?>, Type> resolvedTypes = new HashMap<TypeVariable<?>, Type>();
		Type type = childClass;
		// start walking up the inheritance hierarchy until we hit baseClass
		while (type != null && !getClass(type).equals(baseClass)) {
			if (type instanceof Class) {
				// there is no useful information for us in raw types, so just keep going.
				type = ((Class) type).getGenericSuperclass();
			} else {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Class<?> rawType = (Class) parameterizedType.getRawType();

				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
				for (int i = 0; i < actualTypeArguments.length; i++) {
					resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
				}

				if (!rawType.equals(baseClass)) {
					type = rawType.getGenericSuperclass();
				}
			}
		}
		return resolvedTypes;
	}
	
	/**
	 * @param clazz
	 * @return
	 */
	public static boolean isSimpleType(Type type) {
		if(! (type instanceof Class))
			return false;
		Class<?> clazz = (Class<?>) type;
		return clazz.isPrimitive() || clazz.isEnum() || clazz.isInterface() || clazz.getName().startsWith("java");
	}

	/**
	 * @param bean
	 * @return
	 */
	public static Map<String, Object> getMapFromBean(Object bean) {
		return getMapFromBean(new BeanWrapperFactory(), bean);
	}
	
	/**
	 * @param factory
	 * @param bean
	 * @return
	 */
	public static Map<String, Object> getMapFromBean(BeanWrapperFactory factory, Object bean) {
		Map<String, Object> beanMap = new HashMap<String, Object>();
		Map<Object, String> circularReferences = new HashMap<Object, String>();
		BeanWrapper beanWrapper = factory.getBeanWrapper(bean);
		circularReferences.put(bean, "");
		buildMapFromBean(factory, beanWrapper, circularReferences, "",beanMap);
		return beanMap;
	}
	
	private static void buildMapFromBean(BeanWrapperFactory factory, BeanWrapper beanWrapper, Map<Object, String> circularReferences, String pathPrefix, Map<String, Object> beanMap) {
		for(String propertyName : beanWrapper.getPropertyNames()) {
			Object value = beanWrapper.getValue(propertyName);
			if(value == null)
				continue;
			if(isSimpleType(beanWrapper.getType(propertyName)))
				beanMap.put(pathPrefix + propertyName, value);
			else
				if(circularReferences.containsKey(value))
					beanMap.put(pathPrefix + propertyName, new CircularReference(circularReferences.get(value)));
			else {
				circularReferences.put(value, pathPrefix + propertyName);
				buildMapFromBean(factory, beanWrapper.getNestedWrapper(propertyName), circularReferences, pathPrefix + propertyName + ".", beanMap);
			}
		}
	}
	
	/**
	 * @param beanMap
	 * @param bean
	 */
	public static void getBeanFromMap(Map<String, Object> beanMap, Object bean) {
		getBeanFromMap(new BeanWrapperFactory(), beanMap, bean);
	}

	/**
	 * @param factory
	 * @param beanMap
	 * @param bean
	 */
	public static void getBeanFromMap(BeanWrapperFactory factory, Map<String, Object> beanMap, Object bean) {
		Map<String, CircularReference> circularReferences = new HashMap<String, CircularReference>();
		BeanWrapper beanWrapper = factory.getBeanWrapper(bean);
		for(Entry<String, Object> entry : beanMap.entrySet())
			if(entry.getValue() instanceof CircularReference)
				circularReferences.put(entry.getKey(), (CircularReference) entry.getValue());
			else
				beanWrapper.setValue(entry.getKey(), entry.getValue());
		
		// set the circular references after the other properties so that they can reference existing values
		for(Entry<String, CircularReference> entry : circularReferences.entrySet())
			beanWrapper.setValue(entry.getKey(), entry.getValue().getPath().isEmpty() ? beanWrapper.getBean() : beanWrapper.getValue(entry.getValue().getPath()));
	}

	private static class CircularReference {
		private final String path;
		
		private CircularReference(String path) {
			this.path = path;
		}
		
		public String getPath() {
			return path;
		}
	}
}
