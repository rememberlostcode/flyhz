
package com.flyhz.avengers.framework.event;

import java.util.Map;

import com.flyhz.avengers.framework.Event;

public class DataFilterEvent implements Event {

	@Override
	public boolean call(Map<String, Object> context) {
		return false;
	}

}
