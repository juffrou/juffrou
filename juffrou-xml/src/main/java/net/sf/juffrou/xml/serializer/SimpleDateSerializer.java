package net.sf.juffrou.xml.serializer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.juffrou.util.reflect.BeanWrapper;
import net.sf.juffrou.xml.internal.io.JuffrouReader;
import net.sf.juffrou.xml.internal.io.JuffrouWriter;

public class SimpleDateSerializer implements Serializer {

	private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public void serialize(JuffrouWriter writer, BeanWrapper valueOwner, String valuePropertyName) {
		writer.write(formatter.format((Date)valueOwner.getValue(valuePropertyName)));
	}

	@Override
	public void deserialize(JuffrouReader reader, BeanWrapper valueOwner, String valuePropertyName) {
		String value = reader.getText();
		try {
			valueOwner.setValue(valuePropertyName, formatter.parse(value));
		} catch (ParseException e) {
		}
	}
}
