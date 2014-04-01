
package com.flyhz.framework.lang.content;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

public class FinderJsonView extends MappingJacksonJsonView {

	@Override
	protected Object filterModel(Map<String, Object> model) {
		if (model.size() != 1) {
			Map<String, Object> result = new HashMap<String, Object>(model.size());
			for (Entry<String, Object> entry : model.entrySet()) {
				if (!(entry.getValue() instanceof List)) {
					result.put(entry.getKey(), entry.getValue());
				}
			}
			return super.filterModel(result);
		}
		return super.filterModel(model);
	}
}
