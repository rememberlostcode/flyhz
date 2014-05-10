
package com.flyhz.avengers.framework;

import java.util.List;

import com.flyhz.avengers.framework.xml.Domain;

/**
 * URL过滤：属于URL白名单且不属于URL黑名单
 * 
 * @author fuwb 20140510
 */
public interface UrlFilter {
	/**
	 * 过滤有效UR
	 * 
	 * @param domain
	 * @param waitFilterUrls
	 * @return
	 */
	public List<String> filterValidUrl(Domain domain, List<String> waitFilterUrls);
}
