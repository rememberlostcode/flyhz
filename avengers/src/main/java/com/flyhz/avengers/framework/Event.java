
package com.flyhz.avengers.framework;

import java.util.Map;

public interface Event {

	boolean call(Map<String, Object> context);
}
