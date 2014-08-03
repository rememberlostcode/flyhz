
package com.flyhz.avengers.framework.common.event;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import com.flyhz.avengers.framework.Fetch;
import com.flyhz.avengers.framework.lang.AVTable;
import com.flyhz.avengers.framework.lang.AVTable.AVColumn;
import com.flyhz.avengers.framework.lang.AVTable.AVFamily;

public abstract class AbstractPageEvent extends AbstractEvent {

	HTable	hPage	= null;

	public AbstractPageEvent() {
		log.info("{} start", getClass());
		Configuration hconf = HBaseConfiguration.create();
		hconf.set("hbase.zookeeper.quorum", "m1,s1,s2");
		hconf.set("hbase.zookeeper.property.clientPort", "2181");
		hconf.setLong(HConstants.HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, 120000);
		try {
			hPage = new HTable(hconf, AVTable.av_page.name());
			hPage.setAutoFlush(false, true);
		} catch (IOException e) {
			log.error("RowRangeInitEvent", e);
		}
		log.info("{} end", getClass());
	}

	@Override
	public boolean call(Map<String, Object> context) {
		log.info("{}.call start", getClass());
		Long start = (Long) context.get(Fetch.FETCH_START_ROW_KEY);
		Long end = (Long) context.get(Fetch.FETCH_END_ROW_KEY);
		Scan scan = new Scan(Bytes.toBytes(start), Bytes.toBytes(end));
		byte[] family = Bytes.toBytes(AVFamily.i.name());
		Set<AVColumn> columns = getColumns();
		if (columns == null || !columns.isEmpty()) {
			for (AVColumn avColumn : columns) {
				scan.addColumn(family, Bytes.toBytes(avColumn.name()));
			}
		}
		try {
			ResultScanner rs = hPage.getScanner(scan);
			int i = 1;
			for (Result result : rs) {
				processRowData(result, context, i);
				i++;
			}
			if (i > 1) {
				hPage.flushCommits();
			}
		} catch (IOException e) {
			log.error("", e);
		} catch (Throwable e) {
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

	abstract void processRowData(Result result, Map<String, Object> context, int index)
			throws Throwable;

	abstract Set<AVColumn> getColumns();

}
