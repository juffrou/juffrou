package net.sf.juffrou.xml.test.dom;

import net.sf.juffrou.reflect.JuffrouBeanWrapper;
import net.sf.juffrou.xml.internal.io.JuffrouReader;
import net.sf.juffrou.xml.internal.io.JuffrouWriter;
import net.sf.juffrou.xml.serializer.Serializer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleDateSerializer implements Serializer {

	private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public void serialize(JuffrouWriter writer, JuffrouBeanWrapper valueOwner, String valuePropertyName) {
		writer.write(formatter.format((Date)valueOwner.getValue(valuePropertyName)));
	}

	@Override
	public void deserialize(JuffrouReader reader, JuffrouBeanWrapper valueOwner, String valuePropertyName) {
		String value = reader.getText();
		try {
			valueOwner.setValue(valuePropertyName, formatter.parse(value));
		} catch (ParseException e) {
		}
	}
}
