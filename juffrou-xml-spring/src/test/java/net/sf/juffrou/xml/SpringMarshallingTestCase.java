package net.sf.juffrou.xml;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringMarshallingTestCase {

	@Test
	public void testSpringMarshalling() {
		ApplicationContext appContext = new ClassPathXmlApplicationContext("application-context.xml");

		MockApplication application = (MockApplication) appContext.getBean("application");
        try {
			application.saveSettings();
	        application.loadSettings();
		} catch (IOException e) {
			fail();
		}

	}
}
