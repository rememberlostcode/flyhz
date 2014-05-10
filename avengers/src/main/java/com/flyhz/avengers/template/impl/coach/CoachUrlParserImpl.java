
package com.flyhz.avengers.template.impl.coach;

import com.flyhz.avengers.dto.RtnResult;
import com.flyhz.avengers.framework.UrlParser;
import com.flyhz.avengers.parser.CoachUrlParser;

public class CoachUrlParserImpl implements UrlParser {

	@Override
	public RtnResult parser(String url) {
		CoachUrlParser parser = new CoachUrlParser();
		RtnResult result = parser.parserContent(url);
		return result;
	}

}
