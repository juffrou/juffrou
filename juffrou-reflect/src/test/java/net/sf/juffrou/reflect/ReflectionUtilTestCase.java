package net.sf.juffrou.reflect;

import java.lang.reflect.Type;
import java.util.Map;

import junit.framework.Assert;
import net.sf.juffrou.util.reflect.BeanWrapper;
import net.sf.juffrou.util.reflect.BeanWrapperContext;
import net.sf.juffrou.util.reflect.BeanWrapperFactory;
import net.sf.juffrou.util.reflect.ReflectionUtil;

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
		BeanWrapperContext context = BeanWrapperContext.create(GenericPerson.class);
		BeanWrapper bw = new BeanWrapper(context);
		Type type = bw.getType("genericProperty");
		Assert.assertTrue(Person.class.equals(type));
	}
	
	
	@Test
	public void testGenericClassInstance() {
		
		BeanWrapperContext context = BeanWrapperContext.create(GenericBean.class, Person.class);
		GenericBean<Person> genericPerson = new GenericBean<Person>();
		BeanWrapper bw = new BeanWrapper(context, genericPerson);
		Type type = bw.getType("genericProperty");
		Assert.assertTrue(Person.class.equals(type));
	}

	@Test
	public void testBeanToMapAndBack() {
		AddressCircular address = new AddressCircular();
		address.setStreet("Bean Street");
		address.setTown("Lisboa");
		PersonCircular person = new PersonCircular();
		person.setAddress(address);
		person.setFirstName("Carlos");
		person.setLastName("Martins");
		
		BeanWrapperFactory factory = new BeanWrapperFactory();
		
		Map<String, Object> beanMap = ReflectionUtil.getMapFromBean(factory, person);
		
		PersonCircular fromMap = new PersonCircular();
		ReflectionUtil.getBeanFromMap(factory, beanMap, fromMap);
		
		Assert.assertEquals(person.getFirstName(), fromMap.getFirstName());
	}
}
