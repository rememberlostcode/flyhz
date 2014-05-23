
package com.flyhz.avengers.framework.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(name = "avengersProperties", value = "classpath:/avengers.properties")
public class AvengersConfiguration {
	static Logger	LOG	= LoggerFactory.getLogger(AvengersConfiguration.class);
}
