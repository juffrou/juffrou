package net.sf.juffrou.reflect.internal;

import net.sf.juffrou.reflect.BeanInstanceBuilder;
import net.sf.juffrou.reflect.error.BeanInstanceBuilderException;

public class DefaultBeanInstanceCreator implements BeanInstanceBuilder {

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
