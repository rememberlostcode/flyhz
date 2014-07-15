
package com.flyhz.avengers.framework.common.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import com.flyhz.avengers.framework.AvengersExecutor;
import com.flyhz.avengers.framework.Fetch;
import com.flyhz.avengers.framework.FetchRange;
import com.flyhz.avengers.framework.lang.AVTable;
import com.flyhz.avengers.framework.lang.AVTable.AVColumn;
import com.flyhz.avengers.framework.lang.AVTable.AVColumnFamily;
import com.flyhz.avengers.framework.lang.AbstractEvent;

public class URLFetchInitEvent extends AbstractEvent {

	public static final String		START_ROW_KEY	= "rowkey.start";
	public static final String		END_ROW_KEY		= "rowkey.end";

	private BlockingQueue<String>	queue;
	HConnection						hConnection		= null;
	HBaseAdmin						hbaseAdmin		= null;
	HTable							hPage			= null;

	private List<Thread>			threads			= new ArrayList<Thread>();

	@SuppressWarnings("unchecked")
	public URLFetchInitEvent(Map<String, Object> context) {
		super(context);
		queue = (BlockingQueue<String>) context.get(FetchRange.QUEUE);
		try {
			for (int i = 0; i < queue.remainingCapacity(); i++) {
				AvengersExecutor executor = new Fetch(context);
				Thread thread = new Thread(executor);
				threads.add(thread);
				thread.start();
			}
		} catch (SecurityException e) {
			log.error("", e);
		} catch (IllegalArgumentException e) {
			log.error("", e);
		}
		Configuration hconf = HBaseConfiguration.create();
		hconf.set("hbase.zookeeper.quorum", "m1,s1,s2");
		hconf.set("hbase.zookeeper.property.clientPort", "2181");
		try {
			hConnection = HConnectionManager.createConnection(hconf);
			hbaseAdmin = new HBaseAdmin(hConnection);
			hPage = new HTable(hconf, AVTable.av_page.name());
			hPage.setAutoFlush(false, true);
		} catch (IOException e) {
			log.error("RowRangeInitEvent", e);
		}
	}

	@Override
	public boolean call() {
		Long batchId = (Long) context.get(AvengersExecutor.BATCH_ID);
		Long start = (Long) context.get(START_ROW_KEY);
		Long end = (Long) context.get(END_ROW_KEY);
		Scan scan = new Scan(Bytes.toBytes(start), Bytes.toBytes(end));
		byte[] family = Bytes.toBytes(AVColumnFamily.i.name());
		byte[] columnUrl = Bytes.toBytes(AVColumn.url.name());
		byte[] columnBid = Bytes.toBytes(AVColumn.bid.name());
		byte[] columnE = Bytes.toBytes(AVColumn.e.name());
		scan.addColumn(family, columnUrl);
		scan.addColumn(family, columnBid);
		scan.addColumn(family, columnE);
		try {
			ResultScanner rs = hPage.getScanner(scan);
			for (Result result : rs) {
				if (batchId.equals(Bytes.toLong(result.getValue(family, columnBid)))) {
					queue.put(Bytes.toString(result.getValue(family, columnUrl)));
				}
			}
		} catch (IOException e) {
			log.error("", e);
		} catch (InterruptedException e) {
			log.error("", e);
		}

		return false;
	}
}
