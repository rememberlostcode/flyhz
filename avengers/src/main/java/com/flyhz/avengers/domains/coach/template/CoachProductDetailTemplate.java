
package com.flyhz.avengers.domains.coach.template;

import java.util.Map;

import com.flyhz.avengers.framework.Template;

public class CoachProductDetailTemplate implements Template {

	@Override
	public void apply(Map<String, Object> context) {
		String url = (String) context.get("template.url");
		String htmlDoc = null;
		// HbaseShell shell = new HbaseShell();
		// shell.....
		htmlDoc = "<html>.....</html>";
	}
}
