package org.juffrou.xml.converter;

import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;

public interface Converter {

	void toXml(JuffrouWriter writer, Object instance);
	
	Object fromXml(JuffrouReader reader);
}
