package org.juffrou.xml.serializer;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;

public class EnumSerializer implements Serializer {

	@Override
	public void serialize(JuffrouWriter writer, BeanWrapper valueOwner, String valuePropertyName) {
		writer.write(valueOwner.getValue(valuePropertyName).toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void deserialize(JuffrouReader reader, BeanWrapper valueOwner, String valuePropertyName) {
		String value = reader.getText();
		Class clazz = valueOwner.getClazz(valuePropertyName);
		valueOwner.setValue(valuePropertyName, Enum.valueOf(clazz, value));
	}
}
