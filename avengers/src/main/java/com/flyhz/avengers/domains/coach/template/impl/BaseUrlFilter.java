
package com.flyhz.avengers.domains.coach.template.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.flyhz.avengers.framework.config.xml.XTemplate;

/**
 * URL过滤器基本方法
 * 
 * @author fuwb 20140510
 */
public class BaseUrlFilter {
	/**
	 * 待过滤URL去重
	 * 
	 * @param waitFilterUrls
	 * @return Set<String>
	 */
	public Set<String> rmDuplicate(List<String> waitFilterUrls) {
		if (waitFilterUrls != null && !waitFilterUrls.isEmpty()) {
			Set<String> validUrls = new HashSet<String>();
			for (String waitFilterUrl : waitFilterUrls) {
				validUrls.add(waitFilterUrl);
			}
			return validUrls;
		}
		return null;
	}

	/**
	 * 校验URL白名单
	 * 
	 * @param templates
	 * @param waitFilterUrls
	 * @return Set<String>
	 */
	public Set<String> checkWhiteUrls(List<XTemplate> templates, Set<String> waitFilterUrls) {
		if (waitFilterUrls != null && !waitFilterUrls.isEmpty() && templates != null
				&& !templates.isEmpty()) {
			Set<String> validUrls = new HashSet<String>();
			for (String waitFilterUrl : waitFilterUrls) {
				for (XTemplate template : templates) {
					String whiteRegex = template.getPattern();
					if (StringUtils.isNotBlank(whiteRegex)) {
						boolean result = Pattern.compile(whiteRegex).matcher(waitFilterUrl).find();
						if (result) {
							validUrls.add(waitFilterUrl);
							break;
						}
					}
				}
			}
			return validUrls;
		}
		return waitFilterUrls;
	}

	/**
	 * 校验URL黑名单
	 * 
	 * @param black
	 * @param waitFilterUrls
	 * @return Set<String>
	 */
	public Set<String> checkBlackUrls(List<String> black, Set<String> waitFilterUrls) {
		if (waitFilterUrls != null && !waitFilterUrls.isEmpty() && black != null
				&& !black.isEmpty()) {
			Set<String> validUrls = new HashSet<String>();
			validUrls.addAll(waitFilterUrls);
			for (String waitFilterUrl : waitFilterUrls) {
				for (String blackRegex : black) {
					if (StringUtils.isNotBlank(blackRegex)) {
						boolean result = Pattern.compile(blackRegex).matcher(waitFilterUrl).find();
						if (result && validUrls.contains(waitFilterUrl)) {
							validUrls.remove(waitFilterUrl);
							break;
						}
					}
				}
			}
			return validUrls;
		}
		return waitFilterUrls;
	}
}
