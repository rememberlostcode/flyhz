
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

import com.flyhz.avengers.dto.RtnResult;
import com.flyhz.avengers.template.CoachListTemplate;
import com.flyhz.avengers.util.WebClientUtil;

/**
 * 
 * 类说明：coachURL列表解析器
 * 
 * @author robin 2014-5-5下午7:00:21
 * 
 */
public class CoachUrlParser implements BaseParser {
	private Logger	log			= LoggerFactory.getLogger(CoachUrlParser.class);
	private String	tempUrl		= "http://www.coach.com/online/handbags/";
	private String	chartset	= "UTF-8";

	@Override
	public RtnResult parserContent(String url) {
		try {
			CoachListTemplate template = new CoachListTemplate();
			List<String> urls = new ArrayList<String>();
			long b = System.currentTimeMillis();
			String html = WebClientUtil.getContent(url, false, true);
			long e = System.currentTimeMillis();
			log.debug("爬网页时，花费时间{}毫秒", new Object[] { (e - b) });

			if (StringUtils.isNotBlank(html)) {
				Document doc = Jsoup.parse(html);
				Elements mainEls = doc.select(template.getMainEls());
				Elements linksElements = mainEls != null ? mainEls.eq(0).select(
						template.getLinksElements()) : null;
				log.debug("品牌：coach");
				if (linksElements != null) {
					for (Element ele : linksElements) {
						String href = ele.select("a").eq(0).attr("href");
						if (StringUtils.isNotBlank(href))
							urls.add(tempUrl + href);
					}
				}
			}
			if (urls != null && !urls.isEmpty()) {
				RtnResult result = new RtnResult();
				result.setSiteName("coach");
				result.setDataType(getDataType());
				result.setList(urls);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("采集coach列表页时出错：", e.getMessage());
		}
		return null;
	}

	@Override
	public String getDataType() {
		return "json";
	}
}
