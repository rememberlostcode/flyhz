
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.domains.abercrombie.AbercrombieEncodeUtil;
import com.flyhz.avengers.framework.Event;
import com.flyhz.avengers.framework.Fetch;

public class URLFetchEvent implements Event {
	private static final Logger	LOG	= LoggerFactory.getLogger(URLFetchEvent.class);

	@Override
	public boolean call(Map<String, Object> context) {
		LOG.info("urlFetchEvent begin..............");
		// 获得参数URL并建立请求连接，返回response数据
		String fetchUrl = (String) context.get(Fetch.FETCH_URL);
		String encode = AbercrombieEncodeUtil.getFetchUrlEncode(context);
		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL(fetchUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			connection.setRequestProperty("Charset", encode);
			connection.connect();
			// 定义BufferedReader输入流来读取URL的响应
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(),
					Charset.forName(encode)));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			LOG.info(sb.toString());
			// 初始化HBase av_page表
			LOG.info("init hbase begin..............");
			Configuration hconf = HBaseConfiguration.create();
			hconf.set("hbase.zookeeper.quorum", "m1,s1,s2");
			hconf.set("hbase.zookeeper.property.clientPort", "2181");
			HConnection hConnection = HConnectionManager.createConnection(hconf);
			HBaseAdmin hbaseAdmin = new HBaseAdmin(hConnection);
			if (!hbaseAdmin.tableExists("av_page")) {
				HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf("av_page"));
				// 插入info列族
				HColumnDescriptor columnConfInfo = new HColumnDescriptor("info");
				tableDesc.addFamily(columnConfInfo);
				// 插入preference列族
				HColumnDescriptor columnConfPreference = new HColumnDescriptor("preference");
				tableDesc.addFamily(columnConfPreference);
				hbaseAdmin.createTable(tableDesc);
			}
			LOG.info("init hbase end..............");
			// 更新HBase数据库av_page的response字段
			HTable table = null;
			try {
				hbaseAdmin.flush("av_page");
				table = new HTable(hconf, "av_page");
				Put put = new Put(Bytes.toBytes(fetchUrl));
				// 参数定义：列族、列、值
				put.add(Bytes.toBytes("info"), Bytes.toBytes("response"),
						Bytes.toBytes(sb.toString()));
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
			LOG.info("urlFetchEvent end..............");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
