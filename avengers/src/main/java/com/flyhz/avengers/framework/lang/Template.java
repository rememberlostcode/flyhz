
package com.flyhz.avengers.framework.lang;

import java.util.Map;

import com.flyhz.avengers.framework.common.dto.PageRowDataDto;

public interface Template {
	void apply(PageRowDataDto pageRowDataDto, Map<String, Object> context);
}
