package net.sf.juffrou.reflect.internal;

import net.sf.juffrou.reflect.BeanWrapperContext;
import net.sf.juffrou.reflect.JuffrouBeanWrapper;
import net.sf.juffrou.reflect.error.ReflectionException;

import java.lang.reflect.*;



public class BeanFieldHandler {

	private final BeanWrapperContext context;
	private final Field field;
	private final Class<?> ftype;
	private final Type genericType;
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
		this.ftype = field.getType();
		this.genericType = t;

	}

	public BeanFieldHandler(BeanWrapperContext context, Method getterMethod, Method setterMethod) {
		this.context = context;
		this.field = null;
		this.getter = getterMethod;
		this.setter = setterMethod;
		
		Type t = getterMethod != null ? getterMethod.getGenericReturnType() : setterMethod.getGenericParameterTypes()[0];
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
		this.ftype = getterMethod != null ? getterMethod.getReturnType() : setterMethod.getParameterTypes()[0];
		this.genericType = t;

	}

	public Field getField() {
		return this.field;
	}

	public Class<?> getType() {
		return ftype;
	}

	public Type getGenericType() {
		return genericType;
	}

	public Type[] getTypeArguments() {
		return ftypeArguments;
	}

	public Object getValue(JuffrouBeanWrapper bw) {

		if (getter == null)
			getter = inspectReadMethod(bw.getBeanClass(), field.getName(), ftype);
		
		try {
			return getter.invoke(bw.getBean(), null);
		} catch (IllegalAccessException e) {
			throw new ReflectionException(e);
		} catch (InvocationTargetException e) {
			throw new ReflectionException(e);
		} 

	}

	public void setValue(JuffrouBeanWrapper bw, Object value) {

		if (setter == null)
			setter = inspectWriteMethod(bw.getBeanClass(), field.getName(), ftype);

		try {
			
			setter.invoke(bw.getBean(), value);
			
		} catch (IllegalAccessException e) {
			throw new ReflectionException(e);
		} catch (InvocationTargetException e) {
			throw new ReflectionException(e);
		}
	}

	public void setValueIfBeanField(JuffrouBeanWrapper bw, Object value) {
		if (getter != null || setter != null) {
			try {
				setValue(bw, value);
			} catch (ReflectionException e) {
			}
		}

	}
	
	public static Method inspectReadMethod(Class<?> beanClass, String fieldName, Class<?> fieldClass) {
		Method getterMethod;
		String name = fieldName;
		String methodName = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
		try {
			getterMethod = beanClass.getMethod(methodName, null);
			return getterMethod;
		} catch (NoSuchMethodException e) {
			
			// try the boolean "is" pattern
			if(fieldClass == boolean.class || fieldClass == null) {
				if(name.startsWith("is"))
					name = name.substring(2);
				methodName = "is" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
				try {
					getterMethod = beanClass.getMethod(methodName, null);
					return getterMethod;
				} catch (NoSuchMethodException e1) {
					throw new ReflectionException("The class " + beanClass.getSimpleName()	+ " does not have a getter method for the field " + fieldName);
				}
			}
			else
				throw new ReflectionException("The class " + beanClass.getSimpleName()	+ " does not have a getter method for the field " + fieldName);

		}
	}

	public static Method inspectWriteMethod(Class<?> beanClass, String fieldName, Class<?> fieldClass) {
		String name = fieldName;
		String methodName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
		try {
			if(fieldClass == null) {
				for(Method method : beanClass.getMethods())
					if(method.getName().equals(methodName))
						return method;
				throw new ReflectionException("The class " + beanClass.getSimpleName() + " does not have a setter method for the field " + fieldName);
			}
			else
				return beanClass.getMethod(methodName, fieldClass);
		} catch (NoSuchMethodException e) {
			
			// try the boolean "is" pattern
			if(fieldClass == boolean.class) {
				if(name.startsWith("is"))
					name = name.substring(2);
				methodName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
				try {
					return beanClass.getMethod(methodName, fieldClass);
				} catch (NoSuchMethodException e1) {
					throw new ReflectionException("The class " + beanClass.getSimpleName() + " does not have a setter method for the field " + fieldName);
				}
			}
			else
				throw new ReflectionException("The class " + beanClass.getSimpleName() + " does not have a setter method for the field " + fieldName);
		}
	}
	
	public Method getReadMethod(Class<?> beanClass) {
		if(getter == null)
			getter = inspectReadMethod(beanClass, field.getName(), ftype);
		
		return getter;
	}
	
	public Method getWriteMethod(Class<?> beanClass) {
		if(setter == null)
			setter = inspectWriteMethod(beanClass, field.getName(), ftype);
		
		return setter;
	}
}
