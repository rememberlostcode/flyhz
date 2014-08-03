
package com.flyhz.avengers.domains.coach.template;

import java.util.Map;

import com.flyhz.avengers.framework.common.dto.PageRowDataDto;
import com.flyhz.avengers.framework.lang.Template;

public class CoachProductDetailTemplate implements Template {

	@Override
	public void apply(PageRowDataDto pageRowDataDto, Map<String, Object> context) {
		String url = (String) context.get("template.url");
		String htmlDoc = null;
		// HbaseShell shell = new HbaseShell();
		// shell.....
		htmlDoc = "<html>.....</html>";
	}
}
