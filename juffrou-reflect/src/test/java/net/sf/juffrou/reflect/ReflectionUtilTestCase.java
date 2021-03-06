package net.sf.juffrou.reflect;

import junit.framework.Assert;
import net.sf.juffrou.reflect.dom.*;
import org.junit.Test;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Type;
import java.util.Map;

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
		JuffrouBeanWrapper bw = new JuffrouBeanWrapper(context);
		Type type = bw.getType("genericProperty");
		Assert.assertEquals(Person.class, type);
	}
	
	
	@Test
	public void testGenericClassInstance() {
		
		BeanWrapperContext context = BeanWrapperContext.create(GenericBean.class, Person.class);
		GenericBean<Person> genericPerson = new GenericBean<Person>();
		JuffrouBeanWrapper bw = new JuffrouBeanWrapper(context, genericPerson);
		Type type = bw.getType("genericProperty");
		Assert.assertEquals(Person.class,type);
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
		
		CustomizableBeanWrapperFactory factory = new CustomizableBeanWrapperFactory();
		
		Map<String, Object> beanMap = ReflectionUtil.getMapFromBean(factory, person);
		
		PersonCircular fromMap = new PersonCircular();
		ReflectionUtil.getBeanFromMap(factory, beanMap, fromMap);
		
		Assert.assertEquals(person.getFirstName(), fromMap.getFirstName());
	}
	
	@Test
	public void testGenericBean() {
		
		GenericBean2<String, Integer> genericBean = new GenericBean2<String, Integer>();

		BeanWrapperImpl bw = new BeanWrapperImpl(genericBean);
		Class propertyType = bw.getPropertyType("genericProperty");
		Class propertyType2 = bw.getPropertyType("otherGenericProperty");
		
		
		ReflectionUtil.findGenericArgumentTypes(genericBean);
	}
}
