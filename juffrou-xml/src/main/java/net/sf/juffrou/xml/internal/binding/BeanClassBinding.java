package net.sf.juffrou.xml.internal.binding;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.juffrou.util.reflect.BeanWrapperContext;
import net.sf.juffrou.util.reflect.ReflectionUtil;
import net.sf.juffrou.util.reflect.internal.BeanFieldHandler;

public class BeanClassBinding extends BeanWrapperContext {

	private String xmlElementName;
	/**
	 * Map where keys are bean property names and values are bean property bindings
	 */
	private Map<String, BeanPropertyBinding> beanPropertiesToMarshall = new LinkedHashMap<String, BeanPropertyBinding>();
	/**
	 * Map where keys are xml element names and values are bean property bindings
	 */
	private Map<String, BeanPropertyBinding> xmlElementsToBeanProperties = new HashMap<String, BeanPropertyBinding>();
	
	public BeanClassBinding(Class<?> clazz) {
		super(clazz);
		xmlElementName = clazz.getName();
	}
	public BeanClassBinding(Class clazz, Type... types) {
		super(clazz, types);
		xmlElementName = clazz.getName();
	}
	
	public String getXmlElementName() {
		return xmlElementName;
	}
	public void setXmlElementName(String xmlElementName) {
		this.xmlElementName = xmlElementName;
	}
	public Map<String, BeanPropertyBinding> setAllBeanPropertiesToMarshall() {
		for(Entry<String, BeanFieldHandler> entry : getFields().entrySet()) {
			String propertyName = entry.getKey();
			BeanPropertyBinding beanPropertyBinding = new BeanPropertyBinding();
			beanPropertyBinding.setBeanPropertyName(propertyName);
			beanPropertyBinding.setXmlElementName(propertyName);
			beanPropertyBinding.setPropertyType(ReflectionUtil.getClass(entry.getValue().getType()));
			addBeanPropertyBinding(beanPropertyBinding);
		}
		return beanPropertiesToMarshall;
	}
	public void addBeanPropertyBinding(BeanPropertyBinding beanPropertyBinding) {
		this.beanPropertiesToMarshall.put(beanPropertyBinding.getBeanPropertyName(), beanPropertyBinding);
		this.xmlElementsToBeanProperties.put(beanPropertyBinding.getXmlElementName(), beanPropertyBinding);
	}
	public void replaceBeanPropertyElementName(BeanPropertyBinding beanPropertyBinding, String xmlElementName) {
		this.xmlElementsToBeanProperties.remove(beanPropertyBinding.getXmlElementName());
		beanPropertyBinding.setXmlElementName(xmlElementName);
		this.xmlElementsToBeanProperties.put(xmlElementName, beanPropertyBinding);
	}
	public void removeBeanPropertyBinding(BeanPropertyBinding beanPropertyBinding) {
		this.beanPropertiesToMarshall.remove(beanPropertyBinding.getBeanPropertyName());
		this.xmlElementsToBeanProperties.remove(beanPropertyBinding.getXmlElementName());
	}
	
	public Collection<BeanPropertyBinding> getPropertyBindings() {
		return beanPropertiesToMarshall.values();
	}
	
	public BeanPropertyBinding getBeanPropertyBindingFromPropertyName(String propertyName) {
		return beanPropertiesToMarshall.get(propertyName);
	}

	public BeanPropertyBinding getBeanPropertyBindingFromXmlElement(String xmlElementName) {
		return xmlElementsToBeanProperties.get(xmlElementName);
	}

	public boolean isEmpty() {
		return beanPropertiesToMarshall.isEmpty();
	}
}
