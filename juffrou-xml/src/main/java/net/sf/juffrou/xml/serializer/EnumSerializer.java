package net.sf.juffrou.xml.serializer;

import net.sf.juffrou.reflect.JuffrouBeanWrapper;
import net.sf.juffrou.xml.internal.io.JuffrouReader;
import net.sf.juffrou.xml.internal.io.JuffrouWriter;

public class EnumSerializer implements Serializer {

	@Override
	public void serialize(JuffrouWriter writer, JuffrouBeanWrapper valueOwner, String valuePropertyName) {
		writer.write(valueOwner.getValue(valuePropertyName).toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void deserialize(JuffrouReader reader, JuffrouBeanWrapper valueOwner, String valuePropertyName) {
		String value = reader.getText();
		Class clazz = valueOwner.getClazz(valuePropertyName);
		valueOwner.setValue(valuePropertyName, Enum.valueOf(clazz, value));
	}
}
