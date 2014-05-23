
package com.flyhz.avengers.framework;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Analyze extends AvengersExecutor {

	private static final Logger	LOG	= LoggerFactory.getLogger(Analyze.class);

	@Override
	Logger getLog() {
		return LOG;
	}

	@Override
	Map<String, Object> initArgs(String[] args) {
		return null;
	}

	@Override
	List<Event> initAvengersEvents(Map<String, Object> context) {
		return null;
	}

}
