
package com.flyhz.avengers.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * 类说明：html解析工具类
 * 
 * @author robin 2014-5-4下午7:34:28
 * 
 */
public class HtmlParserUtils {

	/**
	 * 根据URL获得所有的html信息
	 * 
	 * @param url
	 * @return
	 */
	public static String getHtmlByUrl(String url) {
		String html = null;
		HttpClient httpClient = new DefaultHttpClient();// 创建httpClient对象
		HttpGet httpget = new HttpGet(url);// 以get方式请求该URL
		try {
			HttpResponse responce = httpClient.execute(httpget);// 得到responce对象
			int resStatu = responce.getStatusLine().getStatusCode();// 返回码
			if (resStatu == HttpStatus.SC_OK) {// 200正常 其他就不对
				// 获得相应实体
				HttpEntity entity = responce.getEntity();
				if (entity != null) {
					html = EntityUtils.toString(entity);// 获得html源代码
				}
			}
		} catch (Exception e) {
			System.out.println("访问【" + url + "】出现异常!");
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return html;
	}

	/**
	 * 提取HTML文件的文本内容
	 * 
	 * @param html
	 *            提取的html文件名
	 * @return 返回提取内容String
	 */
	private static String getDocument(File html) {
		String text = "";
		try {
			// 设置编码集
			Document doc = Jsoup.parse(html, "UTF-8");
			// 提取标题信息
			Elements title = doc.select("title");
			for (org.jsoup.nodes.Element link : title) {
				text += link.text() + " ";
			}
			// 提取table中的文本信息
			Elements links = doc.select("table");
			for (Element link : links) {
				text += link.text() + " ";
			}
			// 提取div中的文本信息
			Elements divs = doc.select("div[class=post]");
			for (Element link : divs) {
				text += link.text() + " ";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return text;
	}

	/**
	 * 提取HTML的内容
	 * 
	 * @param html
	 * @return 返回提取内容String
	 */
	private static String getDocument(String html) {
		String text = "";
		try {
			// 设置编码集
			Document doc = Jsoup.parse(html, "UTF-8");
			// 提取标题信息
			Elements title = doc.select("title");
			for (org.jsoup.nodes.Element link : title) {
				text += link.text() + " ";
			}
			// 提取table中的文本信息
			Elements links = doc.select("table");
			for (org.jsoup.nodes.Element link : links) {
				text += link.text() + " ";
			}
			// 提取div中的文本信息
			Elements divs = doc.select("div[class=post]");
			for (org.jsoup.nodes.Element link : divs) {
				text += link.text() + " ";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return text;
	}

	public static void main(String[] name) {
		String url = "http://www.coach.com/online/handbags/-newatcoach_allnewatcoach-us-us-5000000000000000002-en?t1Id=5000000000000258802&t2Id=5000000000000000002&LOC=SN1";
		String html = getHtmlByUrl(url);

		if (StringUtils.isNotBlank(html)) {
			Document doc = Jsoup.parse(html);
			Element e = doc.getElementById("productGrid");
			Elements linksElements = e.select("div.oneByOne");
			// 以上代码的意思是 找id为“page”的div里面 id为“content”的div里面 id为“main”的div里面
			// class为“left”的div里面 id为“recommend”的div里面ul里面li里面a标签
			System.out.println("品牌:coach");
			String tempUrl = "http://www.coach.com/online/handbags/";
			for (Element ele : linksElements) {
				String href = ele.select("a").attr("href");
				if (StringUtils.isNotBlank(href))
					System.out.println(tempUrl + href);
			}
		}
	}
}
