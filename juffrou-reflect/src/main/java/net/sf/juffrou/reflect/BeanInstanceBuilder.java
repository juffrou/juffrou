package net.sf.juffrou.reflect;

import net.sf.juffrou.reflect.error.BeanInstanceBuilderException;

/**
 * Used by the {@link BeanWrapperContext#newBeanInstance()} to instantiate the wrapped bean when necessary.<br>
 * Implement this interface and {@link BeanWrapperContext#setBeanInstanceCreator(BeanInstanceCreator)} to handle
 * bean instatiation.
 * @author cemartins
 */
public interface BeanInstanceBuilder {

	@SuppressWarnings("rawtypes")
	public Object build(Class clazz) throws BeanInstanceBuilderException ;
}
