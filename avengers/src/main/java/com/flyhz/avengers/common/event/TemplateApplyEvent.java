
package com.flyhz.avengers.common.event;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.Analyze;
import com.flyhz.avengers.framework.Event;
import com.flyhz.avengers.framework.Template;

public class TemplateApplyEvent implements Event {
	private static final Logger	LOG	= LoggerFactory.getLogger(URLFetchEvent.class);

	@Override
	public boolean call(Map<String, Object> context) {
		LOG.info("templateApplyEvent begin..............");
		// 获得AnalyzeURL的Template，并执行apply方法
		if (context.get(Analyze.ANALYZE_TEMPLATE) != null) {
			Template template = (Template) context.get(Analyze.ANALYZE_TEMPLATE);
			template.apply(context);
		}
		LOG.info("templateApplyEvent end..............");
		return false;
	}
}
