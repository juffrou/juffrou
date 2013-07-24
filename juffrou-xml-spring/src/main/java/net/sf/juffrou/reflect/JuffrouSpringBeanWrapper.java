package net.sf.juffrou.reflect;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.Map;

import net.sf.juffrou.reflect.internal.BeanFieldHandler;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

public class JuffrouSpringBeanWrapper extends JuffrouBeanWrapper implements BeanWrapper {
	
	private ConversionService conversionService;
	private boolean extractOldValueForEditor;

	public JuffrouSpringBeanWrapper(BeanWrapperContext context,
			JuffrouBeanWrapper parentBeanWrapper, String parentBeanProperty) {
		super(context, parentBeanWrapper, parentBeanProperty);
	}

	public JuffrouSpringBeanWrapper(BeanWrapperContext context, Object instance) {
		super(context, instance);
	}

	public JuffrouSpringBeanWrapper(BeanWrapperContext context) {
		super(context);
	}

	public JuffrouSpringBeanWrapper(Class<?> clazz) {
		super(clazz);
	}

	public JuffrouSpringBeanWrapper(Object instance) {
		super(instance);
	}

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public ConversionService getConversionService() {
		return conversionService;
	}

	@Override
	public void setExtractOldValueForEditor(boolean extractOldValueForEditor) {
		this.extractOldValueForEditor = extractOldValueForEditor;
	}

	@Override
	public boolean isExtractOldValueForEditor() {
		return extractOldValueForEditor;
	}

	@Override
	public boolean isReadableProperty(String propertyName) {
		return true;
	}

	@Override
	public boolean isWritableProperty(String propertyName) {
		return true;
	}

	@Override
	public Class getPropertyType(String propertyName) throws BeansException {
		return super.getClazz(propertyName);
	}

	@Override
	public TypeDescriptor getPropertyTypeDescriptor(String propertyName) throws BeansException {
		return new TypeDescriptor(getContext().getBeanFieldHandler(propertyName).getField());
	}

	@Override
	public Object getPropertyValue(String propertyName) throws BeansException {
		return super.getValue(propertyName);
	}

	@Override
	public void setPropertyValue(String propertyName, Object value)
			throws BeansException {
		super.setValue(propertyName, value);
	}

	@Override
	public void setPropertyValue(PropertyValue pv) throws BeansException {
		super.setValue(pv.getName(), pv.getValue());
	}

	@Override
	public void setPropertyValues(Map<?, ?> map) throws BeansException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPropertyValues(PropertyValues pvs) throws BeansException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown)
			throws BeansException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown,
			boolean ignoreInvalid) throws BeansException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerCustomEditor(Class<?> requiredType,
			PropertyEditor propertyEditor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerCustomEditor(Class<?> requiredType,
			String propertyPath, PropertyEditor propertyEditor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PropertyEditor findCustomEditor(Class<?> requiredType,
			String propertyPath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T convertIfNecessary(Object value, Class<T> requiredType)
			throws TypeMismatchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T convertIfNecessary(Object value, Class<T> requiredType,
			MethodParameter methodParam) throws TypeMismatchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getWrappedInstance() {
		return super.getBean();
	}

	@Override
	public Class getWrappedClass() {
		return super.getBeanClass();
	}

	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		String[] propertyNames = getPropertyNames();
		PropertyDescriptor[] descriptors = new PropertyDescriptor[propertyNames.length];
		for(int i=0; i < propertyNames.length; i++)
			descriptors[i] = getPropertyDescriptor(propertyNames[i]);
		return descriptors;
	}

	@Override
	public PropertyDescriptor getPropertyDescriptor(String propertyName) throws InvalidPropertyException {
		BeanFieldHandler beanFieldHandler = getContext().getBeanFieldHandler(propertyName);
		try {
			return new JuffrouPropertyDescriptor(getClazz(propertyName), beanFieldHandler);
		} catch (IntrospectionException e) {
			throw new InvalidPropertyException(getClazz(propertyName), propertyName, "Cannot create PropertyDescriptor", e);
		}
	}

	@Override
	public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
	}

	@Override
	public boolean isAutoGrowNestedPaths() {
		return true;
	}

	@Override
	public void setAutoGrowCollectionLimit(int autoGrowCollectionLimit) {
	}

	@Override
	public int getAutoGrowCollectionLimit() {
		return 0;
	}

}
