package net.sf.juffrou.util.reflect;

import java.lang.reflect.Type;


/**
 * Used by the {@link BeanWrapper} to created contexts for its nested bean wrappers.<br>
 * Implement this interface if you want bean contexts to hold more bean metadata than the one used by bean wrapper.
 * @author cemartins
 */
public interface BeanContextBuilder<T extends BeanWrapperContext> {

	/**
	 * Called by the BeanWrapperFactory to instantiate a new BeanWrapperContext
	 * @param bwFactory
	 * @param clazz
	 * @param types
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public T build(BeanWrapperFactory<T> bwFactory, Class clazz, Type...types);
}
