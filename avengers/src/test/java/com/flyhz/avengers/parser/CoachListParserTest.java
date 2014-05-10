
package com.flyhz.avengers.parser;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import com.flyhz.avengers.dto.RtnResult;

public class CoachListParserTest extends TestCase {
	private String	testUrl	= "http://www.coach.com/online/handbags/-newatcoach_allnewatcoach-us-us-5000000000000000002-en?t1Id=5000000000000258802&t2Id=5000000000000000002&LOC=SN1";

	@Test
	public void testParserContent() {
		CoachListParser coachParser = new CoachListParser();
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
