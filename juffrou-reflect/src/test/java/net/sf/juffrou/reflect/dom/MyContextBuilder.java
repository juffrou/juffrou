package net.sf.juffrou.reflect.dom;

import net.sf.juffrou.reflect.BeanContextBuilder;
import net.sf.juffrou.reflect.CustomizableBeanWrapperFactory;

import java.lang.reflect.Type;

public class MyContextBuilder implements BeanContextBuilder {

	@Override
	public MyBeanWrapperContext build(CustomizableBeanWrapperFactory hierarchyContext, Class clazz, Type... types) {

		MyBeanWrapperContext context = new MyBeanWrapperContext(hierarchyContext, clazz, types);
		return context;
	}

}
