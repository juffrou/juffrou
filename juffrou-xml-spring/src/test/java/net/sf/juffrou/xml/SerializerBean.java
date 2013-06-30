package net.sf.juffrou.xml;

import net.sf.juffrou.util.reflect.BeanWrapper;
import net.sf.juffrou.xml.internal.io.JuffrouReader;
import net.sf.juffrou.xml.internal.io.JuffrouWriter;
import net.sf.juffrou.xml.serializer.Serializer;

public class SerializerBean implements Serializer {

	@Override
	public void serialize(JuffrouWriter writer, BeanWrapper valueOwner,	String valuePropertyName) {
		writer.write((String) valueOwner.getValue(valuePropertyName));
		
	}

	@Override
	public void deserialize(JuffrouReader reader, BeanWrapper valueOwner, String valuePropertyName) {
		valueOwner.setValueOfString(valuePropertyName, reader.getText());
	}

}
