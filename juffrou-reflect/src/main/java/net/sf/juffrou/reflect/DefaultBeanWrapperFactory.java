package net.sf.juffrou.reflect;

import net.sf.juffrou.reflect.internal.DefaultBeanInstanceCreator;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * This class is responsible for creating and caching BeanWrapperContexts.
 * <p>
 * A BeanWrapper uses the same BeanWrapperFactory to create the BeanWrapperContexts for its nested BeanWrappers.
 * BeanWrapperFactory can also be used as an umbrella to create BeanWrappers in a more performant way as it caches the
 * introspection information and re-uses it whenever possible.
 * 
 * @author cemartins
 */
public class DefaultBeanWrapperFactory implements BeanWrapperFactory {

	static final Map<Type, WeakReference<BeanWrapperContext>> classContextMap = Collections.synchronizedMap(new WeakHashMap<Type, WeakReference<BeanWrapperContext>>());

	// preferences info
	private final BeanInstanceBuilder beanInstanceCreator = new DefaultBeanInstanceCreator();

	/**
	 * Retrieves a BeanWrapperContext for java bean class.
	 * <p>
	 * If the BeanWrapperContext is not in cache then creates a new one.
	 * 
	 * @param clazz
	 *            the bean class to inspect
	 * @return a BeanWrapperContext with introspection information about the specified class.
	 */
	@SuppressWarnings("rawtypes")
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
		WeakReference<BeanWrapperContext> contextReference = classContextMap.get(clazz);
		BeanWrapperContext context = contextReference != null ? contextReference.get() : null;
		if (context == null) {
			context = new BeanWrapperContext(this, clazz, types);
			classContextMap.put(clazz, new WeakReference<BeanWrapperContext>(context));
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
		return new JuffrouBeanWrapper(getBeanWrapperContext(clazz));
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
		return beanInstanceCreator;
	}

}
