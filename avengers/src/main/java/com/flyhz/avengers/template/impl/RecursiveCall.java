
package com.flyhz.avengers.template.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.dto.RtnResult;
import com.flyhz.avengers.framework.UrlFilter;
import com.flyhz.avengers.framework.xml.Avengers;
import com.flyhz.avengers.framework.xml.Domain;
import com.flyhz.avengers.template.impl.coach.CoachUrlFilterImpl;
import com.flyhz.avengers.util.AvengersDataUtil;
import com.flyhz.avengers.util.WebClientUtil;

/**
 * 
 * 类说明：递归调用
 * 
 * @author robin 2014-5-10下午2:33:07
 * 
 */
public class RecursiveCall {

	private Logger	log	= LoggerFactory.getLogger(RecursiveCall.class);

	/**
	 * 
	 * @param siteName
	 * 站点名称
	 * @param baseUrl
	 * 网站前缀
	 * @param homeUrl
	 * 首页URL
	 * @param deeps
	 * 深度
	 * @return
	 */
	public RtnResult parserContent(String siteName, String baseUrl, String homeUrl, int deeps) {
		RtnResult result = new RtnResult();
		List<String> urls = parserContent(baseUrl, homeUrl, null, 1, deeps);
		if (urls != null && !urls.isEmpty()) {
			result.setSiteName(siteName);
			result.setDataType(getDataType());
			result.setList(urls);
			return result;
		}
		return result;
	}

	private List<String> parserContent(String baseUrl, String homeUrl, List<String> urls,
			int layerNow, int deeps) {
		try {
			if (urls == null) {
				urls = new ArrayList<String>();
			}
			long b = System.currentTimeMillis();
			String html = WebClientUtil.getContent(homeUrl, false, true);
			long e = System.currentTimeMillis();
			log.debug("爬网页时，花费时间{}毫秒", new Object[] { (e - b) });

			if (StringUtils.isNotBlank(html)) {
				Document doc = Jsoup.parse(html);
				Elements aEles = doc.select("a");
				for (Element ele : aEles) {
					String href = ele.attr("href");
					if (StringUtils.isNotBlank(href)) {
						String url = "";
						if (href.startsWith("http")) {
							url = href;
						} else {
							if (href.startsWith("/")) {
								url = baseUrl + href;
							} else {
								if (!href.startsWith("javascript")) {
									url = homeUrl.substring(0, homeUrl.lastIndexOf("/") + 1) + href;
								}
							}
						}

						if (StringUtils.isNotBlank(url)) {
							urls.add(url);
							if (deeps > layerNow) {
								parserContent(baseUrl, url, urls, layerNow, deeps);
							}
						}
					}
				}
				++layerNow;
			}
			return urls;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("递归采集url时出错：", e.getMessage());
		}
		return null;
	}

	public String getDataType() {
		return "json";
	}

	public static void main(String[] args) {
		RecursiveCall recursiveCall = new RecursiveCall();
		UrlFilter urlFilter = new CoachUrlFilterImpl();
		Avengers avengers = AvengersDataUtil.getDataByXmlFileName("avengers.xml");
		List<Domain> domains = avengers.getDomain();
		int deeps = 3;

		for (int k = 0; k < domains.size(); k++) {
			Domain domain = domains.get(k);
			List<String> waitFilterUrls = null;
			String siteName = "coach";
			String baseUrl = domain.getRoot();
			String homeUrl = "http://www.coach.com/online/handbags/Home-10551-10051-en";
			RtnResult result = recursiveCall.parserContent(siteName, baseUrl, homeUrl, deeps);
			if (result != null) {
				waitFilterUrls = result.getList();
			}
			System.out.println("----" + domain.getRoot()
					+ "===============begin======================");
			if (waitFilterUrls != null) {
				for (String url : waitFilterUrls) {
					System.out.println(url);
				}
			}

			System.out.println("----" + domain.getRoot()
					+ "===============end======================");

			List<String> newList = urlFilter.filterValidUrl(domain, waitFilterUrls);
			if (newList != null) {
				for (String url : newList) {
					System.out.println(url);
				}
			}
		}
	}

}
