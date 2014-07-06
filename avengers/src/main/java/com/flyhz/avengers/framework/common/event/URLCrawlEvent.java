
package com.flyhz.avengers.framework.common.event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
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
import com.flyhz.avengers.framework.util.URLXConnectionUtil;

public class URLCrawlEvent implements Event {

	private static final Logger	LOG				= LoggerFactory.getLogger(URLCrawlEvent.class);
	String						crawlUrl;
	private String				rootUrl			= "";
	HConnection					hConnection		= null;
	HBaseAdmin					hbaseAdmin		= null;
	HTable						hPage			= null;
	HTable						hTempCrawlLog	= null;
	List<String>				urlFilterBeforeCrawls;
	List<String>				urlFilterAfterCrawls;
	String						encode;
	Integer						depth;
	Long						batchId;

	public URLCrawlEvent() {
		LOG.info("URLCrawlEvent()");
		Configuration hconf = HBaseConfiguration.create();
		hconf.set("hbase.zookeeper.quorum", "m1,s1,s2");
		hconf.set("hbase.zookeeper.property.clientPort", "2181");
		try {
			hConnection = HConnectionManager.createConnection(hconf);
			hbaseAdmin = new HBaseAdmin(hConnection);
			hPage = new HTable(hconf, "av_page");
			hPage.setAutoFlush(false, true);
			hTempCrawlLog = new HTable(hconf, "av_temp_crawl_log");
			hTempCrawlLog.setAutoFlush(true, true);
		} catch (IOException e) {
			LOG.error("URLCrawlEvent", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean call(Map<String, Object> context) {
		crawlUrl = (String) context.get(Crawl.CRAWL_URL);
		batchId = (Long) context.get(Crawl.BATCH_ID);
		if (StringUtils.isBlank(crawlUrl))
			return false;

		Map<String, Object> domain = (Map<String, Object>) context.get(crawlUrl);
		String tempUrl = "";
		String httpStr = "";
		if (crawlUrl.startsWith("http")) {
			httpStr = crawlUrl.substring(0, crawlUrl.indexOf("://") + 3);
			tempUrl = crawlUrl.substring(crawlUrl.indexOf("://") + 3);
		} else {
			httpStr = "http://";
			tempUrl = crawlUrl;
		}
		if (tempUrl.indexOf("/") > 0) {
			tempUrl = tempUrl.substring(0, tempUrl.indexOf("/"));
		}
		rootUrl = httpStr + tempUrl;
		encode = (String) domain.get(XConfiguration.ENCODING);
		depth = (Integer) domain.get(XConfiguration.CRAWL_DEPTH);

		this.urlFilterBeforeCrawls = (List<String>) domain.get(XConfiguration.URLFILTER_BEFORE_CRAWL);

		this.urlFilterAfterCrawls = (List<String>) domain.get(XConfiguration.URLFILTER_AFTER_CRAWL);

		try {
			if (StringUtil.filterUrl(crawlUrl, urlFilterAfterCrawls)) {
				insertInfoToHbase(crawlUrl);
			}
			recursiveMethod(crawlUrl, depth);
		} catch (IOException e) {
			LOG.error("", e);
		} finally {
			if (hPage != null) {
				try {
					hPage.close();
				} catch (IOException e) {

				}
			}
			if (hbaseAdmin != null) {
				try {
					hbaseAdmin.close();
				} catch (IOException e) {
				}
			}
			if (hConnection != null) {
				try {
					hConnection.close();
				} catch (IOException e) {

				}
			}
		}

		LOG.info("crawl {} is end", crawlUrl);
		return true;

	}

	private StringBuffer readResponse(URLConnection connection, String charset) throws IOException {
		// 定义BufferedReader输入流来读取URL的响应
		BufferedReader in;
		in = new BufferedReader(new InputStreamReader(connection.getInputStream(),
				Charset.forName(charset)));

		StringBuffer sb = new StringBuffer();
		String line;

		while ((line = in.readLine()) != null) {
			sb.append(line);
		}
		return sb;
	}

	/**
	 * 递归采集Url
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void recursiveMethod(String crawlUrl, Integer depth) throws IOException {
		LOG.info("========= url:{} depth:{} =========", crawlUrl, depth);
		Get get = new Get(Bytes.toBytes(crawlUrl));
		Result result = hTempCrawlLog.get(get);
		if (result != null && !result.isEmpty()) {
			return;
		}
		insertCrawlLogToHbase(crawlUrl);
		if (!crawlUrl.startsWith("http")) {
			crawlUrl = "http://" + crawlUrl;
		}
		URL url = new URL(crawlUrl);
		HttpURLConnection connection = null;
		StringBuffer sb = null;
		try {
			LOG.info("URL {} is crawling", crawlUrl);
			connection = URLXConnectionUtil.getXHttpConnection(url);
			try {
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Charset", encode);
				connection.connect();
				LOG.info("url {} is connected", crawlUrl);
				sb = readResponse(connection, encode);
				LOG.info("url {} responsed", crawlUrl);
			} catch (ConnectException e) {
				try {
					LOG.error("ConnectException", e);
					LOG.info("reconnect ...");
					Thread.sleep(2000);
					connection.connect();
					sb = readResponse(connection, encode);
				} catch (IOException ioe) {
					System.exit(0);
				} catch (InterruptedException ie) {
					System.exit(0);
				}
			} catch (IOException e) {
				LOG.error("crawl " + crawlUrl.toString(), e);
				System.exit(0);
			}
			if (sb != null) {
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
					Integer new_depth = depth - 1;
					for (int i = 0; i < size; i++) {
						String href = linksElements.get(i).attr("href");
						if (StringUtil.isNotBlank(href)) {
							if (href.startsWith("javascript:") || href.startsWith("#"))
								continue;

							if (!href.startsWith("http://")) {
								if (href.startsWith("/")) {
									href = rootUrl + href;
								} else {
									href = rootPath + "/" + href;
								}
							}
							if (StringUtil.filterUrl(href, urlFilterAfterCrawls)) {
								insertInfoToHbase(href);
							}
							if (StringUtil.filterUrl(href, urlFilterBeforeCrawls) && new_depth > 0) {
								recursiveMethod(href, new_depth);
							}

						}
					}
				}

			}
		} catch (MalformedURLException e) {
			LOG.error("", e);
			System.exit(0);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private void insertInfoToHbase(String crawlUrl) throws IOException {
		LOG.info("insertInfoToHbase start");
		// 判断是否已经采过，采了就不要再采了
		Get hPageGet = new Get(Bytes.toBytes(crawlUrl));
		Result res = hPage.get(hPageGet);
		if (res != null && !res.isEmpty()) {
			LOG.info("url {} is crawled,don't insert to hbase", crawlUrl);
			return;
		}
		if (StringUtil.isBlank(crawlUrl))
			return;
		LOG.info("=====insert {} to av_page=====", crawlUrl);
		byte[] rowKey = Bytes.toBytes(crawlUrl);
		Get get = new Get(rowKey);
		get.addColumn(Bytes.toBytes("preference"), Bytes.toBytes("batchId"));
		Result result = hPage.get(get);
		// 判断是否已经crawl过
		if (!result.isEmpty()) {
			Cell cell = result.rawCells()[0];
			Long v = Bytes.toLong(cell.getValueArray());
			if (v.equals(batchId)) {
				return;
			}
		}
		Put put = new Put(rowKey);
		put.add(Bytes.toBytes("preference"), Bytes.toBytes("batchId"), Bytes.toBytes(batchId));
		put.add(Bytes.toBytes("preference"), Bytes.toBytes("encode"), Bytes.toBytes(encode));
		hPage.put(put);
		LOG.info("insertInfoToHbase end");
	}

	private void insertCrawlLogToHbase(String crawlUrl) throws IOException {
		LOG.info("insertCrawlLogToHbase start");
		if (StringUtil.isBlank(crawlUrl))
			return;
		LOG.info("=====insert {} to av_temp_crawl_log=====", crawlUrl);
		byte[] rowKey = Bytes.toBytes(crawlUrl);
		Get get = new Get(rowKey);
		get.addColumn(Bytes.toBytes("preference"), Bytes.toBytes("batchId"));
		Result result = hPage.get(get);
		// 判断是否已经crawl过
		if (!result.isEmpty()) {
			Cell cell = result.rawCells()[0];
			Long v = Bytes.toLong(cell.getValueArray());
			if (v.equals(batchId)) {
				return;
			}
		}
		Put put = new Put(rowKey);
		put.add(Bytes.toBytes("preference"), Bytes.toBytes("batchId"), Bytes.toBytes(batchId));
		hTempCrawlLog.put(put);
		LOG.info("insertCrawlLogToHbase end");
	}

	public static void main(String[] args) {
		System.out.println(1 / 1000);

	}
}
