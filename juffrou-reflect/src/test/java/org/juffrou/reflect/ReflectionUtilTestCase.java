package org.juffrou.reflect;

import java.lang.reflect.Type;

import junit.framework.Assert;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.util.reflect.BeanWrapperContext;
import org.junit.Test;

public class ReflectionUtilTestCase {

	@Test
	public void testIntatiateGeneric() {
		try {
			GenericPerson genericPerson = GenericPerson.class.newInstance();
			Assert.assertNotNull(genericPerson);
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGenericClassExtended() {
		BeanWrapperContext context = new BeanWrapperContext(GenericPerson.class);
		BeanWrapper bw = new BeanWrapper(context);
		Type type = bw.getType("genericProperty");
		Assert.assertTrue(Person.class.equals(type));
	}
	
	
	@Test
	public void testGenericClassInstance() {
		
		BeanWrapperContext context = new BeanWrapperContext(GenericBean.class, Person.class);
		GenericBean<Person> genericPerson = new GenericBean<Person>();
		BeanWrapper bw = new BeanWrapper(context, genericPerson);
		Type type = bw.getType("genericProperty");
		Assert.assertTrue(Person.class.equals(type));
	}

}
