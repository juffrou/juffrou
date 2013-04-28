package org.juffrou.xml.serializer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.juffrou.xml.internal.io.JuffrouReader;
import org.juffrou.xml.internal.io.JuffrouWriter;

public class DateSerializer implements Serializer {

	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public void serialize(JuffrouWriter writer, Object instance) {
		writer.write(formatter.format((Date)instance));
	}

	@Override
	public Object deserialize(JuffrouReader reader) {
		String value = reader.getText();
		try {
			return formatter.parse(value);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
