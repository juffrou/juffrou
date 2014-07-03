package net.sf.juffrou.reflect;

import java.lang.reflect.Type;

public interface BeanWrapperFactory {

	/**
	 * Retrieves a BeanWrapperContext for java bean class.
	 * <p>
	 * If the BeanWrapperContext is not in cache then creates a new one.
	 * 
	 * @param clazz
	 *            the bean class to inspect
	 * @return a BeanWrapperContext with introspection information about the specified class.
	 */
	BeanWrapperContext getBeanWrapperContext(Class clazz);

	/**
	 * Retrieves a BeanWrapperContext for a parameterized (generic) java bean class.
	 * <p>
	 * If the BeanWrapperContext is not in cache then creates a new one.
	 * 
	 * @param clazz
	 *            the generic bean class to inspect.
	 * @param types
	 *            the parameters that defined the generic bean class.
	 * @return a BeanWrapperContext with introspection information about the specified class.
	 */
	BeanWrapperContext getBeanWrapperContext(Class clazz, Type... types);

	/**
	 * Construct a bean wrapper around a class.
	 * <p>
	 * Bean instances will be instances of that class and will be created only when necessary.<br>
	 * Try to use a cached BeanWrapperContext to save introspection time.
	 * 
	 * @param clazz
	 *            class to instantiate the wrapped bean
	 */
	JuffrouBeanWrapper getBeanWrapper(Class clazz);

	/**
	 * Construct a bean wrapper around an existing bean instance.
	 * <p>
	 * Will try to use a cached BeanWrapperContext to save introspection time.
	 * 
	 * @param instance
	 *            the bean object to be wrapped
	 */
	JuffrouBeanWrapper getBeanWrapper(Object instance);

	BeanInstanceBuilder getBeanInstanceBuilder();

}
