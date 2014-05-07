
package com.flyhz.avengers.parser;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import com.flyhz.avengers.dto.ProductModel;

public class CoachParserTest extends TestCase {
	private String	testUrl	= "http://www.coach.com/online/handbags/-newatcoach_allnewatcoach-us-us-5000000000000000002-en?t1Id=5000000000000258802&t2Id=5000000000000000002&LOC=SN1";

	// @Test
	// public void testGetAllProductUrls() {
	// CoachParser coachParser = new CoachParser();
	// long b = System.currentTimeMillis();
	// List<String> urls = coachParser.getAllProductUrls(testUrl);
	// long e = System.currentTimeMillis();
	// Assert.assertNotNull(urls);
	// for (String tempUrl : urls) {
	// System.out.println(tempUrl);
	// }
	// System.out.println("Coach品牌handbags分类下的产品数：" + urls.size());
	// System.out.println("花费时间" + (e - b) + " 毫秒");
	// }

	@Test
	public void testParserHtmlToProduct() {
		String url = "http://www.coach.com/online/handbags/Product-bleecker_preston_satchel_in_edgepaint_leather-10551-10051-30165-en?cs=svcke&catId=5000000000000000002";
		CoachParser coachParser = new CoachParser();
		long b = System.currentTimeMillis();
		ProductModel product = coachParser.parserHtmlToProduct(url);
		long e = System.currentTimeMillis();
		Assert.assertNotNull(product);

		System.out.println("商品名称：" + product.getName());
		System.out.println("款号：" + product.getBrandstyle());
		System.out.println("价格：" + product.getPurchasingprice());
		System.out.println("颜色：" + product.getColor());
		System.out.println("商品描述：" + product.getDescription());
		System.out.println("商品图片：" + product.getImgs());
		System.out.println("花费时间：" + (e - b) + " 毫秒");
	}
}
