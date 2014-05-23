
package com.flyhz.avengers.domains.coach.template.impl.coach;

import com.flyhz.avengers.domains.coach.CoachUrlParser;
import com.flyhz.avengers.domains.dto.RtnResult;

public class CoachUrlParserImpl {

	public RtnResult parse(String url) {
		CoachUrlParser parser = new CoachUrlParser();
		RtnResult result = parser.parserContent(url);
		return result;
	}

}
