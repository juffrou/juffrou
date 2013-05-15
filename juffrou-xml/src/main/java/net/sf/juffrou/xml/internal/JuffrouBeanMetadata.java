package net.sf.juffrou.xml.internal;

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

import net.sf.juffrou.xml.NoImplementationClassException;
import net.sf.juffrou.xml.UnknownXmlElementException;
import net.sf.juffrou.xml.internal.binding.BeanClassBinding;
import net.sf.juffrou.xml.internal.binding.XmlBeanWrapperContextCreator;
import net.sf.juffrou.xml.internal.config.JuffrouXmlPreferences;
import net.sf.juffrou.xml.serializer.ArrayListSerializer;
import net.sf.juffrou.xml.serializer.BeanWrapperSerializer;
import net.sf.juffrou.xml.serializer.BigDecimalSerializer;
import net.sf.juffrou.xml.serializer.BigIntegerSerializer;
import net.sf.juffrou.xml.serializer.BooleanSerializer;
import net.sf.juffrou.xml.serializer.ByteSerializer;
import net.sf.juffrou.xml.serializer.CharacterSerializer;
import net.sf.juffrou.xml.serializer.DateSerializer;
import net.sf.juffrou.xml.serializer.DoubleSerializer;
import net.sf.juffrou.xml.serializer.EnumSerializer;
import net.sf.juffrou.xml.serializer.FloatSerializer;
import net.sf.juffrou.xml.serializer.HashMapSerializer;
import net.sf.juffrou.xml.serializer.HashSetSerializer;
import net.sf.juffrou.xml.serializer.IntegerSerializer;
import net.sf.juffrou.xml.serializer.LongSerializer;
import net.sf.juffrou.xml.serializer.Serializer;
import net.sf.juffrou.xml.serializer.ShortSerializer;
import net.sf.juffrou.xml.serializer.StringSerializer;


public class JuffrouBeanMetadata {

	private final XmlBeanWrapperContextCreator xmlBeanWrapperContextCreator;
	private final Map<Class<?>, BeanClassBinding> classToBindingMap = new HashMap<Class<?>, BeanClassBinding>();
	private final Map<String, BeanClassBinding> xmlElementNameToBindingMap = new HashMap<String, BeanClassBinding>();
	private final Map<Class<?>, Class<?>> defaultImplementations = new HashMap<Class<?>, Class<?>>();
	private final Map<Type, Serializer> generalSerializers = new HashMap<Type, Serializer>();
	private BeanWrapperSerializer defaultSerializer;
	private JuffrouXmlPreferences preferences;
	
	public JuffrouBeanMetadata() {
		defaultSerializer = new BeanWrapperSerializer(this);
		xmlBeanWrapperContextCreator = new XmlBeanWrapperContextCreator(this);
		preferences = new JuffrouXmlPreferences();
		setDefaultImplementations();
		setDefaultConverters();
	}
	
	public XmlBeanWrapperContextCreator getXmlBeanWrapperContextCreator() {
		return xmlBeanWrapperContextCreator;
	}

	public BeanClassBinding getBeanClassBindingFromClass(Class beanClass) {
		BeanClassBinding beanClassBinding = classToBindingMap.get(beanClass);
		return beanClassBinding;
	}
	public BeanClassBinding getBeanClassBindingFromXmlElement(String xmlElement) {
		BeanClassBinding beanClassBinding = xmlElementNameToBindingMap.get(xmlElement);
		if(beanClassBinding == null) {
			try {
				Class<?> beanClass = Class.forName(xmlElement);
				beanClassBinding = xmlBeanWrapperContextCreator.newBeanWrapperContext(beanClass);
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
		Serializer converter = generalSerializers.get(target);
		return converter;
	}
	
	public BeanWrapperSerializer getDefaultSerializer() {
		return defaultSerializer;
	}
	public void setDefaultSerializer(BeanWrapperSerializer defaultSerializer) {
		this.defaultSerializer = defaultSerializer;
	}

	public JuffrouXmlPreferences getPreferences() {
		return preferences;
	}

	public void setPreferences(JuffrouXmlPreferences preferences) {
		this.preferences = preferences;
	}

	private void setDefaultImplementations() {
		defaultImplementations.put(List.class, ArrayList.class);
		defaultImplementations.put(Set.class, HashSet.class);
		defaultImplementations.put(Map.class, HashMap.class);
	}
	
	private void setDefaultConverters() {
		generalSerializers.put(Boolean.class, new BooleanSerializer());
		generalSerializers.put(Byte.class, new ByteSerializer());
		generalSerializers.put(Character.class, new CharacterSerializer());
		generalSerializers.put(Double.class, new DoubleSerializer());
		generalSerializers.put(Float.class, new FloatSerializer());
		generalSerializers.put(Integer.class, new IntegerSerializer());
		generalSerializers.put(Long.class, new LongSerializer());
		generalSerializers.put(Short.class, new ShortSerializer());
		generalSerializers.put(String.class, new StringSerializer());
		generalSerializers.put(BigInteger.class, new BigIntegerSerializer());
		generalSerializers.put(BigDecimal.class, new BigDecimalSerializer());
		generalSerializers.put(Date.class, new DateSerializer());
		generalSerializers.put(Enum.class, new EnumSerializer());
		
		generalSerializers.put(ArrayList.class, new ArrayListSerializer(this));
		generalSerializers.put(HashSet.class, new HashSetSerializer(this));
		generalSerializers.put(HashMap.class, new HashMapSerializer(this));
	}
}
