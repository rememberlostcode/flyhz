
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

import com.flyhz.avengers.TestAll;
import com.flyhz.avengers.dto.ProductDto;
import com.flyhz.avengers.dto.RtnResult;
import com.flyhz.avengers.template.CoachDetailTemplate;
import com.flyhz.avengers.template.CoachUrlTemplate;
import com.flyhz.avengers.util.UrlUtil;
import com.flyhz.avengers.util.WebClientUtil;

/**
 * 
 * 类说明：coach解析器
 * 
 * @author robin 2014-5-5下午7:00:21
 * 
 */
public class CoachParser implements BaseParser {
	private Logger	log			= LoggerFactory.getLogger(CoachParser.class);
	private String	tempUrl		= "http://www.coach.com/online/handbags/";
	private String	chartset	= "UTF-8";

	public List<String> getAllProductUrls(String url) {
		CoachUrlTemplate template = new CoachUrlTemplate();
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
		return urls;
	}

	/**
	 * 解析单个产品页面信息
	 * 
	 * @param url
	 * @return
	 */
	public ProductDto parserHtmlToProduct(String url) {
		CoachDetailTemplate template = new CoachDetailTemplate();
		long begin = System.currentTimeMillis();
		String html = WebClientUtil.getContent(url, true, true);
		long end = System.currentTimeMillis();
		log.debug("爬网页时，花费时间{}毫秒", new Object[] { (end - begin) });

		ProductDto product = null;
		if (StringUtils.isNotBlank(html)) {
			Document doc = Jsoup.parse(html);
			Elements mainEls = doc.select(template.getMainEls());
			Elements prodElements = mainEls != null ? mainEls.eq(0).select(
					template.getProdElements()) : null;
			if (prodElements == null)
				return product;

			log.debug("品牌:coach");
			if (prodElements != null) {
				Element ele = prodElements.get(0);
				String name = ele.select(template.getName()).eq(0).text();
				if (StringUtils.isBlank(name))
					return null;

				product = new ProductDto();
				product.setName(name);

				Elements es = ele.select(template.getStyle());
				if (es != null) {
					String style = es.eq(0).text();
					if (StringUtils.isNotBlank(style)) {
						style = style.substring(style.indexOf(template.getStyleSeparate()) + 10)
										.trim();
						if (StringUtils.isNotBlank(style))
							product.setBrandstyle(style);
					}
				}

				// 采价格
				es = ele.select(template.getPrice());
				if (es != null) {
					String price = es.eq(0).text();
					if (StringUtils.isNotBlank(price) && price.indexOf("$") > -1) {
						try {
							double prodPrice = Double.valueOf(price.substring(
									price.indexOf("$") + 1).trim());
							if (prodPrice != 0) {
								product.setPurchasingprice(new BigDecimal(prodPrice));
							}
						} catch (NumberFormatException ex) {
							log.error("解析产品价格时出错", ex.getMessage());
						}
					} else {
						return null;
					}
				}

				// 采描述信息
				es = ele.select(template.getDescription());
				if (es != null) {
					Elements ds = es.select("span");
					String description = ds != null ? ds.eq(0).text() : null;
					if (StringUtils.isNotBlank(description)) {
						product.setDescription(description.trim().substring(0,
								description.lastIndexOf(".") + 1));
					}
				}

				// 采颜色信息
				es = ele.select(template.getColor());
				if (es != null) {
					String color = es.eq(0).text();
					if (StringUtils.isNotBlank(color)) {
						product.setColor(color.trim());
					}
				}

				// 采图片
				StringBuffer imgs = new StringBuffer();
				es = mainEls.select(template.getImgEls());
				if (es != null && !es.isEmpty()) {
					Element tableEl = es.get(0);
					Elements imgEls = tableEl.select("img");
					if (imgEls != null && !imgEls.isEmpty()) {
						imgs.append("[");
						for (Element imgEl : imgEls) {
							String imgUrl = imgEl.attr("src");
							if (StringUtils.isNotBlank(imgUrl)
									&& !imgUrl.contains(template.getImgUrlFilter())) {
								imgUrl = imgEl.attr(template.getImgAttr());
							}
							// 在这里过滤
							if (imgUrl.contains(template.getImgUrlFilter())) {
								if (imgs.length() > 1) {
									imgs.append(",");
								}
								imgs.append("\"");
								imgs.append(imgUrl);
								imgs.append("\"");
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

	@Override
	public RtnResult parserContent(String url) {
		try {
			ProductDto product = parserHtmlToProduct(url);
			if (product != null) {
				RtnResult result = new RtnResult();
				result.setSiteName("coach");
				result.setDataType(getDataType());
				result.setResult(product.toString());
				TestAll.productList.add(product.getImgs());
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("采集coach详情页时出错：", e.getMessage());
		}
		return null;
	}

	@Override
	public String getDataType() {
		return "json";
	}
}
