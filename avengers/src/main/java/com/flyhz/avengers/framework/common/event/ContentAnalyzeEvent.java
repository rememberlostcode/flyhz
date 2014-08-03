
package com.flyhz.avengers.framework.common.event;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.common.dto.PageRowDataDto;
import com.flyhz.avengers.framework.config.TemplateConfig;
import com.flyhz.avengers.framework.config.XConfiguration;

public class ContentAnalyzeEvent extends PageEvent {

	private static final Logger	LOG	= LoggerFactory.getLogger(URLFetchEvent.class);

	@SuppressWarnings("unchecked")
	@Override
	void processRowData(PageRowDataDto pageRowDataDto, Map<String, Object> context)
			throws Throwable {
		Map<String, Object> domainMap = (Map<String, Object>) context.get(pageRowDataDto.getRoot());
		List<TemplateConfig> templateConfigs = (List<TemplateConfig>) domainMap.get(XConfiguration.DOMAIN_TEMPLATES);
		for (TemplateConfig templateConfig : templateConfigs) {
			if (StringUtils.isNotBlank(templateConfig.getPattern())) {
				if (StringUtils.isNotBlank(pageRowDataDto.getUrl())) {
					// 校验URL是否匹配正则表达式
					if (Pattern.compile(templateConfig.getPattern())
								.matcher(pageRowDataDto.getUrl()).find()) {
						templateConfig.getTemplate().apply(pageRowDataDto, context);
					}
				}
			}
		}
	}
}
