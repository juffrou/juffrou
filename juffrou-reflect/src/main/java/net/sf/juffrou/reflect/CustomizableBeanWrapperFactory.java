package net.sf.juffrou.reflect;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import net.sf.juffrou.reflect.internal.DefaultBeanInstanceCreator;

/**
 * This class is responsible for creating and caching BeanWrapperContexts.
 * <p>
 * A BeanWrapper uses the same BeanWrapperFactory to create the BeanWrapperContexts for its nested BeanWrappers.
 * BeanWrapperFactory can also be used as an umbrella to create BeanWrappers in a more performant way as it caches the
 * introspection information and re-uses it whenever possible.
 * 
 * @author cemartins
 */
public class CustomizableBeanWrapperFactory implements BeanWrapperFactory {

	private final Map<Type, BeanWrapperContext> classContextMap = new HashMap<Type, BeanWrapperContext>();

	// preferences info
	private BeanInstanceBuilder beanInstanceCreator = null;
	private BeanContextBuilder beanContextCreator = null;

	/**
	 * Retrieves a BeanWrapperContext for java bean class.
	 * <p>
	 * If the BeanWrapperContext is not in cache then creates a new one.
	 * 
	 * @param clazz
	 *            the bean class to inspect
	 * @return a BeanWrapperContext with introspection information about the specified class.
	 * @see {@link #getBeanWrapperContext(Class, Type...)}
	 */
	@Override
	public BeanWrapperContext getBeanWrapperContext(Class clazz) {
		return getBeanWrapperContext(clazz, null);
	}

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
	@Override
	public BeanWrapperContext getBeanWrapperContext(Class clazz, Type... types) {
		BeanWrapperContext context = classContextMap.get(clazz);
		if (context == null) {
			context = getBeanContextBuilder().build(this, clazz, types);
			classContextMap.put(clazz, context);
		}
		return context;
	}

	/**
	 * Construct a bean wrapper around a class.
	 * <p>
	 * Bean instances will be instances of that class and will be created only when necessary.<br>
	 * Try to use a cached BeanWrapperContext to save introspection time.
	 * 
	 * @param clazz
	 *            class to instantiate the wrapped bean
	 */
	@Override
	public JuffrouBeanWrapper getBeanWrapper(Class clazz) {
		return new JuffrouBeanWrapper(getBeanWrapperContext(clazz, null));
	}

	/**
	 * Construct a bean wrapper around an existing bean instance.
	 * <p>
	 * Will try to use a cached BeanWrapperContext to save introspection time.
	 * 
	 * @param instance
	 *            the bean object to be wrapped
	 */
	@Override
	public JuffrouBeanWrapper getBeanWrapper(Object instance) {
		return new JuffrouBeanWrapper(getBeanWrapperContext(instance.getClass(), null), instance);
	}

	@Override
	public BeanInstanceBuilder getBeanInstanceBuilder() {
		if (beanInstanceCreator == null)
			beanInstanceCreator = new DefaultBeanInstanceCreator();
		return beanInstanceCreator;
	}

	/**
	 * Control the instantiation of beans and wrapped beans.
	 * <p>
	 * The bean wrapper creates new instances using Class.newIntance() by default. You can use this this if you want to
	 * create class instances yourself.
	 * 
	 * @param beanInstanceBuilder
	 */
	public void setBeanInstanceBuilder(BeanInstanceBuilder beanInstanceBuilder) {
		this.beanInstanceCreator = beanInstanceBuilder;
	}

	private BeanContextBuilder getBeanContextBuilder() {
		if (beanContextCreator == null)
			beanContextCreator = new DefaultBeanContextCreator();
		return beanContextCreator;
	}

	/**
	 * Control the creation of BeanWrapperContexts.
	 * <p>
	 * Provide a custom BeanContextBuilder that will instantiate your custom BeanWrapperContext. This way you can extend
	 * the BeanWrapperContext class and attach more information to a bean.
	 * 
	 * @param beanContextBuilder
	 */
	public void setBeanContextBuilder(BeanContextBuilder beanContextBuilder) {
		this.beanContextCreator = beanContextBuilder;
	}

	private static class DefaultBeanContextCreator implements BeanContextBuilder {

		@Override
		public BeanWrapperContext build(CustomizableBeanWrapperFactory factory, Class clazz, Type... types) {
			return new BeanWrapperContext(factory, clazz, types);
		}

	}

}
