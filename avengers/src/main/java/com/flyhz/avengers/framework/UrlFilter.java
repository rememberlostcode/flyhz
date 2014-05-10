
package com.flyhz.avengers.framework;

import java.util.List;
import java.util.Set;

import com.flyhz.avengers.framework.xml.Domain.Templates.Template;

/**
 * URL过滤：属于URL白名单且不属于URL黑名单
 * 
 * @author fuwb 20140510
 */
public interface UrlFilter {
	/**
	 * 过滤有效UR
	 * 
	 * @param black
	 * @param templates
	 * @param waitFilterUrls
	 * @return
	 */
	public Set<String> filterValidUrl(List<String> black, List<Template> templates,
			List<String> waitFilterUrls);
}
