package org.juffrou.xml.converter;

import java.io.IOException;

import org.juffrou.xml.internal.io.JuffrouWriter;

public class ToStringConverter implements Converter {

	@Override
	public void toXml(JuffrouWriter writer, Object instance) throws IOException {
		writer.write(instance.toString());
	}

}
