
package com.flyhz.avengers.framework.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static Logger	log	= LoggerFactory.getLogger(WebClientUtil.class);

	/**
	 * 
	 * @param url
	 * @param css
	 *            为true时，
	 * @param js
	 * @return
	 */
	public static String getContent(String url, boolean css, boolean js) {
		if (StringUtils.isBlank(url))
			return null;

		String html = "";
		// 模拟一个浏览器
		final WebClient webClient = new WebClient();
		try {
			// 设置webClient的相关参数
			webClient.getOptions().setCssEnabled(css);
			webClient.getOptions().setJavaScriptEnabled(js);
			webClient.getOptions().setThrowExceptionOnScriptError(false); // js运行错误时，是否抛出异常
			webClient.getOptions().setTimeout(0); // 设置连接超时时间
													// ，这里是10S。如果为0，则无限期等待
			// 模拟浏览器打开一个目标网址
			final HtmlPage page = webClient.getPage(url);
			if (page != null) {
				html = page.asXml();
			}
		} catch (Exception e) {
			log.error("爬网页时出错：", e.getMessage());
		} finally {
			webClient.closeAllWindows();
		}

		if (StringUtils.isBlank(html)) {
			log.debug("=======采用直接请求方式=========");
			html = UrlUtil.sendGet(url);
		}
		return html;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String url = "http://www.coach.com/online/handbags/Product-bleecker_preston_satchel_in_edgepaint_leather-10551-10051-30165-en?cs=svcke&catId=5000000000000000002";
		String html = WebClientUtil.getContent(url, false, true);
		System.out.println(html);
	}

}
