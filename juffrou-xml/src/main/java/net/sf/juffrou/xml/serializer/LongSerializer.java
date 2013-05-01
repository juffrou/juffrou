package net.sf.juffrou.xml.serializer;

import net.sf.juffrou.xml.internal.io.JuffrouReader;
import net.sf.juffrou.xml.internal.io.JuffrouWriter;

import org.juffrou.util.reflect.BeanWrapper;

public class LongSerializer implements Serializer {

	@Override
	public void serialize(JuffrouWriter writer, BeanWrapper valueOwner, String valuePropertyName) {
		writer.write(valueOwner.getValue(valuePropertyName).toString());
	}

	@Override
	public void deserialize(JuffrouReader reader, BeanWrapper valueOwner, String valuePropertyName) {
		String value = reader.getText();
		valueOwner.setValue(valuePropertyName, Long.valueOf(value));
	}
}
