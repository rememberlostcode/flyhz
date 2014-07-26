
package com.flyhz.avengers.framework.common.event;

import java.util.Map;


public abstract class AbstractOutPutEvent extends AbstractEvent {

	public AbstractOutPutEvent(Map<String, Object> context) {
		super(context);
	}
}
