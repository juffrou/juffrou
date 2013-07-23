package net.sf.juffrou.reflect;

import java.lang.reflect.Type;

import net.sf.juffrou.reflect.dom.BooleanHolder;
import net.sf.juffrou.reflect.dom.Country;
import net.sf.juffrou.reflect.dom.MyBeanWrapperContext;
import net.sf.juffrou.reflect.dom.MyContextBuilder;
import net.sf.juffrou.reflect.dom.Person;
import net.sf.juffrou.reflect.dom.PersonCircular;
import net.sf.juffrou.reflect.dom.Programmer;
import net.sf.juffrou.reflect.error.BeanInstanceBuilderException;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeanWrapperImpl;

public class BeanWrapperTestCase {

	@Test
	public void testNestedBeanIntrospection() {
		String[] expectedPropertyNames = new String[] { "firstName", "lastName", "birthDay", "home", "specialization" };
		BeanWrapperContext context = BeanWrapperContext.create(Programmer.class);
		Programmer programmer = new Programmer();
		JuffrouBeanWrapper bw = new JuffrouBeanWrapper(context, programmer);
		String[] propertyNames = bw.getPropertyNames();
		Assert.assertArrayEquals(expectedPropertyNames, propertyNames);
	}

	@Test
	public void testInquireBeanWraper() {
		BeanWrapperContext context = BeanWrapperContext.create(Programmer.class);
		JuffrouBeanWrapper beanWrapper = new JuffrouBeanWrapper(context);
		for (String propertyName : beanWrapper.getPropertyNames()) {
			Type type = beanWrapper.getType(propertyName);
			Object value = beanWrapper.getValue(propertyName);
			System.out.println(type + ": " + value);
		}
	}

	@Test
	public void testBeanWraperUseCase() {
		JuffrouBeanWrapper beanWrapper = new JuffrouBeanWrapper(BeanWrapperContext.create(Programmer.class)); // Programmer extends Person
		beanWrapper.setValue("specialization", "Bean Wrappers :)"); // set value to Programmer's property
		beanWrapper.setValue("firstName", "Carlos"); // set value to Person's property
		beanWrapper.setValue("home.town", "Lisboa"); // set value to a nested bean's property
		for (String propertyName : beanWrapper.getPropertyNames()) {
			Type type = beanWrapper.getType(propertyName);
			Object value = beanWrapper.getValue(propertyName);
			System.out.println(type + ": " + value);
		}
		Programmer programmer = (Programmer) beanWrapper.getBean(); // get the wrapped object
		BeanWrapperContext context = beanWrapper.getContext(); // Reuse the context with cached introspection and save time
	}

