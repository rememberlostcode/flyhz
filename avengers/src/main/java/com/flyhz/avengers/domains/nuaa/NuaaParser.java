
package com.flyhz.avengers.domains.nuaa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.domains.coach.template.NuaaTemplate;
import com.flyhz.avengers.domains.dto.NuaaPersonDto;
import com.flyhz.avengers.domains.dto.RtnResult;
import com.flyhz.avengers.domains.parser.BaseParser;
import com.flyhz.avengers.framework.util.UrlUtil;
import com.flyhz.avengers.framework.util.WebClientUtil;

/**
 * 
 * 类说明：nuaa解析器
 * 
 * @author robin 2014-5-8下午3:00:21
 * 
 */
public class NuaaParser implements BaseParser {
	private Logger	log			= LoggerFactory.getLogger(NuaaParser.class);
	private String	nuaaUrl		= "http://science.nuaa.edu.cn/";
	private String	chartset	= "UTF-8";

	public RtnResult parserContent(String url) {
		RtnResult rtnResult = new RtnResult();
		StringBuffer sb = new StringBuffer();
		sb.append("{\"data\":[");
		Map<Integer, String> map = getParserUrls(url);
		if (map != null && !map.isEmpty()) {
			Iterator<Entry<Integer, String>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, String> entry = (Entry<Integer, String>) it.next();
				Integer key = entry.getKey();
				String tempUrl = entry.getValue();
				String typeName = "";
				if (key.equals(1)) {
					typeName = "教授";
				} else if (key.equals(2)) {
					typeName = "副教授";
				} else if (key.equals(3)) {
					typeName = "讲师";
				}
				if (sb.length() > 15)
					sb.append(",");
				sb.append("{\"type\":{\"id\":");
				sb.append(key);
				sb.append(",\"name\":\"");
				sb.append(typeName);
				sb.append("\"");
				sb.append(",\"person\":[");
				if (StringUtils.isNotBlank(tempUrl)) {
					List<String> urls = getParserTwoUrls(tempUrl);
					if (urls != null && !urls.isEmpty()) {
						StringBuffer persons = new StringBuffer();
						for (String detailUrl : urls) {
							if (StringUtils.isNotBlank(detailUrl)) {
								String json = parserDetailHtmlToJsonStr(detailUrl);
								log.debug(detailUrl);
								log.debug(json);
								if (StringUtils.isNotBlank(json)) {
									if (persons.length() > 0)
										persons.append(",");
									persons.append(json);
								}
							}
						}
						if (persons.length() > 0)
							sb.append(persons.toString());
					}
				}
				sb.append("]}}");
			}
		}
		sb.append("]}");

