package org.juffrou.xml.converter;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.xml.internal.JuffrouMarshaller;
import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;

public interface Converter {

	void toXml(JuffrouMarshaller marshaller, JuffrouWriter writer, Object instance);
	
	Object fromXml(JuffrouMarshaller marshaller, JuffrouReader reader, BeanWrapper instance);
}
