package org.juffrou.xml.converter;

import java.io.IOException;

import org.juffrou.xml.internal.io.JuffrouWriter;

public interface Converter {

	void toXml(JuffrouWriter writer, Object instance) throws IOException;
}
