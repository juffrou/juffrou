package org.juffrou.xml.serializer;

import java.math.BigInteger;

import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;

public class BigIntegerSerializer implements Serializer {

	@Override
	public void serialize(JuffrouWriter writer, Object instance) {
		writer.write(instance.toString());
	}

	@Override
	public Object deserialize(JuffrouReader reader) {
		String value = reader.getText();
		return new BigInteger(value);
	}
}
