
package com.flyhz.avengers.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.template.CoachDetailTemplate;
import com.flyhz.avengers.template.NuaaTemplate;
import com.flyhz.avengers.util.UrlUtil;
import com.flyhz.avengers.util.WebClientUtil;

/**
 * 
 * 类说明：nuaa解析器
 * 
 * @author robin 2014-5-8下午3:00:21
 * 
 */
public class NuaaParser {
	private Logger	log			= LoggerFactory.getLogger(NuaaParser.class);
	private String	nuaaUrl		= "http://science.nuaa.edu.cn/";
	private String	chartset	= "UTF-8";

	public List<String> getParserUrls(String url) {
		NuaaTemplate template = new NuaaTemplate();
		List<String> urls = new ArrayList<String>();
		long b = System.currentTimeMillis();
		String html = WebClientUtil.getContent(url, false, true);
		long e = System.currentTimeMillis();
		log.debug("爬网页时，花费时间{}毫秒", new Object[] { (e - b) });

		if (StringUtils.isNotBlank(html)) {
			Document doc = Jsoup.parse(html);
			Elements mainEls = doc.select(template.getMainEls());
			Elements trEls = mainEls != null ? mainEls.eq(0).select(template.getTrEls()) : null;
			if (trEls != null) {
				Elements linksElements = trEls.get(template.getTrNum()).select(
						template.getLinkEls());
				if (linksElements != null && !linksElements.isEmpty()) {
					for (Element ele : linksElements) {
						String href = ele.attr(template.getLinkAttr());
						if (StringUtils.isNotBlank(href) && !href.contains("http:")) {
							urls.add(nuaaUrl + href);
						} else {
							urls.add(href);
						}
					}
				}
			}
		}
		return urls;
	}

	/**
	 * 解析第二层url
	 * 
	 * @param url
	 * @return
	 */
	public List<String> getParserTwoUrls(String url) {
		NuaaTemplate template = new NuaaTemplate();
		List<String> urls = new ArrayList<String>();
		long b = System.currentTimeMillis();
		String html = WebClientUtil.getContent(url, false, true);
		if (StringUtils.isBlank(html)) {
			html = UrlUtil.sendGet(url);
		}
		long e = System.currentTimeMillis();
		log.debug("爬网页时，花费时间{}毫秒", new Object[] { (e - b) });

		if (StringUtils.isNotBlank(html)) {
			Document doc = Jsoup.parse(html);
			Elements mainEls = doc.select(template.getMainEls());
			Elements tdEls = mainEls != null ? mainEls.eq(0).select(template.getTdEls()) : null;
			if (tdEls != null) {
				for (Element td : tdEls) {
					Elements linksElements = td.select(template.getLinkEls());
					for (Element ele : linksElements) {
						String href = ele.attr(template.getLinkAttr());
						if (StringUtils.isNotBlank(href) && !href.contains("http:")) {
							urls.add(nuaaUrl + href);
						} else {
							urls.add(href);
						}
					}
				}
			}
		}
		return urls;
	}

	/**
	 * 解析详情页面信息
	 * 
	 * @param url
	 * @return
	 */
	public String parserDetailHtmlToJsonStr(String url) {
		CoachDetailTemplate template = new CoachDetailTemplate();
		long begin = System.currentTimeMillis();
		String html = WebClientUtil.getContent(url, true, true);
		long end = System.currentTimeMillis();
		log.debug("爬网页时，花费时间{}毫秒", new Object[] { (end - begin) });

		String product = null;
		if (StringUtils.isNotBlank(html)) {
			Document doc = Jsoup.parse(html);
			Elements mainEls = doc.select(template.getMainEls());
			Elements prodElements = mainEls != null ? mainEls.eq(0).select(
					template.getProdElements()) : null;
			if (prodElements == null)
				return product;

		}
		return product;
	}
}
