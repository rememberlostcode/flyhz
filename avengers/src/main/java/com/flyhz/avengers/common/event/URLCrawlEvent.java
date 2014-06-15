
package com.flyhz.avengers.common.event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

			LOG.info("init hbase");
			Configuration hconf = HBaseConfiguration.create();
			hconf.set("hbase.zookeeper.quorum", "m1,s1,s2");
			hconf.set("hbase.zookeeper.property.clientPort", "2181");
			HConnection hConnection = HConnectionManager.createConnection(hconf);
			HBaseAdmin hbaseAdmin = new HBaseAdmin(hConnection);
			if (!hbaseAdmin.tableExists("avengers_page")) {
				HTableDescriptor tableDesc = new HTableDescriptor(
						TableName.valueOf("avengers_page"));
				HColumnDescriptor columnConf = new HColumnDescriptor("page");
				tableDesc.addFamily(columnConf);
				hbaseAdmin.createTable(tableDesc);
			}

			HTable table = null;
			try {
				hbaseAdmin.flush("avengers_page");
				table = new HTable(hconf, "avengers_page");
				Put put = new Put(Bytes.toBytes("avengers"));
				// 参数出分别：列族、列、值
				put.add(Bytes.toBytes("page"), Bytes.toBytes("response"), Bytes.toBytes(doc.html()));
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
