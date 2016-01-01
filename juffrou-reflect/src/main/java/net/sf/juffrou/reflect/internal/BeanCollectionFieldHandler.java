package net.sf.juffrou.reflect.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;

import net.sf.juffrou.reflect.BeanWrapperContext;
import net.sf.juffrou.reflect.JuffrouBeanWrapper;
import net.sf.juffrou.reflect.error.ReflectionException;

public class BeanCollectionFieldHandler extends BeanFieldHandler {

	private enum AccessType {DIRECT, THROUGHBEAN;}; 
	
	private AccessType accessType;
	private Method adder = null;
	private Method remover = null;

	public BeanCollectionFieldHandler(BeanWrapperContext context, Field field) {
		super(context, field);
		
		if(getTypeArguments() != null)
			accessType = AccessType.THROUGHBEAN;
		else
			accessType = AccessType.DIRECT;
	}

	public BeanCollectionFieldHandler(BeanWrapperContext context,
			Method getterMethod, Method setterMethod) {
		super(context, getterMethod, setterMethod);
		
		if(getTypeArguments() != null)
			accessType = AccessType.THROUGHBEAN;
		else
			accessType = AccessType.DIRECT;
	}

	public void addElement(JuffrouBeanWrapper bw, Object element) {
		if (accessType == AccessType.THROUGHBEAN && adder == null) {
			
			adder = inspectMethod(bw.getBeanClass(), getField().getName(), "add");
			if(adder != null)
				try {
					
					adder.invoke(bw.getBean(), element);
					
					return;
					
				} catch (IllegalAccessException e) {
					throw new ReflectionException(e);
				} catch (InvocationTargetException e) {
					throw new ReflectionException(e);
				}
		}

		// if the code reaches here, there is no adder method in the main bean. Try adding to the collection directly
		addDirectlyToCollection(bw, element);
	}

	public void removeElement(JuffrouBeanWrapper bw, Object element) {
		if (accessType == AccessType.THROUGHBEAN && remover == null) {
			
			remover = inspectMethod(bw.getBeanClass(), getField().getName(), "remove");
			if(remover != null)
				try {
					
					remover.invoke(bw.getBean(), element);
					
					return;
					
				} catch (IllegalAccessException e) {
					throw new ReflectionException(e);
				} catch (InvocationTargetException e) {
					throw new ReflectionException(e);
				}
		}

		// if the code reaches here, there is no remover method in the main bean. Try removing from the collection directly
		removeDirectlyFromCollection(bw, element);
		
	}

	private void addDirectlyToCollection(JuffrouBeanWrapper bw, Object element) {
		Collection collection = (Collection) getValue(bw);
		collection.add(element);
	}

	private void removeDirectlyFromCollection(JuffrouBeanWrapper bw, Object element) {
		Collection collection = (Collection) getValue(bw);
		collection.remove(element);
	}

	
	public  Method inspectMethod(Class<?> beanClass, String fieldName, String methodPrefix) {
		
		Type type = getTypeArguments()[0];
	
		if(type != null && type instanceof Class) {
			Class<?> argumentClass = (Class<?>) type;

			String methodName = methodPrefix + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
			try {
				return beanClass.getMethod(methodName, argumentClass);
			} catch (NoSuchMethodException | SecurityException e) {
				// proceed
			}
		}
		
		// if the code reaches here, then there is no addMethod in the holding bean
		accessType = AccessType.DIRECT;
		return null;
	}

}
