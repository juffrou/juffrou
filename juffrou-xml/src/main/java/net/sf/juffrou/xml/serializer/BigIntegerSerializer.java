package net.sf.juffrou.xml.serializer;

import java.math.BigInteger;

import net.sf.juffrou.reflect.JuffrouBeanWrapper;
import net.sf.juffrou.xml.internal.io.JuffrouReader;
import net.sf.juffrou.xml.internal.io.JuffrouWriter;

public class BigIntegerSerializer implements Serializer {

	@Override
	public void serialize(JuffrouWriter writer, JuffrouBeanWrapper valueOwner, String valuePropertyName) {
		writer.write(valueOwner.getValue(valuePropertyName).toString());
	}

	@Override
	public void deserialize(JuffrouReader reader, JuffrouBeanWrapper valueOwner, String valuePropertyName) {
		String value = reader.getText();
		valueOwner.setValue(valuePropertyName, new BigInteger(value));
	}
}
