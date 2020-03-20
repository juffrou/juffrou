package net.sf.juffrou.xml;

import net.sf.juffrou.xml.internal.config.ConfigReader;
import net.sf.juffrou.xml.internal.config.JuffrouSpringConfigReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;

public class JuffrouXmlSpring extends JuffrouXml implements ApplicationContextAware, InitializingBean {

	private final Log logger = LogFactory.getLog(getClass());

	private ApplicationContext applicationContext;
	private Resource[] mappingLocations;

	
	public Resource[] getMappingLocations() {
		return mappingLocations;
	}

	public void setMappingLocations(Resource[] mappingLocations) {
		this.mappingLocations = mappingLocations;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		if (!ObjectUtils.isEmpty(mappingLocations)) {
			ConfigReader configReader = new JuffrouSpringConfigReader(applicationContext);
			for (Resource mappingLocation : mappingLocations) {
				try {
					InputStream inputStream = mappingLocation.getInputStream();
					readConfigFile(configReader, inputStream);
					inputStream.close();
				}
				catch(RuntimeException e) {
					if(logger.isErrorEnabled())
						logger.error("Cannot read configuration file " + mappingLocation.toString(), e);
				}
			}
		}
		else {
			if(logger.isWarnEnabled())
				logger.warn("No mapping location defined. Using default configuration for JuffrouXml.");
		}
		
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.applicationContext = context;
	}

}
