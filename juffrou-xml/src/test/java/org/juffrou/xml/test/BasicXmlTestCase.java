package org.juffrou.xml.test;

<<<<<<< HEAD
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

=======
>>>>>>> origin/master
import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.util.reflect.BeanWrapperContext;
import org.juffrou.xml.internal.JuffrouMarshaller;
import org.juffrou.xml.internal.io.JuffrouWriter;
<<<<<<< HEAD
import org.juffrou.xml.internal.io.XmlReader;
=======
>>>>>>> origin/master
import org.juffrou.xml.internal.io.XmlWriter;
import org.junit.Test;

public class BasicXmlTestCase {

	@Test
	public void testMarshallBean() {
		BeanWrapperContext bwc = new BeanWrapperContext(Person.class);
		BeanWrapper bw = new BeanWrapper(bwc);
		bw.setValue("firstName", "Carlos");
		bw.setValue("lastName", "Martins");
		
		JuffrouMarshaller marshaller = new JuffrouMarshaller();
		JuffrouWriter writer = new XmlWriter();
		marshaller.marshallBean(writer, bw.getBean());
		
		String xmlString = writer.toString();
		System.out.println(xmlString);
		
		try {
			InputStream stream = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
			XmlReader reader = new XmlReader(stream);
			
			Object unmarshall = marshaller.unmarshall(reader);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
