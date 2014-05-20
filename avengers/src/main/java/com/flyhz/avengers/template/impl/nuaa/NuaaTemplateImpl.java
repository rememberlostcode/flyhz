
package com.flyhz.avengers.template.impl.nuaa;

import com.flyhz.avengers.dto.RtnResult;
import com.flyhz.avengers.framework.Template;
import com.flyhz.avengers.parser.NuaaParser;

/**
 * 
 * 类说明：nuaa模板实现类
 * 
 * @author robin 2014-5-10下午1:55:42
 * 
 */
public class NuaaTemplateImpl implements Template {

	@Override
	public RtnResult process(String url) {
		NuaaParser parser = new NuaaParser();
		RtnResult result = parser.parserContent(url);
		return result;
	}

}
