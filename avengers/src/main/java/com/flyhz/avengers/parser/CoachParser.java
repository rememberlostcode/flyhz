
package com.flyhz.avengers.parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.dto.ProductModel;
import com.flyhz.avengers.util.WebClientUtil;

/**
 * 
 * 类说明：coach解析器
 * 
 * @author robin 2014-5-5下午7:00:21
 * 
 */
public class CoachParser {
	Logger			log			= LoggerFactory.getLogger(CoachParser.class);
	private String	tempUrl		= "http://www.coach.com/online/handbags/";
	private String	chartset	= "UTF-8";

	public List<String> getAllProductUrls(String url) {
		List<String> urls = new ArrayList<String>();
		String html = WebClientUtil.getContent(url);

		if (StringUtils.isNotBlank(html)) {
			Document doc = Jsoup.parse(html);
			Element e = doc.getElementById("productGrid");
			Elements linksElements = e.select("div.oneByOne");
			log.debug("品牌：coach");
			for (Element ele : linksElements) {
				String href = ele.select("a").eq(0).attr("href");
				if (StringUtils.isNotBlank(href))
					urls.add(tempUrl + href);
			}
		}
		return urls;
	}

	/**
	 * 解析单个产品页面信息
	 * 
	 * @param url
	 * @return
	 */
	public ProductModel parserHtmlToProduct(String url) {
		String html = WebClientUtil.getContent(url);
		ProductModel product = null;
		if (StringUtils.isNotBlank(html)) {
			Document doc = Jsoup.parse(html);
			Element mainEl = doc.getElementById("prod_container");
			Elements prodElements = mainEl != null ? mainEl.select("div.prod_desc_container")
					: null;
			if (prodElements == null)
				return product;

			log.debug("品牌:coach");
			if (prodElements != null) {
				Element ele = prodElements.get(0);
				String name = ele.select("h1").eq(0).text();
				if (StringUtils.isBlank(name))
					return null;

				product = new ProductModel();
				product.setName(name);

				Elements es = ele.select("div .pdTabProductStyle");
				if (es != null) {
					String style = es.eq(0).text();
					if (StringUtils.isNotBlank(style)) {
						style = style.substring(style.indexOf("Style No.") + 10).trim();
						if (StringUtils.isNotBlank(style))
							product.setBrandstyle(style);
					}
				}

				// 采价格
				es = ele.select("span #pdTabProductPriceSpan");
				if (es != null) {
					String price = es.eq(0).text();
					if (StringUtils.isNotBlank(price) && price.indexOf("$") > -1) {
						try {
							double prodPrice = Double.valueOf(price.substring(
									price.indexOf("$") + 1).trim());
							if (prodPrice != 0) {
								product.setPurchasingprice(new BigDecimal(prodPrice));
							}
						} catch (NumberFormatException e) {
							log.error("解析产品价格时出错", e.getMessage());
						}
					} else {
						return null;
					}
				}

				// 采描述信息
				Element e = ele.getElementById("pdpDescription");
				if (e != null) {
					Elements ds = e.select("span");
					String description = ds != null ? ds.eq(0).text() : null;
					if (StringUtils.isNotBlank(description)) {
						product.setDescription(description.trim().substring(0,
								description.lastIndexOf(".") + 1));
					}
				}

				// 采颜色信息
				e = ele.getElementById("selectedColorText");
				if (e != null) {
					String color = e.text();
					if (StringUtils.isNotBlank(color)) {
						product.setColor(color.trim());
					}
				}

				// 采图片
				StringBuffer imgs = new StringBuffer();
				es = mainEl.select("table[id=pdp-left]");
				if (es != null && !es.isEmpty()) {
					Element tableEl = es.get(0);
					es = tableEl.select("img");
					if (es != null && !es.isEmpty()) {
						imgs.append("[");
						for (Element imgEl : es) {
							String imgUrl = imgEl.attr("src");
							if (StringUtils.isNotBlank(imgUrl)) {
								if (imgs.length() > 1) {
									imgs.append(",");
								}
								imgs.append(imgUrl);
							}
						}
						imgs.append("]");
					}
				}

				if (imgs.length() > 0) {
					product.setImgs(imgs.toString());
				}

			}
		}
		return product;
	}
}
