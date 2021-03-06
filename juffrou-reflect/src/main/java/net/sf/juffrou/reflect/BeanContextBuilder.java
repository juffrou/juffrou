package net.sf.juffrou.reflect;

import java.lang.reflect.Type;


/**
 * Used by the {@link JuffrouBeanWrapper} to created contexts for its nested bean wrappers.<br>
 * Implement this interface if you want bean contexts to hold more bean metadata than the one used by bean wrapper.
 * @author cemartins
 */
public interface BeanContextBuilder {

	/**
	 * Called by the BeanWrapperFactory to instantiate a new BeanWrapperContext
	 * @param bwFactory and instance of CustomizableBeanWrapperFactory
	 * @param clazz the Class to instantiate
	 * @param types the parameters that defined the generic Class, or null if clazz is not generic
	 * @return a bean wrapper context with bean metadata
	 */
	@SuppressWarnings("rawtypes")
	BeanWrapperContext build(CustomizableBeanWrapperFactory bwFactory, Class clazz, Type...types);
}
