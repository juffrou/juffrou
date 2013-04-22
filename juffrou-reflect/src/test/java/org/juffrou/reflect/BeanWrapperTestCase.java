package org.juffrou.reflect;

import org.juffrou.error.BeanInstanceCreatorException;
import org.juffrou.util.reflect.BeanInstanceCreator;
import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.util.reflect.BeanWrapperContext;
import org.junit.Assert;
import org.junit.Test;


public class BeanWrapperTestCase {

	@Test
	public void testNestedBeanIntrospection() {
		String[] expectedPropertyNames = new String[] {"lastName", "birthDay", "firstName", "specialization"};
		BeanWrapperContext context = new BeanWrapperContext(Programmer.class);
		Programmer programmer = new Programmer();
		BeanWrapper bw = new BeanWrapper(context, programmer);
		String[] propertyNames = bw.getPropertyNames();
		Assert.assertArrayEquals(expectedPropertyNames, propertyNames);
	}
	
	@Test
	public void testBetterPerformanceWithContext() {
		int loop = 10000;
		long start = System.currentTimeMillis();
		for(int i=0; i < loop; i++) {
			BeanWrapper bw = new BeanWrapper(Programmer.class);
		}
		long stop = System.currentTimeMillis();
		Long noContext = new Long(stop - start);

		start = System.currentTimeMillis();
		BeanWrapperContext context = new BeanWrapperContext(Programmer.class);
		for(int i=0; i < loop; i++) {
			BeanWrapper bw = new BeanWrapper(context);
		}
		stop = System.currentTimeMillis();
		Long withContext = new Long(stop - start);
		Assert.assertTrue(withContext.longValue() < noContext.longValue());
		System.out.println(loop + " instantiations without context: " + noContext);
		System.out.println(loop + " instantiations with context: " + withContext);
	}
	
	@Test
	public void testCustomInstanceCreator() {
		BeanInstanceCreator iCreator = new BeanInstanceCreator() {
			@Override
			public Object newBeanInstance() throws BeanInstanceCreatorException {
				Programmer programmer = new Programmer();
				programmer.setLastName("Smith");
				return programmer;
			}
		};
		BeanWrapperContext context = new BeanWrapperContext(Programmer.class);
		context.setBeanInstanceCreator(iCreator);
		BeanWrapper bw = new BeanWrapper(context);
		bw.setValue("firstName", "John");
		Programmer programmer = (Programmer) bw.getBean();
		Assert.assertEquals("John", programmer.getFirstName());
		Assert.assertEquals("Smith", programmer.getLastName());
	}
	
	@Test
	public void testNestedWrapper() {
		BeanWrapperContext context = new BeanWrapperContext(Country.class);
		BeanWrapper bw = new BeanWrapper(context);
		bw.setValue("programmer.specialization", null);
		bw.setValue("president.genericProperty", null);
	}
}
