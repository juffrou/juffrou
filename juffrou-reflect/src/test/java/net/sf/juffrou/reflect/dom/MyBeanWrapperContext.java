package net.sf.juffrou.reflect.dom;

import net.sf.juffrou.reflect.BeanWrapperContext;
import net.sf.juffrou.reflect.CustomizableBeanWrapperFactory;

import java.lang.reflect.Type;

public class MyBeanWrapperContext extends BeanWrapperContext {

	//TODO create properties to extend the context
	
	public MyBeanWrapperContext(CustomizableBeanWrapperFactory factory, Class clazz, Type... types) {
		super(factory, clazz, types);
		//TODO some initialization
	}
}
