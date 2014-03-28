
package com.flyhz.framework.view.content;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import com.flyhz.framework.auth.WebUser;

public class FinderJsonView extends MappingJacksonJsonView {

	@Override
	protected Object filterModel(Map<String, Object> model) {
		if (model.size() != 1) {
			Map<String, Object> result = new HashMap<String, Object>(model.size());
			for (Entry<String, Object> entry : model.entrySet()) {
				if (!"finderMenus".equals(entry.getKey()) && !(entry.getValue() instanceof List)
						&& !"webUser".equals(entry.getKey())
						&& !(entry.getValue() instanceof WebUser)) {
					result.put(entry.getKey(), entry.getValue());
				}
			}
			return super.filterModel(result);
		}
		return super.filterModel(model);
	}
}
