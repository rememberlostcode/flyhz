
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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import com.flyhz.avengers.framework.AvengersExecutor;
import com.flyhz.avengers.framework.Fetch;
import com.flyhz.avengers.framework.lang.HBaseAVTable;
import com.flyhz.avengers.framework.lang.HBaseAVTable.HBaseAVColumn;
import com.flyhz.avengers.framework.lang.HBaseAVTable.HBaseAVFamily;
import com.flyhz.avengers.framework.util.URLXConnectionUtil;

public class URLFetchEvent extends AbstractEvent {
	HTable	hPage	= null;

	public URLFetchEvent(Map<String, Object> context) {
		super(context);
		log.info("URLFetchEvent start");
		Configuration hconf = HBaseConfiguration.create();
		hconf.set("hbase.zookeeper.quorum", "m1,s1,s2");
		hconf.set("hbase.zookeeper.property.clientPort", "2181");
		hconf.setLong(HConstants.HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, 120000);
		try {
			hPage = new HTable(hconf, HBaseAVTable.av_page.name());
			hPage.setAutoFlush(false, true);
		} catch (IOException e) {
			log.error("RowRangeInitEvent", e);
		}
		log.info("URLFetchEvent end");
	}

	@Override
	public boolean call() {
		log.info("{}.call start", getClass());
		Long batchId = (Long) context.get(AvengersExecutor.BATCH_ID);
		Long start = (Long) context.get(Fetch.FETCH_START_ROW_KEY);
		Long end = (Long) context.get(Fetch.FETCH_END_ROW_KEY);
		Scan scan = new Scan(Bytes.toBytes(start), Bytes.toBytes(end));
		// Scan scan = new Scan();
		byte[] family = Bytes.toBytes(HBaseAVFamily.i.name());
		byte[] columnUrl = Bytes.toBytes(HBaseAVColumn.url.name());
		byte[] columnBid = Bytes.toBytes(HBaseAVColumn.bid.name());
		byte[] columnE = Bytes.toBytes(HBaseAVColumn.e.name());
		byte[] columnR = Bytes.toBytes(HBaseAVColumn.r.name());
		scan.addColumn(family, columnUrl);
		scan.addColumn(family, columnBid);
		scan.addColumn(family, columnE);
		scan.addColumn(family, columnR);
		try {
			ResultScanner rs = hPage.getScanner(scan);
			int i = 1;
			for (Result result : rs) {
				if (batchId.equals(Bytes.toLong(result.getValue(family, columnBid)))) {
					String url = Bytes.toString(result.getValue(family, columnUrl));
					Long id = Bytes.toLong(result.getRow());
					String encoding = Bytes.toString(result.getValue(family, columnE));
					String root = Bytes.toString(result.getValue(family, columnR));

					log.info("fetch is start:url > {},id > {},batchId >{},encoding > {},root > {}",
							url, id, batchId, encoding, root);
					fetch(id, url, batchId, encoding, root);
					if (i++ % 100 == 0) {
						hPage.flushCommits();
					}
				}
			}
			if (i > 1) {
				hPage.flushCommits();
			}
		} catch (RetriesExhaustedWithDetailsException e) {
			log.error("", e);
		} catch (InterruptedIOException e) {
			log.error("", e);
		} catch (IOException e) {
			log.error("", e);
		} finally {
			if (hPage != null) {
				try {
					hPage.close();
				} catch (IOException e) {

				}
			}
		}
		log.info("{}.call end", getClass());
		return true;
	}

	protected void fetch(Long id, String fetchUrl, Long batchId, String encode, String root)
			throws RetriesExhaustedWithDetailsException, InterruptedIOException {
		log.info("fetch begin..............");

		HttpURLConnection connection = null;
		StringBuffer sb = null;
		try {
			URL url = new URL(fetchUrl);
			try {
				connection = URLXConnectionUtil.getXHttpConnection(url);
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Charset", encode);
				connection.connect();
				sb = readResponse(connection, encode);
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

			Put put = new Put(Bytes.toBytes(id));
			// 参数定义：列族、列、值
			put.add(Bytes.toBytes(HBaseAVFamily.i.name()), Bytes.toBytes(HBaseAVColumn.c.name()),
					Bytes.toBytes(sb.toString()));
			put.add(Bytes.toBytes(HBaseAVFamily.i.name()), Bytes.toBytes(HBaseAVColumn.bid.name()),
					Bytes.toBytes(batchId));
			log.info("insert into av_page rowkey > {}", id);
			hPage.put(put);

			log.info("fetch end..............");
		} catch (MalformedURLException e) {
			log.error("", e);
		}
	}

	protected void anylaze() {

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
