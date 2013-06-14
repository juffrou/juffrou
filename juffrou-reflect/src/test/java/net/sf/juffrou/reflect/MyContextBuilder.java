package net.sf.juffrou.reflect;

import java.lang.reflect.Type;

import net.sf.juffrou.util.reflect.BeanContextBuilder;
import net.sf.juffrou.util.reflect.BeanWrapperFactory;

public class MyContextBuilder implements BeanContextBuilder<MyBeanWrapperContext> {

	@Override
	public MyBeanWrapperContext build(BeanWrapperFactory<MyBeanWrapperContext> hierarchyContext, Class clazz, Type... types) {

		MyBeanWrapperContext context = new MyBeanWrapperContext(hierarchyContext, clazz, types);
		return context;
	}

}
