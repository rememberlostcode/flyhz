
package com.flyhz.avengers.template.impl.coach;

import com.flyhz.avengers.dto.RtnResult;
import com.flyhz.avengers.framework.Template;
import com.flyhz.avengers.parser.CoachParser;

public class CoachTemplateImpl implements Template {

	@Override
	public RtnResult process(String url) {
		CoachParser parser = new CoachParser();
		RtnResult result = parser.parserContent(url);
		return result;
	}

}
