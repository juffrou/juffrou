package net.sf.juffrou.xml;

import net.sf.juffrou.reflect.JuffrouBeanWrapper;
import net.sf.juffrou.xml.internal.io.JuffrouReader;
import net.sf.juffrou.xml.internal.io.JuffrouWriter;
import net.sf.juffrou.xml.serializer.Serializer;

public class SerializerBean implements Serializer {

	@Override
	public void serialize(JuffrouWriter writer, JuffrouBeanWrapper valueOwner,	String valuePropertyName) {
		writer.write((String) valueOwner.getValue(valuePropertyName));
		
	}

	@Override
	public void deserialize(JuffrouReader reader, JuffrouBeanWrapper valueOwner, String valuePropertyName) {
		valueOwner.setValueOfString(valuePropertyName, reader.getText());
	}

}
