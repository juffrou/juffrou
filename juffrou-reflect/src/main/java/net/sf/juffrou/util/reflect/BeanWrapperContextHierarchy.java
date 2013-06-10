package net.sf.juffrou.util.reflect;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import net.sf.juffrou.error.BeanInstanceCreatorException;

/**
 * This class holds data that is common to a whole hierarchy of BeanWrapperContexts (ie to a complete graph of wrapped nested beans).<p>
 * The top BeanWrapperContext creates an instance of this class and all nested wrapper share the same instance
 * @author cemartins
 */
public class BeanWrapperContextHierarchy {

	private final Map<Type, BeanWrapperContext> classContextMap = new HashMap<Type, BeanWrapperContext>();
	
	// preferences info
	private BeanInstanceCreator beanInstanceCreator;
	private BeanContextCreator<? extends BeanWrapperContext> beanContextCreator;

	public BeanWrapperContextHierarchy() {
		
		beanInstanceCreator = new DefaultBeanInstanceCreator();
		beanContextCreator = new DefaultBeanContextCreator();
	}
	
	public void addTypeContext(Type type, BeanWrapperContext context) {
		classContextMap.put(type, context);
	}
	
	public BeanWrapperContext getTypeContext(Type type) {
		return classContextMap.get(type);
	}

	public BeanInstanceCreator getBeanInstanceCreator() {
		return beanInstanceCreator;
	}

	/**
	 * The bean wrapper creates new instances using Class.newIntance(). You can use this this if you want to create class instances yourself.  
	 * @param beanInstanceCreator
	 */
	public void setBeanInstanceCreator(BeanInstanceCreator beanInstanceCreator) {
		this.beanInstanceCreator = beanInstanceCreator;
	}
	
	public BeanContextCreator<? extends BeanWrapperContext> getBeanContextCreator() {
		return beanContextCreator;
	}
	public void setBeanContextCreator(
			BeanContextCreator<? extends BeanWrapperContext> beanContextCreator) {
		this.beanContextCreator = beanContextCreator;
	}

	private static class DefaultBeanInstanceCreator implements BeanInstanceCreator {
		@Override
		public Object newBeanInstance(Class clazz) throws BeanInstanceCreatorException {
			Object instance;
			try {
				instance = clazz.newInstance();
			} catch (InstantiationException e) {
				throw new BeanInstanceCreatorException(e);
			} catch (IllegalAccessException e) {
				throw new BeanInstanceCreatorException(e);
			}
			return instance;
		}
	}
	
	private static class DefaultBeanContextCreator implements BeanContextCreator<BeanWrapperContext> {

		@Override
		public BeanWrapperContext newBeanWrapperContext(BeanWrapperContextHierarchy hierarchyContext, Class clazz) {
			return new BeanWrapperContext(hierarchyContext, clazz);
		}

		@Override
		public BeanWrapperContext newBeanWrapperContext(BeanWrapperContextHierarchy hierarchyContext, Class clazz, Type... types) {
			return new BeanWrapperContext(hierarchyContext, clazz, types);
		}
		
	}
	

}
