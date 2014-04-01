
package com.flyhz.framework.lang.content;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import com.flyhz.framework.lang.Protocol;

public class FinderJsonView extends MappingJacksonJsonView {

	private String	key	= Protocol.class.getSimpleName().toLowerCase();

	@Override
	protected Object filterModel(Map<String, Object> model) {
		if (model.size() == 1) {
			Object value = model.get(key);
			if (value != null && value instanceof Protocol) {
				Protocol protocol = (Protocol) value;
				Map<String, Object> result = new HashMap<String, Object>(2);
				if (protocol.getCode() != null) {
					result.put("code", protocol.getCode());
				}
				if (protocol.getData() != null) {
					result.put("data", protocol.getData());
				}
				return result;
			}
		}
		return null;
	}
}
