package net.sf.juffrou.xml.internal.binding;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.juffrou.util.reflect.BeanWrapperContext;
import org.juffrou.util.reflect.ReflectionUtil;
import org.juffrou.util.reflect.internal.BeanFieldHandler;

public class BeanClassBinding extends BeanWrapperContext {

	private String xmlElementName;
	private Map<String, BeanPropertyBinding> beanPropertiesToMarshall = new HashMap<String, BeanPropertyBinding>();
	
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
	public Map<String, BeanPropertyBinding> getBeanPropertiesToMarshall() {
		return beanPropertiesToMarshall;
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
	}
	
}
