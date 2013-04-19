package com.googlecode.juffrou.util.reflect;

import com.googlecode.juffrou.error.BeanInstanceCreatorException;

/**
 * Used by the {@link BeanWrapperContext#newBeanInstance()} to instantiate the wrapped bean when necessary.<br>
 * Implement this interface and {@link BeanWrapperContext#setBeanInstanceCreator(BeanInstanceCreator)} to handle
 * bean instatiation.
 * @author cemartins
 */
public interface BeanInstanceCreator {

	Object newBeanInstance() throws BeanInstanceCreatorException ;
}
