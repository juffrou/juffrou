package org.juffrou.util.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}
