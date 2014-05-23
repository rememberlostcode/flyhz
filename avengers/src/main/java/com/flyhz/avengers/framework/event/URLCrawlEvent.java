
package com.flyhz.avengers.framework.event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.Event;

public class URLCrawlEvent implements Event {

	private static final Logger	LOG	= LoggerFactory.getLogger(URLCrawlEvent.class);

	@Override
	public boolean call(Map<String, Object> context) {
		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL((String) context.get("crawl.url"));
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");

			connection.connect();

			Map<String, List<String>> map = connection.getHeaderFields();

			// 遍历所有的响应头字段

			for (String key : map.keySet()) {

				System.out.println(key + "--->" + map.get(key));

			}

			// 定义BufferedReader输入流来读取URL的响应
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(),
					Charset.forName("gb2312")));

			StringBuffer sb = new StringBuffer();
			String line;

			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			// System.out.println(sb.toString());
			// URLCrawlEvent parser = URLCrawlEvent.createParser(sb.toString(),
			// "gb2312");
			// NodeFilter filter = new TagNameFilter("table");
			// NodeList list = parser.parse(tableFilter);
			Document doc = Jsoup.parse(sb.toString());
			LOG.info(doc.html());
			Elements tables = doc.getElementsByTag("table");
			System.out.println(tables.size());
			for (Element table : tables) {
				// System.out.println(tr.child(0).text());
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return false;
	}
}
