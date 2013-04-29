package org.juffrou.xml.serializer;

import java.math.BigInteger;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;

public class BigIntegerSerializer implements Serializer {

	@Override
	public void serialize(JuffrouWriter writer, BeanWrapper valueOwner, String valuePropertyName) {
		writer.write(valueOwner.getValue(valuePropertyName).toString());
	}

	@Override
	public void deserialize(JuffrouReader reader, BeanWrapper valueOwner, String valuePropertyName) {
		String value = reader.getText();
		valueOwner.setValue(valuePropertyName, new BigInteger(value));
	}
}
