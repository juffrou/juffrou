package org.juffrou.xml.converter;

import java.util.Collection;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.xml.internal.JuffrouMarshaller;
import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;

public class CollectionConverter implements Converter {

	public void toXml(JuffrouMarshaller marshaller, JuffrouWriter writer, Object instance) {
		if(instance == null)
			return;
		Collection<?> collection = (Collection<?>) instance;
		for(Object object : collection) {
			marshaller.marshallBean(writer, object);
		}
	}

	@Override
	public Object fromXml(JuffrouMarshaller marshaller, JuffrouReader reader,
			BeanWrapper instance) {
		// TODO Auto-generated method stub
		return null;
	}
}
