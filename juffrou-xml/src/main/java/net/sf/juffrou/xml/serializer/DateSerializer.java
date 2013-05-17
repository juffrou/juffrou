package net.sf.juffrou.xml.serializer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import net.sf.juffrou.util.reflect.BeanWrapper;
import net.sf.juffrou.xml.internal.io.JuffrouReader;
import net.sf.juffrou.xml.internal.io.JuffrouWriter;

public class DateSerializer implements Serializer {

	//ISO8601 long RFC822 zone
	private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
	
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
