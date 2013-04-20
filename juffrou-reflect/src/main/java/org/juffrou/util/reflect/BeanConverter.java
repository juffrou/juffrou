package org.juffrou.util.reflect;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to convert between two beans<p>
 * Given any two beans and a map that establishes which properties in bean 1 correspond to properties in bean 2,
 * this class can be used to automatically obtain bean 1 from an instance of bean 2 and vice-versa.
 * 
 * @author cemartins
 *
 * @param <T1> bean 1 type
 * @param <T2> bean 2 type
 */
public class BeanConverter<T1, T2> {

	private Map<String, String> b1b2BindingMap;
	private Map<String, String> b2b1BindingMap;
	private BeanWrapper bw1;
	private BeanWrapper bw2;
	
	/**
	 * @param clazz1 bean 1 class
	 * @param clazz2 bean 2 class
	 * @param propertyBindingMap map that establishes which properties in bean 1 correspond to properties in bean 2
	 */
	public BeanConverter(Class<T1> clazz1, Class<T2> clazz2, Map<String, String> propertyBindingMap) {
		this.bw1 = new BeanWrapper(clazz1);
		this.bw2 = new BeanWrapper(clazz2);
		this.b1b2BindingMap = propertyBindingMap;
		b2b1BindingMap = new HashMap<String, String>();
		for(String b1prop : b1b2BindingMap.keySet()) {
			b2b1BindingMap.put(b1b2BindingMap.get(b1prop), b1prop);
		}
	}
	
	/**
	 * Get bean 1 from an instance of bean 2
	 * @param bean2
	 * @return
	 */
	public T1 getBean1(T2 bean2) {
		bw1.reset();
		bw2.setBean(bean2);
		for(String b2prop : b2b1BindingMap.keySet()) {
			bw1.setValue(b2b1BindingMap.get(b2prop), bw2.getValue(b2prop));
		}
		return (T1) bw1.getBean();
	}
	
	/**
	 * Get bean 2 from an instance of bean 1
	 * @param bean1
	 * @return
	 */
	public T2 getBean2(T1 bean1) {
		bw2.reset();
		bw1.setBean(bean1);
		for(String b1prop : b1b2BindingMap.keySet()) {
			bw2.setValue(b1b2BindingMap.get(b1prop), bw1.getValue(b1prop));
		}
		return (T2) bw2.getBean();
	}
}
