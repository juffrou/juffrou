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

import net.sf.juffrou.util.reflect.ReflectionUtil;
import net.sf.juffrou.xml.error.NoImplementationClassException;
import net.sf.juffrou.xml.error.NoSerializerException;
import net.sf.juffrou.xml.error.UnknownXmlElementException;
import net.sf.juffrou.xml.internal.binding.BeanClassBinding;
import net.sf.juffrou.xml.internal.binding.BeanPropertyBinding;
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
	private final Map<String, Serializer> serializerRegistry = new HashMap<String, Serializer>();
	private final Map<Type, XmlElementType> classToSerializerRef = new HashMap<Type, XmlElementType>();
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
				beanClassBinding = xmlBeanWrapperContextCreator.newBeanWrapperContext(null, beanClass);
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
	
	/**
	 * Registers a bean with juffrou serializer and defines the name to use in tags representing this bean
	 * @param beanClazz the class of the java bean
	 * @param elementName the xml element name to use
	 */
	public void registerRootElement(Class beanClazz, String elementName, String serializerId) {
		BeanClassBinding beanClassBinding = getBeanClassBindingFromClass(beanClazz);
		if(beanClassBinding != null) {
			//remove old registration
			classToBindingMap.remove(beanClazz);
			xmlElementNameToBindingMap.remove(beanClassBinding.getXmlElementName());
		}
		else {
			beanClassBinding = new BeanClassBinding(beanClazz);
			beanClassBinding.setBeanContextCreator(xmlBeanWrapperContextCreator);
		}
		beanClassBinding.setXmlElementName(elementName);
		classToBindingMap.put(beanClazz, beanClassBinding);
		xmlElementNameToBindingMap.put(elementName, beanClassBinding);
		if(serializerId != null)
			beanClassBinding.setSerializer(getSerializerWithId(serializerId));
	}
	
	public void registerProperty(Class beanClazz, String beanPropertyName, String elementName, String serializerId, NodeType nodeType) {
		BeanClassBinding beanClassBinding = getBeanClassBindingFromClass(beanClazz);
		if(beanClassBinding.isEmpty())
			beanClassBinding.setAllBeanPropertiesToMarshall();

		BeanPropertyBinding beanPropertyBinding;
		
		// test if the beanPropertyName references a nested property
		int nestedIndex = beanPropertyName.indexOf('.');
		if(nestedIndex != -1 ) {
			String thisProperty = beanPropertyName.substring(0, nestedIndex);
			beanPropertyBinding = beanClassBinding.getBeanPropertyBindingFromPropertyName(thisProperty);
			if(elementName == null)
				elementName = beanPropertyBinding.getXmlElementName();
			beanClassBinding.removeBeanPropertyBinding(beanPropertyBinding);
			
			beanPropertyBinding = new BeanPropertyBinding();
			beanPropertyBinding.setBeanPropertyName(beanPropertyName);
			beanPropertyBinding.setXmlElementName(elementName);
			beanPropertyBinding.setPropertyType(ReflectionUtil.getClass(beanClassBinding.getType(beanPropertyName)));
			beanClassBinding.addBeanPropertyBinding(beanPropertyBinding);

		}
		else {
			beanPropertyBinding = beanClassBinding.getBeanPropertyBindingFromPropertyName(beanPropertyName);
			if(elementName != null && ! elementName.equals(beanPropertyBinding.getXmlElementName()))
				beanClassBinding.replaceBeanPropertyElementName(beanPropertyBinding, elementName);
		}
		beanPropertyBinding.setNodeType(nodeType);
		if(serializerId != null)
			beanPropertyBinding.setSerializer(getSerializerWithId(serializerId));
	}

	public void registerSerializer(String serializerId, Serializer serializer) {
		serializerRegistry.put(serializerId, serializer);
	}
	
	public Serializer getSerializerForClass(Class<?> clazz) {
		Class<?> target = clazz;
		if(clazz.isInterface())
			target = defaultImplementations.get(clazz);
		if(target == null)
			throw new NoImplementationClassException("I don't know which implementation of " + clazz.getSimpleName() + " to choose. Please add to the default implementations.");
		XmlElementType xmlElementType = classToSerializerRef.get(target);
		return xmlElementType != null ? getSerializerWithId(xmlElementType.xml()) : null;
	}
	
	public Serializer getSerializerWithId(String serializerId) {
		Serializer serializer = serializerRegistry.get(serializerId);
		if(serializer == null)
			throw new NoSerializerException("There is no serializer with id " + serializerId);
		return serializer;
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

		classToSerializerRef.put(Boolean.class, XmlElementType.BOOLEAN);
		classToSerializerRef.put(Byte.class, XmlElementType.BYTE);
		classToSerializerRef.put(Character.class, XmlElementType.CHARACTER);
		classToSerializerRef.put(Double.class, XmlElementType.DOUBLE);
		classToSerializerRef.put(Float.class, XmlElementType.FLOAT);
		classToSerializerRef.put(Integer.class, XmlElementType.INTEGER);
		classToSerializerRef.put(Long.class, XmlElementType.LONG);
		classToSerializerRef.put(Short.class, XmlElementType.SHORT);
		classToSerializerRef.put(String.class, XmlElementType.STRING);
		classToSerializerRef.put(BigInteger.class, XmlElementType.BIGINTEGER);
		classToSerializerRef.put(BigDecimal.class, XmlElementType.BIGDECIMAL);
		classToSerializerRef.put(Date.class, XmlElementType.DATE);
		classToSerializerRef.put(Enum.class, XmlElementType.ENUM);
		classToSerializerRef.put(ArrayList.class, XmlElementType.LIST);
		classToSerializerRef.put(HashSet.class, XmlElementType.SET);
		classToSerializerRef.put(HashMap.class, XmlElementType.MAP);

		serializerRegistry.put(XmlElementType.BEAN.xml(), defaultSerializer);
		serializerRegistry.put(XmlElementType.BOOLEAN.xml(), new BooleanSerializer());
		serializerRegistry.put(XmlElementType.BYTE.xml(), new ByteSerializer());
		serializerRegistry.put(XmlElementType.CHARACTER.xml(), new CharacterSerializer());
		serializerRegistry.put(XmlElementType.DOUBLE.xml(), new DoubleSerializer());
		serializerRegistry.put(XmlElementType.FLOAT.xml(), new FloatSerializer());
		serializerRegistry.put(XmlElementType.INTEGER.xml(), new IntegerSerializer());
		serializerRegistry.put(XmlElementType.LONG.xml(), new LongSerializer());
		serializerRegistry.put(XmlElementType.SHORT.xml(), new ShortSerializer());
		serializerRegistry.put(XmlElementType.STRING.xml(), new StringSerializer());
		serializerRegistry.put(XmlElementType.BIGINTEGER.xml(), new BigIntegerSerializer());
		serializerRegistry.put(XmlElementType.BIGDECIMAL.xml(), new BigDecimalSerializer());
		serializerRegistry.put(XmlElementType.DATE.xml(), new DateSerializer());
		serializerRegistry.put(XmlElementType.ENUM.xml(), new EnumSerializer());
		serializerRegistry.put(XmlElementType.LIST.xml(), new ArrayListSerializer(this));
		serializerRegistry.put(XmlElementType.SET.xml(), new HashSetSerializer(this));
		serializerRegistry.put(XmlElementType.MAP.xml(), new HashMapSerializer(this));
	}
}
