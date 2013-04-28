package org.juffrou.xml.converter;

import java.util.Collection;

import org.juffrou.xml.internal.JuffrouMarshaller;
import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;

public class CollectionConverter implements Converter {
	
	private JuffrouMarshaller marshaller;
	
	public CollectionConverter(JuffrouMarshaller marshaller) {
		this.marshaller = marshaller;
	}

	public void toXml(JuffrouWriter writer, Object instance) {
		if(instance == null)
			return;
		Collection<?> collection = (Collection<?>) instance;
		for(Object object : collection) {
			marshaller.marshallBean(writer, object);
		}
	}

	@Override
	public Object fromXml(JuffrouReader reader) {
		// TODO Auto-generated method stub
		return null;
	}
}
