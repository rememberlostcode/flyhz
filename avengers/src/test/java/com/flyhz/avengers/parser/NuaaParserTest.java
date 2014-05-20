
package com.flyhz.avengers.parser;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import com.flyhz.avengers.dto.RtnResult;

public class NuaaParserTest extends TestCase {
	private String	tempUrl	= "http://science.nuaa.edu.cn/news_more.asp?lm2=110";

	// @Test
	// public void testGetUrls() {
	// NuaaParser parser = new NuaaParser();
	// Map<Integer, String> map = parser.getParserUrls(tempUrl);
	// Assert.assertNotNull(map);
	//
	// if (map != null && !map.isEmpty()) {
	// Iterator<Entry<Integer, String>> it = map.entrySet().iterator();
	// while (it.hasNext()) {
	// Entry<Integer, String> entry = (Entry<Integer, String>) it.next();
	// Integer key = entry.getKey();
	// String tempUrl = entry.getValue();
	// System.out.println("key:" + key + ",value:" + tempUrl);
	// List<String> twoUrls = parser.getParserTwoUrls(tempUrl);
	// for (String twoUrl : twoUrls) {
	// System.out.println("====" + twoUrl);
	// }
	// }
	// }
	// }
	//
	// @Test
	// public void testParserDetailHtmlToJsonStr() {
	// String url = "http://science.nuaa.edu.cn/szdw_view.asp?id=247";
	// NuaaParser parser = new NuaaParser();
	// long b = System.currentTimeMillis();
	// String result = parser.parserDetailHtmlToJsonStr(url);
	// long e = System.currentTimeMillis();
	// System.out.println("花费时间" + (e - b) + " 毫秒");
	// Assert.assertNotNull(result);
	// System.out.println(result);
	// }

	@Test
	public void testParserContent() {
		NuaaParser parser = new NuaaParser();
		long b = System.currentTimeMillis();
		RtnResult result = parser.parserContent(tempUrl);
		long e = System.currentTimeMillis();
		System.out.println("花费时间" + (e - b) + " 毫秒");

		Assert.assertNotNull(result);
		System.out.println("siteName:" + result.getSiteName());
		System.out.println("result:" + result.getResult());
		System.out.println("dataType:" + result.getDataType());
	}
}
