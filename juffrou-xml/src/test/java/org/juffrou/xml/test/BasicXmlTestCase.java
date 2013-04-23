package org.juffrou.xml.test;

import java.io.StringWriter;
import java.io.Writer;

import org.juffrou.util.reflect.BeanWrapper;
import org.juffrou.util.reflect.BeanWrapperContext;
import org.juffrou.xml.internal.JuffrouXmlMarshaller;
import org.junit.Test;

public class BasicXmlTestCase {

	@Test
	public void testMarshallBean() {
		BeanWrapperContext bwc = new BeanWrapperContext(Person.class);
		BeanWrapper bw = new BeanWrapper(bwc);
		bw.setValue("firstName", "Carlos");
		bw.setValue("lastName", "Martins");
		
		JuffrouXmlMarshaller marshaller = new JuffrouXmlMarshaller();
		Writer writer = new StringWriter();
		marshaller.marshallBean(writer, bw.getBean());
		System.out.println(writer.toString());
	}
}
