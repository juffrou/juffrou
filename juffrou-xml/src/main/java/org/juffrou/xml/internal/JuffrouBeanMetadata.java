package org.juffrou.xml.internal;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.juffrou.xml.NoImplementationClassException;
import org.juffrou.xml.UnknownXmlElementException;
import org.juffrou.xml.internal.binding.BeanClassBinding;
import org.juffrou.xml.internal.binding.XmlBeanWrapperContextCreator;
import org.juffrou.xml.serializer.ArrayListSerializer;
import org.juffrou.xml.serializer.BeanWrapperSerializer;
import org.juffrou.xml.serializer.BigDecimalSerializer;
import org.juffrou.xml.serializer.BigIntegerSerializer;
import org.juffrou.xml.serializer.BooleanSerializer;
import org.juffrou.xml.serializer.ByteSerializer;
import org.juffrou.xml.serializer.CharacterSerializer;
import org.juffrou.xml.serializer.DateSerializer;
import org.juffrou.xml.serializer.DoubleSerializer;
import org.juffrou.xml.serializer.EnumSerializer;
import org.juffrou.xml.serializer.FloatSerializer;
import org.juffrou.xml.serializer.HashMapSerializer;
import org.juffrou.xml.serializer.HashSetSerializer;
import org.juffrou.xml.serializer.IntegerSerializer;
import org.juffrou.xml.serializer.LongSerializer;
import org.juffrou.xml.serializer.Serializer;
import org.juffrou.xml.serializer.ShortSerializer;
import org.juffrou.xml.serializer.StringSerializer;

public class JuffrouBeanMetadata {

	private XmlBeanWrapperContextCreator xmlBeanWrapperContextCreator;
	private Map<Type, Serializer> generalConverters = new HashMap<Type, Serializer>();
	private Map<Class<?>, Class<?>> defaultImplementations = new HashMap<Class<?>, Class<?>>();
	private Map<Class<?>, BeanClassBinding> classToBindingMap = new HashMap<Class<?>, BeanClassBinding>();
	private Map<String, BeanClassBinding> xmlElementNameToBindingMap = new HashMap<String, BeanClassBinding>();
	private BeanWrapperSerializer defaultSerializer;
	
	public JuffrouBeanMetadata() {
		defaultSerializer = new BeanWrapperSerializer(this);
		xmlBeanWrapperContextCreator = new XmlBeanWrapperContextCreator(this);
		setDefaultImplementations();
		setDefaultConverters();
	}
	
	public BeanClassBinding getBeanClassBindingFromClass(Object bean) {
		Class<? extends Object> beanClass = bean.getClass();
		BeanClassBinding beanClassBinding = classToBindingMap.get(beanClass);
		if(beanClassBinding == null)
			beanClassBinding = xmlBeanWrapperContextCreator.newBeanWrapperContext(beanClass);
		return beanClassBinding;
	}
	public BeanClassBinding getBeanClassBindingFromXmlElement(String xmlElement) {
		BeanClassBinding beanClassBinding = xmlElementNameToBindingMap.get(xmlElement);
		if(beanClassBinding == null) {
			try {
				Class<?> beanClass = Class.forName(xmlElement);
				beanClassBinding = classToBindingMap.get(beanClass);
			} catch (ClassNotFoundException e) {
				throw new UnknownXmlElementException("The element '" + xmlElement + "' has not been registered");
			}
		}
		return beanClassBinding;
	}
	public void putBeanClassBinding(BeanClassBinding beanClassBinding) {
		classToBindingMap.put(beanClassBinding.getBeanClass(), beanClassBinding);
		xmlElementNameToBindingMap.put(beanClassBinding.getXmlElementName(), beanClassBinding);
	}
	
	public Serializer getSerializerForClass(Class<?> clazz) {
		Class<?> target = clazz;
		if(clazz.isInterface())
			target = defaultImplementations.get(clazz);
		if(target == null)
			throw new NoImplementationClassException("I don't know which implementation of " + clazz.getSimpleName() + " to choose. Please add to the default implementations.");
		Serializer converter = generalConverters.get(target);
		return converter;
	}
	
	public BeanWrapperSerializer getDefaultSerializer() {
		return defaultSerializer;
	}
	public void setDefaultSerializer(BeanWrapperSerializer defaultSerializer) {
		this.defaultSerializer = defaultSerializer;
	}

	private void setDefaultImplementations() {
		defaultImplementations.put(List.class, ArrayList.class);
		defaultImplementations.put(Set.class, HashSet.class);
		defaultImplementations.put(Map.class, HashMap.class);
	}
	
	private void setDefaultConverters() {
		generalConverters.put(Boolean.class, new BooleanSerializer());
		generalConverters.put(Byte.class, new ByteSerializer());
		generalConverters.put(Character.class, new CharacterSerializer());
		generalConverters.put(Double.class, new DoubleSerializer());
		generalConverters.put(Float.class, new FloatSerializer());
		generalConverters.put(Integer.class, new IntegerSerializer());
		generalConverters.put(Long.class, new LongSerializer());
		generalConverters.put(Short.class, new ShortSerializer());
		generalConverters.put(String.class, new StringSerializer());
		generalConverters.put(BigInteger.class, new BigIntegerSerializer());
		generalConverters.put(BigDecimal.class, new BigDecimalSerializer());
		generalConverters.put(Date.class, new DateSerializer());
		generalConverters.put(Enum.class, new EnumSerializer());
		
		generalConverters.put(ArrayList.class, new ArrayListSerializer(this));
		generalConverters.put(HashSet.class, new HashSetSerializer(this));
		generalConverters.put(HashMap.class, new HashMapSerializer(this));
	}
}
