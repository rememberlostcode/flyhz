
package com.flyhz.avengers.common.event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.util.Bytes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.Crawl;
import com.flyhz.avengers.framework.Event;
import com.flyhz.avengers.framework.config.XConfiguration;
import com.flyhz.avengers.framework.util.StringUtil;

public class URLCrawlEvent implements Event {

	private static final Logger	LOG		= LoggerFactory.getLogger(URLCrawlEvent.class);
	private String				rootUrl	= "";

	@SuppressWarnings("unchecked")
	@Override
	public boolean call(Map<String, Object> context) {
		String url = (String) context.get(Crawl.CRAWL_URL);
		if (StringUtils.isBlank(url))
			return false;

		Map<String, Object> domains = (Map<String, Object>) context.get(XConfiguration.AVENGERS_DOMAINS);
		Map<String, Object> domain = (Map<String, Object>) domains.get(url);

		String encode = (String) domain.get("encoding");
		Long depth = (Long) domain.get("crawl.depth");

		List<String> urlFilterBeforeCrawls = (List<String>) domain.get(XConfiguration.URLFILTER_BEFORE_CRAWL);
		List<String> urlFilterAfterCrawls = (List<String>) domain.get(XConfiguration.URLFILTER_AFTER_CRAWL);

		String tempUrl = "";
		String httpStr = "";
		if (url.startsWith("http")) {
			httpStr = url.substring(0, url.indexOf("://") + 3);
			tempUrl = url.substring(url.indexOf("://") + 3);
		} else {
			httpStr = "http://";
			tempUrl = url;
		}
		rootUrl = httpStr + tempUrl.substring(0, tempUrl.indexOf("/"));

		recursiveMethod(url, null, depth, encode, urlFilterBeforeCrawls, urlFilterAfterCrawls);
		return false;
	}

	/**
	 * 递归采集Url
	 */
	private void recursiveMethod(String crawlUrl, Set<String> alreadyCrawlUrls, Long depth,
			String charset, List<String> urlFilterBeforeCrawls, List<String> urlFilterAfterCrawls) {
		if (alreadyCrawlUrls == null) {
			alreadyCrawlUrls = new HashSet<String>();
		}
		Set<String> urls = new HashSet<String>();
		URL url = null;
		HttpURLConnection connection = null;
		try {
			if (!crawlUrl.startsWith("http")) {
				crawlUrl = "http://" + crawlUrl;
			}
			url = new URL(crawlUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			connection.setRequestProperty("Charset", charset); // 设置编码

			connection.connect();

			// Map<String, List<String>> map = connection.getHeaderFields();
			//
			// // 遍历所有的响应头字段
			//
			// for (String key : map.keySet()) {
			//
			// System.out.println(key + "--->" + map.get(key));
			//
			// }

			// 定义BufferedReader输入流来读取URL的响应
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(),
					Charset.forName(charset)));

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
			// LOG.info(doc.html());

			Elements linksElements = doc.select("a");
			if (linksElements != null) {
				int size = linksElements.size();
				for (int i = 0; i < size; i++) {
					String href = linksElements.get(i).attr("href");
					if (StringUtil.isNotBlank(href)) {
						if (href.startsWith("javascript:"))
							continue;

						if (!href.startsWith("http://")) {
							if (href.startsWith("/")) {
								href = rootUrl + href;
							} else {
								href = crawlUrl + "/" + href;
							}
						}

						// 只有通过过滤条件才放入队列里
						if (StringUtil.filterUrl(href, urlFilterBeforeCrawls)
								|| StringUtil.filterUrl(href, urlFilterAfterCrawls)) {
							// 判断是否已经采过，采了就不要再采了
							if (alreadyCrawlUrls.add(href))
								urls.add(href);
						}
					}
				}
			}

			if (urls != null && !urls.isEmpty()) {
				Long new_depth = depth - 1;
				for (String tempUrl : urls) {
					LOG.info("=======url_number===========" + new_depth + "==========begin========");
					if (StringUtil.filterUrl(tempUrl, urlFilterAfterCrawls)) {
						LOG.info("=====AfterFilter=====" + tempUrl);
						insertInfoToHbase(tempUrl, "");
					} else if (StringUtil.filterUrl(tempUrl, urlFilterBeforeCrawls)) {
						LOG.info("=====beforeFilter=====" + tempUrl);
						if (new_depth > 0) {
							recursiveMethod(tempUrl, alreadyCrawlUrls, new_depth, charset,
									urlFilterBeforeCrawls, urlFilterAfterCrawls);
						}
					}
				}
				if (new_depth > 0) {
					depth -= 1;
				}

				LOG.info("=======depth===========" + new_depth + "==========end========");
				insertInfoToHbase(crawlUrl, doc.html());
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
	}

	private void insertInfoToHbase(String crawlUrl, String htmlContent) throws IOException,
			MasterNotRunningException, ZooKeeperConnectionException, InterruptedIOException,
			RetriesExhaustedWithDetailsException {
		LOG.info("init hbase");
		if (StringUtil.isBlank(crawlUrl))
			return;

		Configuration hconf = HBaseConfiguration.create();
		hconf.set("hbase.zookeeper.quorum", "m1,s1,s2");
		hconf.set("hbase.zookeeper.property.clientPort", "2181");
		HConnection hConnection = HConnectionManager.createConnection(hconf);
		HBaseAdmin hbaseAdmin = new HBaseAdmin(hConnection);
		if (!hbaseAdmin.tableExists("av_page")) {
			HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf("av_page"));
			HColumnDescriptor columnConf = new HColumnDescriptor("page");
			tableDesc.addFamily(columnConf);
			hbaseAdmin.createTable(tableDesc);
		}

		HTable table = null;
		try {
			hbaseAdmin.flush("av_page");
			table = new HTable(hconf, "av_page");
			Put put = new Put(Bytes.toBytes(crawlUrl));
			// 参数出分别：列族、列、值
			put.add(Bytes.toBytes("info"), Bytes.toBytes("response"), Bytes.toBytes(htmlContent));
			table.put(put);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (table != null) {
				table.close();
			}
			if (hbaseAdmin != null) {
				hbaseAdmin.close();
			}
			if (hConnection != null) {
				hConnection.close();
			}
		}
	}
}