		rtnResult.setSiteName("nuaa");
		rtnResult.setResult(sb.toString());
		rtnResult.setDataType(getDataType());
		return rtnResult;
	}

	/**
	 * 按类型采
	 * 
	 * @param url
	 * @return
	 */
	public Map<Integer, String> getParserUrls(String url) {
		Map<Integer, String> map = new HashMap<Integer, String>();
		NuaaTemplate template = new NuaaTemplate();
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
						Integer typeId = 0;
						String typeName = ele.text();
						if (StringUtils.isNotBlank(typeName)) {
							if (typeName.equals("教授")) {
								typeId = 1;
							} else if (typeName.equals("副教授")) {
								typeId = 2;
							} else if (typeName.equals("讲师")) {
								typeId = 3;
							}
						}
						String href = ele.attr(template.getLinkAttr());
						if (StringUtils.isNotBlank(href)) {
							if (!href.contains("http:")) {
								map.put(typeId, nuaaUrl + href);
							} else {
								map.put(typeId, href);
							}

						}
					}
				}
			}
		}
		return map;
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
			try {
				Document doc = Jsoup.parse(html);
				Elements mainEls = doc.select(template.getMainEls());
				if (mainEls != null) {
					Elements typeEls = mainEls.eq(0).select(template.getType());
					String type = typeEls != null ? typeEls.eq(0).text() : null;
					System.out.println("========" + type);

					Elements tdEls = mainEls.eq(0).select(template.getTdEls());
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
			} catch (Exception e1) {
				log.error("解析NUAA异常：", e1.getMessage());
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
		NuaaTemplate template = new NuaaTemplate();
		long begin = System.currentTimeMillis();
		String html = WebClientUtil.getContent(url, true, true);
		long end = System.currentTimeMillis();
		log.debug("爬网页时，花费时间{}毫秒", new Object[] { (end - begin) });

		try {
			NuaaPersonDto nuaaPerson = null;
			if (StringUtils.isNotBlank(html)) {
				nuaaPerson = new NuaaPersonDto();
				Document doc = Jsoup.parse(html);
				Elements mainEls = doc.select(template.getMainEls());
				Elements trEls = mainEls != null ? mainEls.eq(0).select(template.getTrEls()) : null;
				if (trEls != null) {
					int trSize = trEls.size();
					for (int i = 0; i < trSize; i++) {
						Elements tdEls = trEls.eq(i).select("td");
						if (tdEls != null && !tdEls.isEmpty()) {
							String text = tdEls.eq(0).text();
							if (StringUtils.isNotBlank(text)) {
								if (text.contains("姓名")) {
									text = tdEls.eq(1).html();
									if (StringUtils.isNotBlank(text)) {
										nuaaPerson.setName(text.replaceAll("&nbsp;", "").trim());

										Element imgE = tdEls.get(2).getElementById("userImg");
										String userImg = imgE.attr("src");
										if (StringUtils.isNotBlank(userImg)
												&& userImg.contains(template.getImgFilter())) {
											if (!userImg.contains("http:")) {
												if (userImg.charAt(0) == '/')
													userImg = userImg.substring(1);
												userImg = nuaaUrl + userImg;
											}
											nuaaPerson.setUserImg(userImg);
										}
									}
								} else if (text.contains("姓别")) {
									text = tdEls.eq(1).html();
									if (StringUtils.isNotBlank(text))
										nuaaPerson.setSex(text.replaceAll("&nbsp;", "").trim());
								} else if (text.contains("职称")) {
									text = tdEls.eq(1).html();
									if (StringUtils.isNotBlank(text))
										nuaaPerson.setJobTitle(text.replaceAll("&nbsp;", "").trim());

								} else if (text.contains("职务")) {
									text = tdEls.eq(1).html();
									if (StringUtils.isNotBlank(text))
										nuaaPerson.setPosition(text.replaceAll("&nbsp;", "").trim());

								} else if (text.contains("学历/学位")) {
									text = tdEls.eq(1).html();
									if (StringUtils.isNotBlank(text))
										nuaaPerson.setEducationDegree(text.replaceAll("&nbsp;", "")
																			.trim());

								} else if (text.contains("导师类别")) {
									text = tdEls.eq(1).html();
									if (StringUtils.isNotBlank(text))
										nuaaPerson.setMentorCategory(text.replaceAll("&nbsp;", "")
																			.trim());

								} else if (text.contains("所在学院")) {
									text = tdEls.eq(1).html();
									if (StringUtils.isNotBlank(text))
										nuaaPerson.setCollege(text.replaceAll("&nbsp;", "").trim());

								} else if (text.contains("专业")) {
									text = tdEls.eq(1).html();
									if (StringUtils.isNotBlank(text))
										nuaaPerson.setProfession(text.replaceAll("&nbsp;", "")
																		.trim());

								} else if (text.contains("研究方向")) {
									text = tdEls.eq(1).html();
									if (StringUtils.isNotBlank(text))
										nuaaPerson.setResearchDirection(text.replaceAll("&nbsp;",
												"").trim());

								} else if (text.contains("社会兼职")) {
									text = tdEls.eq(1).html();
									if (StringUtils.isNotBlank(text))
										nuaaPerson.setSocialAppointments(text.replaceAll("&nbsp;",
												"").trim());

								} else if (text.contains("荣誉称号")) {
									text = tdEls.eq(1).html();
									if (StringUtils.isNotBlank(text))
										nuaaPerson.setHonorary(text.replaceAll("&nbsp;", "").trim());

								} else if (text.contains("科研情况")) {
									text = tdEls.eq(1).html();
									if (StringUtils.isNotBlank(text))
										nuaaPerson.setScientificResearch(text.replaceAll("&nbsp;",
												"").trim());

								} else if (text.contains("研究成果")) {
									text = tdEls.eq(1).html();
									if (StringUtils.isNotBlank(text))
										nuaaPerson.setResearchResults(text.replaceAll("&nbsp;", "")
																			.trim());

								} else if (text.contains("学术经历")) {
									text = tdEls.eq(1).html();
									if (StringUtils.isNotBlank(text))
										nuaaPerson.setAcademicExperience(text.replaceAll("&nbsp;",
												"").trim());

								} else if (text.contains("课程链接")) {
									text = tdEls.eq(1).select("a").eq(0).attr("href");
									if (StringUtils.isNotBlank(text))
										nuaaPerson.setCourseLinks(text.replaceAll("&nbsp;", "")
																		.trim());

								} else if (text.contains("其它")) {
									text = tdEls.eq(1).html();
									if (StringUtils.isNotBlank(text))
										nuaaPerson.setOther(text.replaceAll("&nbsp;", "").trim());

								}

							}
						}
					}
				}
				return nuaaPerson.toString();
			}
		} catch (Exception e) {
			log.error("采集nuaa时出错：", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getDataType() {
		return "json";
	}
}
