
package com.flyhz.avengers.common.event;

import java.util.Map;

import com.flyhz.avengers.framework.Event;

public class TemplateApplyEvent implements Event {

	@Override
	public boolean call(Map<String, Object> context) {
		context.get("");
		return false;
	}

}
