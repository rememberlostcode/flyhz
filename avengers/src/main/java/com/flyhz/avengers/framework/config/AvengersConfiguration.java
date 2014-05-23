
package com.flyhz.avengers.framework.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.config.xml.Domains;
import com.flyhz.avengers.framework.util.XmlUtil;

public class AvengersConfiguration {

	private static final Logger	LOG	= LoggerFactory.getLogger(AvengersConfiguration.class);

	private Domains				domainsConfig;

	private Properties			sysConfig;

	private AvengersConfiguration() {
		ClassLoader classLoader = AvengersConfiguration.class.getClassLoader();
		URL avengersXml = classLoader.getResource("avengers.xml");
		URL avengersSchema = classLoader.getResource("avengers.xsd");
		URL avengersProperties = classLoader.getResource("avengers.properties");
		sysConfig = new Properties();
		try {
			sysConfig.load(new FileInputStream(new File(avengersProperties.toURI())));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("can't find avengers.properties", e);
		} catch (IOException e) {
			throw new RuntimeException("when open avengers.properties make an error", e);
		} catch (URISyntaxException e) {
			throw new RuntimeException("avengers uri Syntax error", e);
		}
		domainsConfig = XmlUtil.convertXmlToObject(Domains.class, avengersXml, avengersSchema);
		if (LOG.isDebugEnabled()) {
			LOG.debug(domainsConfig.toString());
		}
	}

	private static class Singleton {
		private static final AvengersConfiguration	singleton	= new AvengersConfiguration();
	}

	public static Domains getDomainsConfig() {
		return Singleton.singleton.domainsConfig;
	}
}
