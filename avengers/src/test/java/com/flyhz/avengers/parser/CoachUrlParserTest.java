
package com.flyhz.avengers.parser;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import com.flyhz.avengers.domains.coach.CoachUrlParser;
import com.flyhz.avengers.domains.dto.RtnResult;

public class CoachUrlParserTest extends TestCase {
	private String	testUrl	= "http://www.coach.com/online/handbags/-handbags_features_newarrivals_1-us-us-5000000000000015027-en?navCatId=62&LOC=HN2";

	@Test
	public void testParserContent() {
		CoachUrlParser coachParser = new CoachUrlParser();
		long b = System.currentTimeMillis();
		RtnResult result = coachParser.parserContent(testUrl);
		long e = System.currentTimeMillis();
		System.out.println("花费时间" + (e - b) + " 毫秒");

		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getList());

		System.out.println("Coach品牌handbags分类下的产品数：" + result.getList().size());
		List<String> urls = result.getList();
		for (String tempUrl : urls) {
			System.out.println(tempUrl);
		}

		System.out.println("网站名称：" + result.getSiteName());
		System.out.println("数据类型：" + result.getDataType());
	}
}
