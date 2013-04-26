package org.juffrou.xml.converter;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.xml.internal.JuffrouMarshaller;
import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;

public class ToStringConverter implements Converter {

	@Override
	public void toXml(JuffrouMarshaller marshaller, JuffrouWriter writer, Object instance) {
		writer.write(instance.toString());
	}

	@Override
	public Object fromXml(JuffrouMarshaller marshaller, JuffrouReader reader,
			BeanWrapper instance) {
		// TODO Auto-generated method stub
		return null;
	}

}
