package net.sf.juffrou.xml.internal.binding;

import java.lang.reflect.Type;

import net.sf.juffrou.reflect.BeanContextBuilder;
import net.sf.juffrou.reflect.CustomizableBeanWrapperFactory;
import net.sf.juffrou.xml.internal.JuffrouBeanMetadata;

public class XmlBeanWrapperContextCreator implements BeanContextBuilder {

	private final JuffrouBeanMetadata juffrouBeanMetadata;
	
	public XmlBeanWrapperContextCreator(JuffrouBeanMetadata juffrouBeanMetadata) {
		this.juffrouBeanMetadata = juffrouBeanMetadata;
	}
	
	@Override
	public BeanClassBinding build(CustomizableBeanWrapperFactory hierarchyContext, Class clazz, Type... types) {
		BeanClassBinding beanClassBinding = juffrouBeanMetadata.getBeanClassBindingFromClass(clazz);
		if(beanClassBinding == null) {
			beanClassBinding = new BeanClassBinding(hierarchyContext, clazz, types);
			juffrouBeanMetadata.putBeanClassBinding(beanClassBinding);
		}
		return beanClassBinding;
	}

}
