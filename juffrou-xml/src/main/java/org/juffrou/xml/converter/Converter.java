package org.juffrou.xml.converter;

<<<<<<< HEAD
import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.xml.internal.JuffrouMarshaller;
import org.juffrou.xml.internal.io.JuffrouReader;
=======
import java.io.IOException;

>>>>>>> origin/master
import org.juffrou.xml.internal.io.JuffrouWriter;

public interface Converter {

<<<<<<< HEAD
	void toXml(JuffrouMarshaller marshaller, JuffrouWriter writer, Object instance);
	
	Object fromXml(JuffrouMarshaller marshaller, JuffrouReader reader, BeanWrapper instance);
=======
	void toXml(JuffrouWriter writer, Object instance) throws IOException;
>>>>>>> origin/master
}
