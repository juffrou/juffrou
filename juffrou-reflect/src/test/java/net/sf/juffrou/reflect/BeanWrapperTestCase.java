package net.sf.juffrou.reflect;

import java.lang.reflect.Type;

import net.sf.juffrou.error.BeanInstanceBuilderException;
import net.sf.juffrou.util.reflect.BeanInstanceBuilder;
import net.sf.juffrou.util.reflect.BeanWrapper;
import net.sf.juffrou.util.reflect.BeanWrapperContext;
import net.sf.juffrou.util.reflect.BeanWrapperFactory;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeanWrapperImpl;


public class BeanWrapperTestCase {

	@Test
	public void testNestedBeanIntrospection() {
		String[] expectedPropertyNames = new String[] {"firstName", "lastName", "birthDay", "specialization"};
		BeanWrapperContext context = BeanWrapperContext.create(Programmer.class);
		Programmer programmer = new Programmer();
		BeanWrapper bw = new BeanWrapper(context, programmer);
		String[] propertyNames = bw.getPropertyNames();
		Assert.assertArrayEquals(expectedPropertyNames, propertyNames);
	}
	
	@Test
	public void testInquireBeanWraper() {
		BeanWrapperContext context = BeanWrapperContext.create(Programmer.class);
		BeanWrapper beanWrapper = new BeanWrapper(context);
		for(String propertyName : beanWrapper.getPropertyNames()) {
			Type type = beanWrapper.getType(propertyName);
			Object value = beanWrapper.getValue(propertyName);
			System.out.println(type + ": " + value);
		}
	}
	
	@Test
	public void testBetterPerformanceWithContext() {
		// create bean wrapper directly
		int loop = 10000;
		long start = System.currentTimeMillis();
		for(int i=0; i < loop; i++) {
			BeanWrapper bw = new BeanWrapper(Programmer.class);
		}
		long stop = System.currentTimeMillis();
		Long noContext = new Long(stop - start);

		// create a bean wrapper context and use that to create the bean wrappers
		start = System.currentTimeMillis();
		BeanWrapperContext context = BeanWrapperContext.create(Programmer.class);
		for(int i=0; i < loop; i++) {
			BeanWrapper bw = new BeanWrapper(context);
		}
		stop = System.currentTimeMillis();
		Long withContext = new Long(stop - start);

		start = System.currentTimeMillis();
		BeanWrapperFactory factory = new BeanWrapperFactory();
		for(int i=0; i < loop; i++) {
			BeanWrapper bw = factory.getBeanWrapper(Programmer.class);
		}
		stop = System.currentTimeMillis();
		Long withFactory = new Long(stop - start);

		// compare with Springs BeanWrapperImpl
		start = System.currentTimeMillis();
		for(int i=0; i < loop; i++) {
			BeanWrapperImpl bw = new BeanWrapperImpl(Programmer.class);
		}
		stop = System.currentTimeMillis();
		Long spring = new Long(stop - start);

		
		Assert.assertTrue(withContext.longValue() < noContext.longValue());
		System.out.println(loop + " instantiations without context: " + noContext);
		System.out.println(loop + " instantiations with context: " + withContext);
		System.out.println(loop + " instantiations with factory: " + withFactory);
		System.out.println(loop + " instantiations with spring BeanWrapperImpl: " + spring);
	}

	@Test
	public void testPerformanceWithBeanManipulation() {
		// create bean wrapper directly
		int loop = 10000;
		long start = System.currentTimeMillis();
		for(int i=0; i < loop; i++) {
			BeanWrapper bw = new BeanWrapper(PersonCircular.class);
			bw.setValue("firstName", "Carlos");
			bw.setValue("address.street", "Bean street");
		}
		long stop = System.currentTimeMillis();
		Long noContext = new Long(stop - start);

		// create a bean wrapper context and use that to create the bean wrappers
		start = System.currentTimeMillis();
		BeanWrapperContext context = BeanWrapperContext.create(PersonCircular.class);
		for(int i=0; i < loop; i++) {
			BeanWrapper bw = new BeanWrapper(context);
			bw.setValue("firstName", "Carlos");
			bw.setValue("address.street", "Bean street");
		}
		stop = System.currentTimeMillis();
		Long withContext = new Long(stop - start);

		start = System.currentTimeMillis();
		BeanWrapperFactory<BeanWrapperContext> factory = new BeanWrapperFactory<BeanWrapperContext>();
		for(int i=0; i < loop; i++) {
			BeanWrapper bw = factory.getBeanWrapper(PersonCircular.class);
			bw.setValue("firstName", "Carlos");
			bw.setValue("address.street", "Bean street");
		}
		stop = System.currentTimeMillis();
		Long withFactory = new Long(stop - start);

		// compare with Springs BeanWrapperImpl
		start = System.currentTimeMillis();
		for(int i=0; i < loop; i++) {
			BeanWrapperImpl bw = new BeanWrapperImpl(PersonCircular.class);
			bw.setAutoGrowNestedPaths(true);
			bw.setPropertyValue("firstName", "Carlos");
			bw.setPropertyValue("address.street", "Bean street");
		}
		stop = System.currentTimeMillis();
		Long spring = new Long(stop - start);

		
		Assert.assertTrue(withContext.longValue() < noContext.longValue());
		System.out.println(loop + " instantiations without context: " + noContext);
		System.out.println(loop + " instantiations with context: " + withContext);
		System.out.println(loop + " instantiations with factory: " + withFactory);
		System.out.println(loop + " instantiations with spring BeanWrapperImpl: " + spring);
	}

	@Test
	public void testCustomInstanceCreator() {
		BeanInstanceBuilder iCreator = new BeanInstanceBuilder() {
			@Override
			public Object build(Class clazz) throws BeanInstanceBuilderException {
				Programmer programmer = new Programmer();
				programmer.setLastName("Smith");
				return programmer;
			}
		};
		BeanWrapperContext context = BeanWrapperContext.create(Programmer.class);
		context.setBeanInstanceBuilder(iCreator);
		BeanWrapper bw = new BeanWrapper(context);
		bw.setValue("firstName", "John");
		Programmer programmer = (Programmer) bw.getBean();
		Assert.assertEquals("John", programmer.getFirstName());
		Assert.assertEquals("Smith", programmer.getLastName());
	}
	
	@Test
	public void testNestedWrapper() {
		BeanWrapperContext context = BeanWrapperContext.create(Country.class);
		BeanWrapper bw = new BeanWrapper(context);
		bw.setValue("programmer.specialization", null);
		bw.setValue("president.genericProperty", null);
	}
	
	@Test
	public void testCircularReferences() {
		BeanWrapperContext context = BeanWrapperContext.create(PersonCircular.class);
		
		PersonCircular person = new PersonCircular();
		person.setFirstName("Carlos");
		person.setLastName("Martins");
		
		BeanWrapper bw = new BeanWrapper(context, person);
		
		bw.setValue("address.street", "Bean street");
		String value = (String) bw.getValue("address.person.lastName");
		
		Assert.assertEquals("Martins", value);
	}
	
	@Test
	public void testBeanContextCreator() {
		BeanWrapperFactory<MyBeanWrapperContext> factory = new BeanWrapperFactory<MyBeanWrapperContext>();
		factory.setBeanContextBuilder(new MyContextBuilder());
		BeanWrapper myPersonWrapper = factory.getBeanWrapper(Person.class);
		MyBeanWrapperContext context = (MyBeanWrapperContext) myPersonWrapper.getContext();
		
	}
}
