package org.juffrou.xml.converter;

import java.io.IOException;
import java.io.Writer;

public interface Converter {

	void toXml(Writer writer, Object instance) throws IOException;
}
