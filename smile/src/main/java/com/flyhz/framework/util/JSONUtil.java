
package com.flyhz.framework.util;

import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;

public class JSONUtil {

	public static final ObjectMapper	mapper	= new ObjectMapper();
	static {
		mapper.configure(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

	}
}
