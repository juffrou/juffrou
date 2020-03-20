package net.sf.juffrou.reflect;

import net.sf.juffrou.reflect.internal.BeanFieldHandler;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 * Spring framework's property descriptor for Juffrou BeanWrapper's properties
 * @author cemartins
 *
 */
public class JuffrouPropertyDescriptor extends PropertyDescriptor {
	
	public JuffrouPropertyDescriptor(Class<?> beanClass, BeanFieldHandler beanFieldHandler) throws IntrospectionException {
		super(beanFieldHandler.getField().getName(), beanFieldHandler.getReadMethod(beanClass), beanFieldHandler.getWriteMethod(beanClass));
	}
}
