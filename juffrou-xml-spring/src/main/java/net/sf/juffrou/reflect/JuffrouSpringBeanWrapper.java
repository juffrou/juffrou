package net.sf.juffrou.reflect;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.Map;

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public ConversionService getConversionService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExtractOldValueForEditor(boolean extractOldValueForEditor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isExtractOldValueForEditor() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isReadableProperty(String propertyName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWritableProperty(String propertyName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Class getPropertyType(String propertyName) throws BeansException {
		return super.getClazz(propertyName);
	}

	@Override
	public TypeDescriptor getPropertyTypeDescriptor(String propertyName)
			throws BeansException {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PropertyDescriptor getPropertyDescriptor(String propertyName)
			throws InvalidPropertyException {
		// TODO Auto-generated method stub
		return null;
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