	@Test
	public void testSetWrongPropertyType() {
		BeanWrapperContext context = BeanWrapperContext.create(Person.class);
		JuffrouBeanWrapper beanWrapper = new JuffrouBeanWrapper(context);
		try {
			beanWrapper.setValue("birthDay", "SOMEVALUE");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testBetterPerformanceWithContext() {
		// create bean wrapper directly
		int loop = 10000;
		long start = System.currentTimeMillis();
		for (int i = 0; i < loop; i++) {
			JuffrouBeanWrapper bw = new JuffrouBeanWrapper(Programmer.class);
		}
		long stop = System.currentTimeMillis();
		Long noContext = new Long(stop - start);

		// create a bean wrapper context and use that to create the bean wrappers
		start = System.currentTimeMillis();
		BeanWrapperContext context = BeanWrapperContext.create(Programmer.class);
		for (int i = 0; i < loop; i++) {
			JuffrouBeanWrapper bw = new JuffrouBeanWrapper(context);
		}
		stop = System.currentTimeMillis();
		Long withContext = new Long(stop - start);

		start = System.currentTimeMillis();
		CustomizableBeanWrapperFactory factory = new CustomizableBeanWrapperFactory();
		for (int i = 0; i < loop; i++) {
			JuffrouBeanWrapper bw = factory.getBeanWrapper(Programmer.class);
		}
		stop = System.currentTimeMillis();
		Long withFactory = new Long(stop - start);

		// compare with Springs BeanWrapperImpl
		start = System.currentTimeMillis();
		for (int i = 0; i < loop; i++) {
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
		for (int i = 0; i < loop; i++) {
			JuffrouBeanWrapper bw = new JuffrouBeanWrapper(PersonCircular.class);
			bw.setValue("firstName", "Carlos");
			bw.setValue("address.street", "Bean street");
		}
		long stop = System.currentTimeMillis();
		Long noContext = new Long(stop - start);

		// create a bean wrapper context and use that to create the bean wrappers
		start = System.currentTimeMillis();
		BeanWrapperContext context = BeanWrapperContext.create(PersonCircular.class);
		for (int i = 0; i < loop; i++) {
			JuffrouBeanWrapper bw = new JuffrouBeanWrapper(context);
			bw.setValue("firstName", "Carlos");
			bw.setValue("address.street", "Bean street");
		}
		stop = System.currentTimeMillis();
		Long withContext = new Long(stop - start);

		start = System.currentTimeMillis();
		CustomizableBeanWrapperFactory factory = new CustomizableBeanWrapperFactory();
		for (int i = 0; i < loop; i++) {
			JuffrouBeanWrapper bw = factory.getBeanWrapper(PersonCircular.class);
			bw.setValue("firstName", "Carlos");
			bw.setValue("address.street", "Bean street");
		}
		stop = System.currentTimeMillis();
		Long withFactory = new Long(stop - start);

		// compare with Springs BeanWrapperImpl
		start = System.currentTimeMillis();
		for (int i = 0; i < loop; i++) {
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
		CustomizableBeanWrapperFactory factory = new CustomizableBeanWrapperFactory();
		factory.setBeanInstanceBuilder(iCreator);
		BeanWrapperContext context = BeanWrapperContext.create(Programmer.class);
		JuffrouBeanWrapper bw = factory.getBeanWrapper(Programmer.class);
		bw.setValue("firstName", "John");
		Programmer programmer = (Programmer) bw.getBean();
		Assert.assertEquals("John", programmer.getFirstName());
		Assert.assertEquals("Smith", programmer.getLastName());
	}

	@Test
	public void testNestedWrapper() {
		BeanWrapperContext context = BeanWrapperContext.create(Country.class);
		JuffrouBeanWrapper bw = new JuffrouBeanWrapper(context);
		bw.setValue("programmer.specialization", null);
		bw.setValue("president.genericProperty", null);
	}

	@Test
	public void testCircularReferences() {
		BeanWrapperContext context = BeanWrapperContext.create(PersonCircular.class);

		PersonCircular person = new PersonCircular();
		person.setFirstName("Carlos");
		person.setLastName("Martins");

		JuffrouBeanWrapper bw = new JuffrouBeanWrapper(context, person);

		bw.setValue("address.street", "Bean street");
		String value = (String) bw.getValue("address.person.lastName");

		Assert.assertEquals("Martins", value);
	}

	@Test
	public void testBeanContextCreator() {
		CustomizableBeanWrapperFactory factory = new CustomizableBeanWrapperFactory();
		factory.setBeanContextBuilder(new MyContextBuilder());
		JuffrouBeanWrapper myPersonWrapper = factory.getBeanWrapper(Person.class);
		MyBeanWrapperContext context = (MyBeanWrapperContext) myPersonWrapper.getContext();

	}

	@Test
	public void testPrimitiveBoolean() {
		BooleanHolder booleanHolder = new BooleanHolder();
		booleanHolder.setDirty(true);
		booleanHolder.setHasContent(true);
		booleanHolder.setIsClean(Boolean.TRUE);
		booleanHolder.setHasCash(Boolean.TRUE);

		JuffrouBeanWrapper bw = new JuffrouBeanWrapper(booleanHolder);

		Object value = bw.getValue("isDirty");
		Assert.assertEquals(value, true);
		value = bw.getValue("hasContent");
		Assert.assertEquals(value, true);
		value = bw.getValue("isClean");
		Assert.assertEquals(value, Boolean.TRUE);
		value = bw.getValue("hasCash");
		Assert.assertEquals(value, Boolean.TRUE);

		bw.setValue("isDirty", false);
		bw.setValue("hasContent", false);
		bw.setValue("isClean", Boolean.FALSE);
		bw.setValue("hasCash", Boolean.FALSE);

		value = bw.getValue("isDirty");
		Assert.assertEquals(value, false);
		value = bw.getValue("hasContent");
		Assert.assertEquals(value, false);
		value = bw.getValue("isClean");
		Assert.assertEquals(value, Boolean.FALSE);
		value = bw.getValue("hasCash");
		Assert.assertEquals(value, Boolean.FALSE);

	}
}
