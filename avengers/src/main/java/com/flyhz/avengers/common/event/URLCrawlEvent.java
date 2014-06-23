
package com.flyhz.avengers.common.event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
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
import com.flyhz.avengers.framework.util.SerializableUtil;
import com.flyhz.avengers.framework.util.StringUtil;

public class URLCrawlEvent implements Event {

	private static final Logger	LOG		= LoggerFactory.getLogger(URLCrawlEvent.class);
	private String				rootUrl	= "";
	private String				domainRoot;
	// 统计carwl urls总数
	private final AtomicInteger	total	= new AtomicInteger(0);

	@SuppressWarnings("unchecked")
	@Override
	public boolean call(Map<String, Object> context) {
		String url = (String) context.get(Crawl.CRAWL_URL);
		domainRoot = url;
		String version = (String) context.get(Crawl.VERSION);
		if (StringUtils.isBlank(url))
			return false;

		Map<String, Object> domains = (Map<String, Object>) context.get(XConfiguration.AVENGERS_DOMAINS);
		Map<String, Object> domain = (Map<String, Object>) domains.get(url);

		String encode = (String) domain.get(XConfiguration.ENCODING);
		Integer depth = (Integer) domain.get(XConfiguration.CRAWL_DEPTH);

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
		if (tempUrl.indexOf("/") > 0) {
			tempUrl = tempUrl.substring(0, tempUrl.indexOf("/"));
		}
		rootUrl = httpStr + tempUrl;

		try {
			recursiveMethod(url, version, null, depth, encode, urlFilterBeforeCrawls,
					urlFilterAfterCrawls);
			return true;
		} catch (ClassNotFoundException e) {
			LOG.error("", e);
		}
		return false;
	}

	/**
	 * 递归采集Url
	 * 
	 * @throws ClassNotFoundException
	 */
	private void recursiveMethod(String crawlUrl, String version, Set<String> alreadyCrawlUrls,
			Integer depth, String charset, List<String> urlFilterBeforeCrawls,
			List<String> urlFilterAfterCrawls) throws ClassNotFoundException {
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

			// 定义BufferedReader输入流来读取URL的响应
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(),
					Charset.forName(charset)));

			StringBuffer sb = new StringBuffer();
			String line;

			while ((line = in.readLine()) != null) {
				sb.append(line);
			}

			Document doc = Jsoup.parse(sb.toString());

			Elements linksElements = doc.select("a");
			if (linksElements != null) {
				String rootPath = "";
				if (crawlUrl.lastIndexOf("/") > 8) {
					rootPath = crawlUrl.substring(0, crawlUrl.lastIndexOf("/"));
				} else {
					rootPath = crawlUrl;

				}

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
								href = rootPath + "/" + href;
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
				Integer new_depth = depth - 1;
				LOG.info("=======url_number===========" + new_depth + "==========begin========");
				for (String tempUrl : urls) {
					if (StringUtil.filterUrl(tempUrl, urlFilterAfterCrawls)) {
						insertInfoToHbase(tempUrl, "", version);
					}
					if (StringUtil.filterUrl(tempUrl, urlFilterBeforeCrawls)) {
						LOG.info("=====beforeFilter=====" + tempUrl);
						if (new_depth > 0) {
							recursiveMethod(tempUrl, version, alreadyCrawlUrls, new_depth, charset,
									urlFilterBeforeCrawls, urlFilterAfterCrawls);
						}
					}
				}
				LOG.info("=======url_number===========" + new_depth + "==========end========");
				if (new_depth > 0) {
					depth -= 1;
				}

				LOG.info("=======depth===========" + depth + "==========end========");
				insertInfoToHbase(crawlUrl, doc.html(), version);
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

	@SuppressWarnings("unchecked")
	private void insertInfoToHbase(String crawlUrl, String htmlContent, String version)
			throws IOException, MasterNotRunningException, ZooKeeperConnectionException,
			InterruptedIOException, RetriesExhaustedWithDetailsException, ClassNotFoundException {
		LOG.info("init hbase");
		if (StringUtil.isBlank(crawlUrl))
			return;
		LOG.info("=====init hbase=====" + crawlUrl);
		Configuration hconf = HBaseConfiguration.create();
		hconf.set("hbase.zookeeper.quorum", "m1,s1,s2");
		hconf.set("hbase.zookeeper.property.clientPort", "2181");
		HConnection hConnection = HConnectionManager.createConnection(hconf);
		HBaseAdmin hbaseAdmin = new HBaseAdmin(hConnection);

		HTable hPage = null;
		HTable hDomain = null;
		try {
			hPage = new HTable(hconf, "av_page");
			byte[] rowKey = Bytes.toBytes(crawlUrl);
			Get get = new Get(rowKey);
			get.addColumn(Bytes.toBytes("version"), Bytes.toBytes(version));
			Result result = hPage.get(get);
			// 判断是否已经crawl过
			if (result.isEmpty()) {
				Put put = new Put(rowKey);
				// 参数出分别：列族、列、值
				put.add(Bytes.toBytes("info"), Bytes.toBytes("response"),
						Bytes.toBytes(htmlContent));
				put.add(Bytes.toBytes("preference"), Bytes.toBytes("version"),
						Bytes.toBytes(version));
				hPage.put(put);
				int n = total.incrementAndGet() / 1000;
				String infoColumn = "urls" + n;
				hDomain = new HTable(hconf, "av_domain");
				Get hDomainGet = new Get(Bytes.toBytes(domainRoot));
				hDomainGet.addColumn(Bytes.toBytes("info"), Bytes.toBytes(infoColumn));

				Result res = hDomain.get(hDomainGet);
				List<String> urls = null;
				if (res != null && !res.isEmpty() && res.rawCells().length == 1) {
					Cell cell = res.rawCells()[0];
					byte[] buf = cell.getValueArray();
					if (buf != null && buf.length > 0) {
						urls = SerializableUtil.deserialize(buf, ArrayList.class);
						urls.add(crawlUrl);
						byte[] array = SerializableUtil.serializ(urls);
						Put hDomainPut = new Put(Bytes.toBytes(domainRoot));
						hDomainPut.add(Bytes.toBytes("info"), Bytes.toBytes(infoColumn), array);
					}
				} else {
					urls = new ArrayList<String>();
					urls.add(crawlUrl);
					byte[] array = SerializableUtil.serializ(urls);
					Put hDomainPut = new Put(Bytes.toBytes(domainRoot));
					hDomainPut.add(Bytes.toBytes("info"), Bytes.toBytes(infoColumn), array);
				}

			}
		} finally {
			if (hPage != null) {
				hPage.close();
			}
			if (hbaseAdmin != null) {
				hbaseAdmin.close();
			}
			if (hConnection != null) {
				hConnection.close();
			}
		}
	}

	public static void main(String[] args) {
		System.out.println(1 / 1000);

	}
}
