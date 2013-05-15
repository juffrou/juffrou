package net.sf.juffrou.xml.internal.binding;

import java.lang.reflect.Type;

import net.sf.juffrou.util.reflect.BeanContextCreator;
import net.sf.juffrou.xml.internal.JuffrouBeanMetadata;

public class XmlBeanWrapperContextCreator implements BeanContextCreator<BeanClassBinding> {

	private final JuffrouBeanMetadata juffrouBeanMetadata;
	
	public XmlBeanWrapperContextCreator(JuffrouBeanMetadata juffrouBeanMetadata) {
		this.juffrouBeanMetadata = juffrouBeanMetadata;
	}
	
	@Override
	public BeanClassBinding newBeanWrapperContext(Class clazz) {
		BeanClassBinding beanClassBinding = juffrouBeanMetadata.getBeanClassBindingFromClass(clazz);
		if(beanClassBinding == null) {
			beanClassBinding = new BeanClassBinding(clazz);
			registerBeanClassBinding(beanClassBinding);
		}
		return beanClassBinding;
	}

	@Override
	public BeanClassBinding newBeanWrapperContext(Class clazz, Type... types) {
		BeanClassBinding beanClassBinding = juffrouBeanMetadata.getBeanClassBindingFromClass(clazz);
		if(beanClassBinding == null) {
			beanClassBinding = new BeanClassBinding(clazz, types);
			registerBeanClassBinding(beanClassBinding);
		}
		return beanClassBinding;
	}
	
	public void registerBeanClassBinding(BeanClassBinding xmlBeanWrapperContext) {
		xmlBeanWrapperContext.setBeanContextCreator(this);
		juffrouBeanMetadata.putBeanClassBinding(xmlBeanWrapperContext);
	}

}
