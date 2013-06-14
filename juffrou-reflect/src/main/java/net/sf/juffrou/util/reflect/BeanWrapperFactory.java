package net.sf.juffrou.util.reflect;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import net.sf.juffrou.error.BeanInstanceBuilderException;

/**
 * This class holds data that is common to a whole hierarchy of BeanWrapperContexts (ie to a complete graph of wrapped nested beans).<p>
 * The top BeanWrapperContext creates an instance of this class and all nested wrapper share the same instance
 * @author cemartins
 */
public class BeanWrapperFactory<BWCType extends BeanWrapperContext> {

	private final Map<Type, BWCType> classContextMap = new HashMap<Type, BWCType>();
	
	// preferences info
	private BeanInstanceBuilder beanInstanceCreator = null;
	private BeanContextBuilder<BWCType> beanContextCreator = null;

	public BWCType getBeanWrapperContext(Class clazz) {
		return getBeanWrapperContext(clazz, null);
	}
	
	public BWCType getBeanWrapperContext(Class clazz, Type... types) {
		BWCType context = classContextMap.get(clazz);
		if(context == null) {
			context = getBeanContextBuilder().build(this, clazz, types);
			classContextMap.put(clazz,  context);
		}
		return context;
	}

	/**
	 * Construct a bean wrapper around a class. Bean instances will be instances of that class and will be created only when necessary.
	 * @param clazz class to instantiate the wrapped bean
	 */
	public BeanWrapper getBeanWrapper(Class clazz) {
		return new BeanWrapper(getBeanWrapperContext(clazz));
	}
	
	/**
	 * Construct a bean wrapper around an existing bean instance.
	 * @param instance
	 */
	public BeanWrapper getBeanWrapper(Object instance) {
		return new BeanWrapper(getBeanWrapperContext(instance.getClass()), instance);
	}

	protected BeanInstanceBuilder getBeanInstanceBuilder() {
		if(beanInstanceCreator == null)
			beanInstanceCreator = new DefaultBeanInstanceCreator();
		return beanInstanceCreator;
	}

	/**
	 * The bean wrapper creates new instances using Class.newIntance(). You can use this this if you want to create class instances yourself.  
	 * @param beanInstanceCreator
	 */
	public void setBeanInstanceBuilder(BeanInstanceBuilder beanInstanceCreator) {
		this.beanInstanceCreator = beanInstanceCreator;
	}
	
	private BeanContextBuilder<BWCType> getBeanContextBuilder() {
		if(beanContextCreator == null)
			beanContextCreator = new DefaultBeanContextCreator<BWCType>();
		return beanContextCreator;
	}
	public void setBeanContextBuilder(
			BeanContextBuilder<? extends BeanWrapperContext> beanContextCreator) {
		this.beanContextCreator = (BeanContextBuilder<BWCType>) beanContextCreator;
	}

	private static class DefaultBeanInstanceCreator implements BeanInstanceBuilder {
		@Override
		public Object build(Class clazz) throws BeanInstanceBuilderException {
			Object instance;
			try {
				instance = clazz.newInstance();
			} catch (InstantiationException e) {
				throw new BeanInstanceBuilderException(e);
			} catch (IllegalAccessException e) {
				throw new BeanInstanceBuilderException(e);
			}
			return instance;
		}
	}
	
	private static class DefaultBeanContextCreator<BCType extends BeanWrapperContext> implements BeanContextBuilder<BCType> {

		@Override
		public BCType build(BeanWrapperFactory factory, Class clazz, Type... types) {
			return (BCType) new BeanWrapperContext(factory, clazz, types);
		}
		
	}
	

}
