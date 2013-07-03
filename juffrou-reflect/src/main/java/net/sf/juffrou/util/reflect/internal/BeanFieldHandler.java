package net.sf.juffrou.util.reflect.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import net.sf.juffrou.error.ReflectionException;
import net.sf.juffrou.util.reflect.BeanWrapper;
import net.sf.juffrou.util.reflect.BeanWrapperContext;



public class BeanFieldHandler {

	private final BeanWrapperContext context;
	private final Field field;
	private final Type ftype;
	private final Type[] ftypeArguments;
	private Method getter = null;
	private Method setter = null;

	public BeanFieldHandler(BeanWrapperContext context, Field field) {
		this.context = context;
		this.field = field;
		Type t = field.getGenericType();
		if (t instanceof TypeVariable) {
			t = context.getTypeArgumentsMap().get(t);
			if(t == null)
				t = Object.class;
		}
		if (t instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) t;
			this.ftypeArguments = pt.getActualTypeArguments();
		} else {
			this.ftypeArguments = null;
		}
		this.ftype = t;

	}

	public Field getField() {
		return this.field;
	}

	public Type getType() {
		return ftype;
	}

	public Type[] getTypeArguments() {
		return ftypeArguments;
	}

	public Object getValue(BeanWrapper bw) {

		if (getter == null) {
			String name = field.getName();
			String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
			try {
				getter = bw.getBeanClass().getMethod(methodName, null);
			} catch (NoSuchMethodException e) {
				
				// try the boolean "is" pattern
				if(this.field.getType() == boolean.class) {
					if(name.startsWith("is"))
						name = name.substring(2);
					methodName = "is" + name.substring(0, 1).toUpperCase() + name.substring(1);
					try {
						getter = bw.getBeanClass().getMethod(methodName, null);
					} catch (NoSuchMethodException e1) {
						throw new ReflectionException("The class " + bw.getBeanClass().getSimpleName()	+ " does not have a getter method for the field " + field.getName());
					}
				}
				else
					throw new ReflectionException("The class " + bw.getBeanClass().getSimpleName()	+ " does not have a getter method for the field " + field.getName());

			}
		}
		try {
			
			return getter.invoke(bw.getBean(), null);
			
		} catch (IllegalAccessException e) {
			throw new ReflectionException(e);
		} catch (InvocationTargetException e) {
			throw new ReflectionException(e);
		} 

	}

	public void setValue(BeanWrapper bw, Object value) {

		if (setter == null) {
			String name = field.getName();
			String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
			try {
				setter = bw.getBeanClass().getMethod(methodName, (Class<?>) this.field.getType());
			} catch (NoSuchMethodException e) {
				
				// try the boolean "is" pattern
				if(this.field.getType() == boolean.class) {
					if(name.startsWith("is"))
						name = name.substring(2);
					methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
					try {
						setter = bw.getBeanClass().getMethod(methodName, (Class<?>) this.field.getType());
					} catch (NoSuchMethodException e1) {
						throw new ReflectionException("The class " + bw.getBeanClass().getSimpleName() + " does not have a setter method for the field " + field.getName());
					}
				}
				else
					throw new ReflectionException("The class " + bw.getBeanClass().getSimpleName() + " does not have a setter method for the field " + field.getName());
			}
		}

		try {
			
			setter.invoke(bw.getBean(), value);
			
		} catch (IllegalAccessException e) {
			throw new ReflectionException(e);
		} catch (InvocationTargetException e) {
			throw new ReflectionException(e);
		}
	}

	public void setValueIfBeanField(BeanWrapper bw, Object value) {
		if (getter != null || setter != null) {
			try {
				setValue(bw, value);
			} catch (ReflectionException e) {
			}
		}

	}

}
