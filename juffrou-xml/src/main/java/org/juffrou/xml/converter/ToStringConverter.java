package org.juffrou.xml.converter;

<<<<<<< HEAD
import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.xml.internal.JuffrouMarshaller;
import org.juffrou.xml.internal.io.JuffrouReader;
=======
import java.io.IOException;

>>>>>>> origin/master
import org.juffrou.xml.internal.io.JuffrouWriter;

public class ToStringConverter implements Converter {

	@Override
<<<<<<< HEAD
	public void toXml(JuffrouMarshaller marshaller, JuffrouWriter writer, Object instance) {
		writer.write(instance.toString());
	}

	@Override
	public Object fromXml(JuffrouMarshaller marshaller, JuffrouReader reader,
			BeanWrapper instance) {
		// TODO Auto-generated method stub
		return null;
	}

=======
	public void toXml(JuffrouWriter writer, Object instance) throws IOException {
		writer.write(instance.toString());
	}

>>>>>>> origin/master
}
