package org.juffrou.xml.serializer;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;

public interface Serializer {

	void serialize(JuffrouWriter writer, BeanWrapper valueOwner, String valuePropertyName);
	
	void deserialize(JuffrouReader reader, BeanWrapper valueOwner, String valuePropertyName);
}
