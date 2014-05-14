package com.flyhz.avengers;

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
import com.flyhz.avengers.parser.CoachUrlParser;
import com.flyhz.avengers.util.WebClientUtil;

public class RecursiveDemo {
	private Logger log = LoggerFactory.getLogger(CoachUrlParser.class);

	/**
	 * 
	 * @param siteName 站点名称
	 * @param baseUrl 网站前缀
	 * @param homeUrl 首页URL
	 * @param deeps 深度
	 * @return
	 */
	public RtnResult parserContent(String siteName, String baseUrl,
			String homeUrl, int deeps) {
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

	private List<String> parserContent(String baseUrl, String homeUrl,
			List<String> urls, int layerNow, int deeps) {
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
							url = baseUrl + href;
						}
						urls.add(url);
						if (deeps > layerNow) {
							parserContent(baseUrl, url, urls, ++layerNow,
									deeps);
						}
					}
				}
			}
			return urls;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("采集时出错：", e.getMessage());
		}
		return null;
	}

	public String getDataType() {
		return "json";
	}

	public static void main(String[] args) {
		String siteName = "coach";
		String baseUrl = "http://rcms.zju.edu.cn/static/";
		String homeUrl = "http://rcms.zju.edu.cn/static/index1.html";
		int deeps = 3;
		RtnResult result = new RecursiveDemo().parserContent(siteName, baseUrl,
				homeUrl, deeps);
		System.out.println();
	}

}
