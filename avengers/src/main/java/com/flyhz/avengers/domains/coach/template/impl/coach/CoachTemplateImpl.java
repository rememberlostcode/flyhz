
package com.flyhz.avengers.domains.coach.template.impl.coach;

import com.flyhz.avengers.domains.coach.CoachParser;
import com.flyhz.avengers.domains.dto.RtnResult;

public class CoachTemplateImpl {

	public RtnResult doPattern(String url) {
		CoachParser parser = new CoachParser();
		RtnResult result = parser.parserContent(url);
		return result;
	}

}
