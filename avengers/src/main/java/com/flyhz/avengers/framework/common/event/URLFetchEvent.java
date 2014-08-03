
package com.flyhz.avengers.framework.common.event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.util.Bytes;

import com.flyhz.avengers.framework.common.dto.PageRowDataDto;
import com.flyhz.avengers.framework.lang.AVTable.AVColumn;
import com.flyhz.avengers.framework.lang.AVTable.AVFamily;
import com.flyhz.avengers.framework.util.URLXConnectionUtil;

public class URLFetchEvent extends PageEvent {

	void processRowData(PageRowDataDto pageRowDataDto, Map<String, Object> context)
			throws RetriesExhaustedWithDetailsException, InterruptedIOException {
		log.info("fetch begin..............");
		HttpURLConnection connection = null;
		StringBuffer sb = null;
		try {
			URL url = new URL(pageRowDataDto.getUrl());
			try {
				connection = URLXConnectionUtil.getXHttpConnection(url);
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Charset", pageRowDataDto.getEncoding());
				connection.connect();
				sb = readResponse(connection, pageRowDataDto.getEncoding());
			} catch (ConnectException e) {
				try {
					log.error("ConnectException", e);
					log.info("reconnect ...");
					Thread.sleep(2000);
					connection.connect();
				} catch (IOException ioe) {
					System.exit(0);
				} catch (InterruptedException ie) {
					System.exit(0);
				}
			} catch (IOException ioe) {
				log.error("", ioe);
			}

			Put put = new Put(Bytes.toBytes(pageRowDataDto.getId()));
			// 参数定义：列族、列、值
			put.add(Bytes.toBytes(AVFamily.i.name()), Bytes.toBytes(AVColumn.c.name()),
					Bytes.toBytes(sb.toString()));
			put.add(Bytes.toBytes(AVFamily.i.name()), Bytes.toBytes(AVColumn.bid.name()),
					Bytes.toBytes(pageRowDataDto.getBatchId()));
			log.info("insert into av_page rowkey > {}", pageRowDataDto.getId());
			hPage.put(put);

			log.info("fetch end..............");
		} catch (MalformedURLException e) {
			log.error("", e);
		}
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
}
