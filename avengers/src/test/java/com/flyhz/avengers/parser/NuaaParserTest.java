
package com.flyhz.avengers.parser;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

public class NuaaParserTest extends TestCase {
	private String	tempUrl	= "http://science.nuaa.edu.cn/news_more.asp?lm2=110";

	@Test
	public void testGetUrls() {
		NuaaParser parser = new NuaaParser();
		long b = System.currentTimeMillis();
		List<String> urls = parser.getParserUrls(tempUrl);
		long e = System.currentTimeMillis();
		Assert.assertNotNull(urls);
		System.out.println("links number：" + urls.size());

		for (String url : urls) {
			System.out.println(url);
			List<String> twoUrls = parser.getParserTwoUrls(url);
			for (String twoUrl : twoUrls) {
				System.out.println("====" + twoUrl);
			}
		}
		System.out.println("花费时间" + (e - b) + " 毫秒");
	}

	@Test
	public void testParserHtmlToJsonStr() {

	}

}
