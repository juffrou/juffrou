package org.juffrou.xml.converter;

import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;

public class ToStringConverter implements Converter {

	@Override
	public void toXml(JuffrouWriter writer, Object instance) {
		writer.write(instance.toString());
	}

	@Override
	public Object fromXml(JuffrouReader reader) {
		// TODO Auto-generated method stub
		return null;
	}
}
