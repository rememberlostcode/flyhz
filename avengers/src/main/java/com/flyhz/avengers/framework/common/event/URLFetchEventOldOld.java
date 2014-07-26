//
//package com.flyhz.avengers.framework.common.event;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.InterruptedIOException;
//import java.net.ConnectException;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLConnection;
//import java.nio.charset.Charset;
//import java.util.Map;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.hbase.HBaseConfiguration;
//import org.apache.hadoop.hbase.client.HBaseAdmin;
//import org.apache.hadoop.hbase.client.HConnection;
//import org.apache.hadoop.hbase.client.HConnectionManager;
//import org.apache.hadoop.hbase.client.HTable;
//import org.apache.hadoop.hbase.client.Put;
//import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
//import org.apache.hadoop.hbase.util.Bytes;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.flyhz.avengers.framework.FetchOld;
//import com.flyhz.avengers.framework.lang.AbstractEvent;
//import com.flyhz.avengers.framework.lang.HBaseAVTable.HBaseAVColumn;
//import com.flyhz.avengers.framework.lang.HBaseAVTable.HBaseAVFamily;
//import com.flyhz.avengers.framework.util.URLXConnectionUtil;
//
//public class URLFetchEventOldOld extends AbstractEvent {
//
//	private static final Logger	LOG			= LoggerFactory.getLogger(URLFetchEventOldOld.class);
//	HConnection					hConnection	= null;
//	HBaseAdmin					hbaseAdmin	= null;
//	HTable						hPage		= null;
//
//	public URLFetchEventOldOld(Map<String, Object> context) {
//		super(context);
//		LOG.info("URLFetchEventOld()");
//		Configuration hconf = HBaseConfiguration.create();
//		hconf.set("hbase.zookeeper.quorum", "m1,s1,s2");
//		hconf.set("hbase.zookeeper.property.clientPort", "2181");
//		try {
//			hConnection = HConnectionManager.createConnection(hconf);
//			hbaseAdmin = new HBaseAdmin(hConnection);
//			hPage = new HTable(hconf, "av_page");
//			hPage.setAutoFlush(false, true);
//		} catch (IOException e) {
//			LOG.error("URLCrawlEvent", e);
//		}
//	}
//
//	private StringBuffer readResponse(URLConnection connection, String charset) throws IOException {
//		// 定义BufferedReader输入流来读取URL的响应
//		BufferedReader in;
//		in = new BufferedReader(new InputStreamReader(connection.getInputStream(),
//				Charset.forName(charset)));
//
//		StringBuffer sb = new StringBuffer();
//		String line;
//
//		while ((line = in.readLine()) != null) {
//			sb.append(line);
//		}
//		return sb;
//	}
//
//	@Override
//	public boolean call() {
//		LOG.info("urlFetchEvent begin..............");
//		// 获得参数URL并建立请求连接，返回response数据
//		String fetchUrl = (String) context.get(FetchOld.FETCH_URL);
//		Long id = (Long) context.get(FetchOld.FETCH_ID);
//		Long batchId = (Long) context.get(FetchOld.BATCH_ID);
//		String encode = (String) context.get(FetchOld.FETCH_ENCODING);
//
//		HttpURLConnection connection = null;
//		StringBuffer sb = null;
//		try {
//			URL url = new URL(fetchUrl);
//			try {
//				connection = URLXConnectionUtil.getXHttpConnection(url);
//				connection.setRequestMethod("GET");
//				connection.setRequestProperty("Charset", encode);
//				connection.connect();
//				sb = readResponse(connection, encode);
//			} catch (ConnectException e) {
//				try {
//					LOG.error("ConnectException", e);
//					LOG.info("reconnect ...");
//					Thread.sleep(2000);
//					connection.connect();
//				} catch (IOException ioe) {
//					System.exit(0);
//				} catch (InterruptedException ie) {
//					System.exit(0);
//				}
//			} catch (IOException ioe) {
//				LOG.error("", ioe);
//			}
//			try {
//				Put put = new Put(Bytes.toBytes(id));
//				// 参数定义：列族、列、值
//				put.add(Bytes.toBytes(HBaseAVFamily.i.name()),
//						Bytes.toBytes(HBaseAVColumn.c.name()), Bytes.toBytes(sb.toString()));
//				put.add(Bytes.toBytes(HBaseAVFamily.i.name()),
//						Bytes.toBytes(HBaseAVColumn.bid.name()), Bytes.toBytes(batchId));
//				LOG.info("insert into av_page rowkey > {}", id);
//				hPage.put(put);
//			} catch (RetriesExhaustedWithDetailsException e) {
//				LOG.error("", e);
//			} catch (InterruptedIOException e) {
//				LOG.error("", e);
//				e.printStackTrace();
//			} finally {
//				if (hPage != null) {
//					try {
//						hPage.close();
//					} catch (IOException e) {
//
//					}
//				}
//				if (hbaseAdmin != null) {
//					try {
//						hbaseAdmin.close();
//					} catch (IOException e) {
//
//					}
//				}
//				if (hConnection != null) {
//					try {
//						hConnection.close();
//					} catch (IOException e) {
//					}
//				}
//			}
//			LOG.info("urlFetchEvent second..............");
//		} catch (MalformedURLException e) {
//			LOG.error("", e);
//		}
//
//		return true;
//	}
// }
