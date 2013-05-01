package net.sf.juffrou.xml.serializer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.juffrou.xml.internal.io.JuffrouReader;
import net.sf.juffrou.xml.internal.io.JuffrouWriter;

import org.juffrou.util.reflect.BeanWrapper;

public class DateSerializer implements Serializer {

	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
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
