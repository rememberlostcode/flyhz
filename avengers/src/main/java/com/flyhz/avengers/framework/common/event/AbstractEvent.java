
package com.flyhz.avengers.framework.common.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.lang.Event;

public abstract class AbstractEvent implements Event {

	protected Logger	log	= LoggerFactory.getLogger(getClass());

}
