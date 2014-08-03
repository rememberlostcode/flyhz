
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
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
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
import com.flyhz.avengers.framework.config.XConfiguration;
import com.flyhz.avengers.framework.lang.AVTable;
import com.flyhz.avengers.framework.lang.AVTable.AVColumn;
import com.flyhz.avengers.framework.lang.AVTable.AVFamily;
import com.flyhz.avengers.framework.util.IdFactory;
import com.flyhz.avengers.framework.util.StringUtil;
import com.flyhz.avengers.framework.util.URLXConnectionUtil;

public class URLCrawlEvent extends AbstractEvent {

	private static final Logger	LOG		= LoggerFactory.getLogger(URLCrawlEvent.class);
	String						crawlUrl;
	private String				rootUrl	= "";
	HTable						hDomain	= null;
	HTable						hPage	= null;
	HTable						hFetch	= null;
	HTable						hCrawl	= null;
	List<String>				urlFilterBeforeCrawls;
	List<String>				urlFilterAfterCrawls;
	String						encode;
	Integer						depth;
	Long						batchId;
	Long						maxId	= 0L;

	public URLCrawlEvent() {
		LOG.info("URLCrawlEvent()");
		Configuration hconf = HBaseConfiguration.create();
		hconf.set("hbase.zookeeper.quorum", "m1,s1,s2");
		hconf.set("hbase.zookeeper.property.clientPort", "2181");
		try {
			hDomain = new HTable(hconf, AVTable.av_domain.name());
			hDomain.setAutoFlush(true, true);
			hPage = new HTable(hconf, AVTable.av_page.name());
			hPage.setAutoFlush(false, true);
			hFetch = new HTable(hconf, AVTable.av_fetch.name());
			hFetch.setAutoFlush(true, true);
			hCrawl = new HTable(hconf, AVTable.av_crawl.name());
			hCrawl.setAutoFlush(true, true);
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
				insertToFetch(crawlUrl);
			}
			recursiveMethod(crawlUrl, depth);
			LOG.info("put av_domain");
			Put avDomainPut = new Put(Bytes.toBytes(crawlUrl));
			avDomainPut.add(Bytes.toBytes(AVFamily.i.name()),
					Bytes.toBytes(AVColumn.bid.name()), Bytes.toBytes(batchId));
			avDomainPut.add(Bytes.toBytes(AVFamily.i.name()),
					Bytes.toBytes(AVColumn.maxid.name()), Bytes.toBytes(maxId));
			hDomain.put(avDomainPut);
			hPage.flushCommits();
		} catch (IOException e) {
			LOG.error("", e);
		} finally {
			if (hPage != null) {
				try {
					hPage.close();
				} catch (IOException e) {

				}
			}
			if (hFetch != null) {
				try {
					hFetch.close();
				} catch (IOException e) {

				}
			}
			if (hDomain != null) {
				try {
					hDomain.close();
				} catch (IOException e) {

				}
			}
			if (hCrawl != null) {
				try {
					hCrawl.close();
				} catch (IOException e) {
				}
			}
		}

		LOG.info("crawl {} is second", crawlUrl);
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
		if (insertToCrawl(crawlUrl)) {

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
									insertToFetch(href);
								}
								if (StringUtil.filterUrl(href, urlFilterBeforeCrawls)
										&& new_depth > 0) {
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
	}

	private void insertToPage(Long id, String crawlUrl) throws IOException {
		LOG.info("insertToPage first");
		byte[] rowKey = Bytes.toBytes(id);
		LOG.info("=====insert {} {} {} to av_page=====", id, crawlUrl, batchId);
		Put put = new Put(rowKey);
		put.add(Bytes.toBytes(AVFamily.i.name()), Bytes.toBytes(AVColumn.bid.name()),
				Bytes.toBytes(batchId));
		put.add(Bytes.toBytes(AVFamily.i.name()), Bytes.toBytes(AVColumn.url.name()),
				Bytes.toBytes(crawlUrl));
		put.add(Bytes.toBytes(AVFamily.i.name()), Bytes.toBytes(AVColumn.e.name()),
				Bytes.toBytes(encode));
		put.add(Bytes.toBytes(AVFamily.i.name()), Bytes.toBytes(AVColumn.r.name()),
				Bytes.toBytes(this.crawlUrl));
		hPage.put(put);
		if (id > maxId) {
			maxId = id;
		}
		LOG.info("insertToPage second");
	}

	private void insertToFetch(String crawlUrl) throws IOException {
		LOG.info("insertToFetch first");
		byte[] rowKey = Bytes.toBytes(crawlUrl);

		Get hFetchGet = new Get(rowKey);
		hFetchGet.addFamily(Bytes.toBytes(AVFamily.i.name()));
		Result res = hFetch.get(hFetchGet);
		Long id;
		if (res != null && !res.isEmpty()) {
			Long bid = Bytes.toLong(res.getValue(Bytes.toBytes(AVFamily.i.name()),
					Bytes.toBytes(AVColumn.bid.name())));
			// 判断是否已经采过，采了就不要再采了
			if (batchId.equals(bid)) {
				LOG.info("url {} is crawled,don't insert to hbase", crawlUrl);
				return;
			} else {
				id = Bytes.toLong(res.getValue(Bytes.toBytes(AVFamily.i.name()),
						Bytes.toBytes(AVColumn.id.name())));
			}
		} else {
			id = IdFactory.getInstance().nextId();
		}

		insertToPage(id, crawlUrl);

		LOG.info("=====insert {} {} to av_fetch=====", crawlUrl, batchId);
		Put put = new Put(rowKey);
		put.add(Bytes.toBytes(AVFamily.i.name()), Bytes.toBytes(AVColumn.bid.name()),
				Bytes.toBytes(batchId));
		put.add(Bytes.toBytes(AVFamily.i.name()), Bytes.toBytes(AVColumn.id.name()),
				Bytes.toBytes(id));
		hFetch.put(put);
		LOG.info("insertToFetch second");
	}

	private boolean insertToCrawl(String crawlUrl) throws IOException {
		LOG.info("insertToCrawl first");
		if (StringUtil.isBlank(crawlUrl))
			return false;
		LOG.info("=====insert {} {} to av_crawl=====", crawlUrl, batchId);
		byte[] rowKey = Bytes.toBytes(crawlUrl);
		Get get = new Get(rowKey);
		get.addColumn(Bytes.toBytes(AVFamily.i.name()),
				Bytes.toBytes(AVColumn.bid.name()));
		Result result = hCrawl.get(get);
		// 判断是否已经crawl过
		if (result != null && !result.isEmpty()) {
			Long v = Bytes.toLong(result.getValue(Bytes.toBytes(AVFamily.i.name()),
					Bytes.toBytes(AVColumn.bid.name())));
			if (v.equals(batchId)) {
				return false;
			}
		}
		Put put = new Put(rowKey);
		put.add(Bytes.toBytes(AVFamily.i.name()), Bytes.toBytes(AVColumn.bid.name()),
				Bytes.toBytes(batchId));
		hCrawl.put(put);
		LOG.info("insertToCrawl second");
		return true;
	}

	public static void main(String[] args) {
		System.out.println(1 / 1000);

	}
}
