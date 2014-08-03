
package com.flyhz.avengers.framework.common.event;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import com.flyhz.avengers.framework.AvengersExecutor;
import com.flyhz.avengers.framework.Fetch;
import com.flyhz.avengers.framework.common.dto.PageRowDataDto;
import com.flyhz.avengers.framework.lang.AVTable;
import com.flyhz.avengers.framework.lang.AVTable.AVColumn;
import com.flyhz.avengers.framework.lang.AVTable.AVFamily;

public abstract class PageEvent extends AbstractEvent {

	HTable	hPage	= null;

	public PageEvent() {
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
		Long batchId = (Long) context.get(AvengersExecutor.BATCH_ID);
		Long start = (Long) context.get(Fetch.FETCH_START_ROW_KEY);
		Long end = (Long) context.get(Fetch.FETCH_END_ROW_KEY);
		Scan scan = new Scan(Bytes.toBytes(start), Bytes.toBytes(end));
		// Scan scan = new Scan();
		byte[] family = Bytes.toBytes(AVFamily.i.name());
		byte[] columnUrl = Bytes.toBytes(AVColumn.url.name());
		byte[] columnBid = Bytes.toBytes(AVColumn.bid.name());
		byte[] columnE = Bytes.toBytes(AVColumn.e.name());
		byte[] columnR = Bytes.toBytes(AVColumn.r.name());
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
					PageRowDataDto pageRowDataDto = new PageRowDataDto();
					pageRowDataDto.setId(id);
					pageRowDataDto.setBatchId(batchId);
					pageRowDataDto.setEncoding(encoding);
					pageRowDataDto.setRoot(root);
					pageRowDataDto.setUrl(url);
					processRowData(pageRowDataDto, context);
					if (i++ % 100 == 0) {
						hPage.flushCommits();
					}
				}
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

	abstract void processRowData(PageRowDataDto pageRowDataDto, Map<String, Object> context) throws Throwable;

}
