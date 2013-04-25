package org.juffrou.xml.converter;

import java.util.Collection;

import org.juffrou.xml.internal.JuffrouMarshaller;
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
}
