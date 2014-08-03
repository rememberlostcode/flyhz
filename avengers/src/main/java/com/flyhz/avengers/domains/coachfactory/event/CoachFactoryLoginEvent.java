
package com.flyhz.avengers.domains.coachfactory.event;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.lang.Event;

public class CoachFactoryLoginEvent implements Event {

	private static final Logger	LOG	= LoggerFactory.getLogger(CoachFactoryLoginEvent.class);

	private String				username;

	private String				password;

	public CoachFactoryLoginEvent(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	@Override
	public boolean call(Map<String, Object> context) {
		LOG.info("login ...... username = {} password = ", this.username, this.password);
		return false;
	}

}
