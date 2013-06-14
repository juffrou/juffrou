package net.sf.juffrou.reflect;

import java.lang.reflect.Type;

import net.sf.juffrou.util.reflect.BeanWrapperContext;
import net.sf.juffrou.util.reflect.BeanWrapperFactory;

public class MyBeanWrapperContext extends BeanWrapperContext {

	//TODO create properties to extend the context
	
	public MyBeanWrapperContext(BeanWrapperFactory factory, Class clazz, Type... types) {
		super(factory, clazz, types);
		//TODO some initialization
	}
}
