package net.sf.juffrou.xml.serializer;

import java.util.Collection;

import net.sf.juffrou.xml.internal.JuffrouBeanMetadata;
import net.sf.juffrou.xml.internal.binding.BeanClassBinding;
import net.sf.juffrou.xml.internal.binding.BeanPropertyBinding;
import net.sf.juffrou.xml.internal.io.JuffrouReader;
import net.sf.juffrou.xml.internal.io.JuffrouWriter;

import org.juffrou.util.reflect.BeanWrapper;

public class BeanWrapperSerializer implements Serializer {

	private final JuffrouBeanMetadata xmlBeanMetadata;
	
	public BeanWrapperSerializer(JuffrouBeanMetadata xmlBeanMetadata) {
		this.xmlBeanMetadata = xmlBeanMetadata;
	}
	
	@Override
	public void serialize(JuffrouWriter writer, BeanWrapper valueOwner, String valuePropertyName) {
		BeanWrapper nestedWrapper = valueOwner.getNestedWrapper(valuePropertyName);
		serializeBeanProperties(writer, nestedWrapper);
	}

	@Override
	public void deserialize(JuffrouReader reader, BeanWrapper valueOwner, String valuePropertyName) {
		BeanWrapper nestedWrapper = valueOwner.getNestedWrapper(valuePropertyName);
		deserializeBeanProperties(reader, nestedWrapper);
	}
	
	public void serializeBeanProperties(JuffrouWriter writer, BeanWrapper bw) {
		BeanClassBinding beanClassBinding = (BeanClassBinding) bw.getContext();
		if(beanClassBinding.getBeanPropertiesToMarshall().isEmpty())
			beanClassBinding.setAllBeanPropertiesToMarshall();
		Collection<BeanPropertyBinding> propertiesToMarshall = beanClassBinding.getBeanPropertiesToMarshall().values();
		for(BeanPropertyBinding beanPropertyBinding : propertiesToMarshall) {
			Object value = bw.getValue(beanPropertyBinding.getBeanPropertyName());
			if(value != null) {
				writer.startNode(beanPropertyBinding.getXmlElementName());
				Serializer converter = beanPropertyBinding.getConverter();
				if(converter == null)
					converter = xmlBeanMetadata.getSerializerForClass(beanPropertyBinding.getPropertyType());
				if(converter == null)
					serialize(writer, bw, beanPropertyBinding.getBeanPropertyName());
				else
					converter.serialize(writer, bw, beanPropertyBinding.getBeanPropertyName());
				writer.endNode();
			}
		}
	}

	public void deserializeBeanProperties(JuffrouReader reader, BeanWrapper instance) {
		BeanClassBinding beanClassBinding = (BeanClassBinding) instance.getContext();
		if(beanClassBinding.getBeanPropertiesToMarshall().isEmpty())
			beanClassBinding.setAllBeanPropertiesToMarshall();
		String xmlElementName = reader.enterNode();
		while(xmlElementName != null) {
			BeanPropertyBinding beanPropertyBinding = beanClassBinding.getBeanPropertiesToMarshall().get(xmlElementName);
			Serializer converter = beanPropertyBinding.getConverter();
			if(converter == null)
				converter = xmlBeanMetadata.getSerializerForClass(beanPropertyBinding.getPropertyType());
			if(converter == null)
				// treat it as a bean
				deserialize(reader, instance, beanPropertyBinding.getBeanPropertyName());
			else
				converter.deserialize(reader, instance, beanPropertyBinding.getBeanPropertyName());
			xmlElementName = reader.next();
		}
		reader.exitNode();
	}

}
