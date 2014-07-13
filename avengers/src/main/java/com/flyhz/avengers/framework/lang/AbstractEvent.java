
package com.flyhz.avengers.framework.lang;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEvent implements Event {

	protected Logger				log	= LoggerFactory.getLogger(getClass());

	protected Map<String, Object>	context;

	public AbstractEvent(Map<String, Object> context) {
		this.context = context;
	}
}
