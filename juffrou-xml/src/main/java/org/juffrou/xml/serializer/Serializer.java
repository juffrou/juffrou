package org.juffrou.xml.serializer;

import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;

public interface Serializer {

	void serialize(JuffrouWriter writer, Object instance);
	
	Object deserialize(JuffrouReader reader);
}
