package org.juffrou.util.reflect;

import java.lang.reflect.Type;


/**
 * Used by the {@link BeanWrapper} to created contexts for its nested bean wrappers.<br>
 * Implement this interface if you want bean contexts to hold more bean metadata than the one used by bean wrapper.
 * @author cemartins
 */
public interface BeanContextCreator<T extends BeanWrapperContext> {

	@SuppressWarnings("rawtypes")
	T newBeanWrapperContext(Class clazz);
	
	@SuppressWarnings("rawtypes")
	T newBeanWrapperContext(Class clazz, Type...types);
}
