package org.juffrou.xml.converter;

import org.juffrou.xml.internal.JuffrouMarshaller;
import org.juffrou.xml.internal.io.JuffrouWriter;

public class ToStringConverter implements Converter {

	@Override
	public void toXml(JuffrouMarshaller marshaller, JuffrouWriter writer, Object instance) {
		writer.write(instance.toString());
	}

}
