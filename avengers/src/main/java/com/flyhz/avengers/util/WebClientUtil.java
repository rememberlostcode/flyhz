
package com.flyhz.avengers.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * 
 * 类说明：网页爬虫工具类
 * 
 * @author robin 2014-5-6下午5:31:52
 * 
 */
public class WebClientUtil {

	public static String getContent(String url) {
		String html = "";
		// 模拟一个火狐浏览器
		final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
		try {
			// 设置webClient的相关参数
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setJavaScriptEnabled(true);
			// 模拟浏览器打开一个目标网址
			final HtmlPage page = webClient.getPage(url);
			if (page != null) {
				html = page.asXml();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			webClient.closeAllWindows();
		}
		return html;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = "http://www.coach.com/online/handbags/Product-bleecker_preston_satchel_in_edgepaint_leather-10551-10051-30165-en?cs=svcke&catId=5000000000000000002";
		String html = WebClientUtil.getContent(url);
		System.out.println(html);
	}

}
