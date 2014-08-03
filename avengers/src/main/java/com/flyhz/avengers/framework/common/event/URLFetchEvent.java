
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.util.Bytes;

import com.flyhz.avengers.framework.AvengersExecutor;
import com.flyhz.avengers.framework.common.dto.PageRowDataDto;
import com.flyhz.avengers.framework.lang.AVTable.AVColumn;
import com.flyhz.avengers.framework.lang.AVTable.AVFamily;
import com.flyhz.avengers.framework.util.URLXConnectionUtil;

public class URLFetchEvent extends AbstractPageEvent {
	@Override
	Set<AVColumn> getColumns() {
		Set<AVColumn> columns = new HashSet<AVColumn>();
		columns.add(AVColumn.url);
		columns.add(AVColumn.bid);
		columns.add(AVColumn.e);
		columns.add(AVColumn.r);
		return columns;
	}

	void processRowData(Result result, Map<String, Object> context, int index)
			throws RetriesExhaustedWithDetailsException, InterruptedIOException {
		log.info("{} processRowData begin..............", getClass());
		Long batchId = (Long) context.get(AvengersExecutor.BATCH_ID);
		byte[] family = Bytes.toBytes(AVFamily.i.name());
		byte[] columnUrl = Bytes.toBytes(AVColumn.url.name());
		byte[] columnBid = Bytes.toBytes(AVColumn.bid.name());
		byte[] columnE = Bytes.toBytes(AVColumn.e.name());
		byte[] columnR = Bytes.toBytes(AVColumn.r.name());
		if (batchId.equals(Bytes.toLong(result.getValue(family, columnBid)))) {
			String url = Bytes.toString(result.getValue(family, columnUrl));
			Long id = Bytes.toLong(result.getRow());
			String encoding = Bytes.toString(result.getValue(family, columnE));
			String root = Bytes.toString(result.getValue(family, columnR));

			log.info("fetch is start:url > {},id > {},batchId >{},encoding > {},root > {}", url,
					id, batchId, encoding, root);
			PageRowDataDto pageRowDataDto = new PageRowDataDto();
			pageRowDataDto.setId(id);
			pageRowDataDto.setBatchId(batchId);
			pageRowDataDto.setEncoding(encoding);
			pageRowDataDto.setRoot(root);
			pageRowDataDto.setUrl(url);
			HttpURLConnection connection = null;
			StringBuffer sb = null;

			try {
				connection = URLXConnectionUtil.getXHttpConnection(new URL(pageRowDataDto.getUrl()));
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
					return;
				} catch (InterruptedException ie) {
					return;
				}
			} catch (MalformedURLException e) {
				log.error("", e);
				return;
			} catch (IOException ioe) {
				log.error("", ioe);
				return;
			}
			Put put = new Put(Bytes.toBytes(pageRowDataDto.getId()));
			// 参数定义：列族、列、值
			put.add(Bytes.toBytes(AVFamily.i.name()), Bytes.toBytes(AVColumn.c.name()),
					Bytes.toBytes(sb.toString()));
			put.add(Bytes.toBytes(AVFamily.i.name()), Bytes.toBytes(AVColumn.bid.name()),
					Bytes.toBytes(pageRowDataDto.getBatchId()));
			log.info("insert into av_page rowkey > {}", pageRowDataDto.getId());
			hPage.put(put);
			if (index % 100 == 0) {
				hPage.flushCommits();
			}
			log.info("{} processRowData end..............", getClass());
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
