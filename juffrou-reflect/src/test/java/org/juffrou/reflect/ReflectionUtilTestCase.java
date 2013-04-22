package org.juffrou.reflect;

import junit.framework.Assert;

import org.juffrou.util.reflect.ReflectionUtil;
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
	
	public void testGenericClassExtended() {
//		ReflectionUtil.getTypeArgumentsMap(Class.class, GenericPerson.class);
	}
}
