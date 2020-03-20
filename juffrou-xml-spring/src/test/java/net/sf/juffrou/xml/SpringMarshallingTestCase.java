package net.sf.juffrou.xml;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

import static org.junit.Assert.fail;

public class SpringMarshallingTestCase {

	@Test
	public void testSpringMarshalling() {
		ApplicationContext appContext = new ClassPathXmlApplicationContext("application-context.xml");

		MockApplication application = (MockApplication) appContext.getBean("application");
		application.getSettings().setMyString("SomeBeanPropertyValue");
        try {
			application.saveSettings();
	        application.loadSettings();
		} catch (IOException e) {
			fail();
		}

	}
}
