
package com.flyhz.avengers.domains.coach.template.impl.nuaa;

import com.flyhz.avengers.domains.dto.RtnResult;
import com.flyhz.avengers.domains.nuaa.NuaaParser;

/**
 * 
 * 类说明：nuaa模板实现类
 * 
 * @author robin 2014-5-10下午1:55:42
 * 
 */
public class NuaaTemplateImpl {

	public RtnResult doPattern(String url) {
		NuaaParser parser = new NuaaParser();
		RtnResult result = parser.parserContent(url);
		return result;
	}

}
