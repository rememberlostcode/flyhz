
package com.flyhz.avengers.framework.lang;

import java.util.Map;

public interface Event {

	boolean call(Map<String, Object> context);
}
