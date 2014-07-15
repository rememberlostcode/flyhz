
package com.flyhz.avengers.framework;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {

	static Logger	LOG	= LoggerFactory.getLogger(Test.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("http://www.coach.com/".hashCode());
		System.out.println("http://www.coach.com/".hashCode() & 0x00000000ffffffffL);
		System.out.println(Long.valueOf(System.currentTimeMillis()).hashCode());
		System.out.println("http://www.coachfactory.com/".hashCode());
		System.out.println("http://www.coach.com/online/handbags/Product-baby_bag_tote_in_saffiano_leather-10551-10051-26353-en?cs=svbk&catId=5000000000000015032&navCatId=7100000000000000598".hashCode());
		System.out.println("http://www.coach.com/online/handbags/Product-baby_bag_tote_in_saffiano_leather-10551-10051-26353-en?cs=svbk&catId=5000000000000015032&navCatId=7100000000000000599".hashCode());
		HConnection hConnection = null;
		HBaseAdmin hbaseAdmin = null;
		// HTable hVersion = null;
		HTable hPage = null;
		try {
			Configuration hbaseConf = HBaseConfiguration.create();
			hbaseConf.set("hbase.zookeeper.quorum", "m1,s1,s2");
			hbaseConf.set("hbase.zookeeper.property.clientPort", "2181");
			hConnection = HConnectionManager.createConnection(hbaseConf);
			hbaseAdmin = new HBaseAdmin(hConnection);

			Configuration configuration = HBaseConfiguration.create(hbaseConf);

			configuration.setLong("hbase.rpc.timeout", 600000);
			// 设置Scan缓存
			configuration.setLong("hbase.client.scanner.caching", 1000);

			hPage = new HTable(hbaseConf, "av_page");
			// Pair<byte[][], byte[][]> pairs = hPage.getRowOrBefore(row,
			// family).getStartEndKeys();

			for (byte[] startKey : hPage.getStartKeys()) {
				LOG.info("startKey > {}", Bytes.toString(startKey));
			}
			for (byte[] endKey : hPage.getEndKeys()) {
				LOG.info("endKey > {}", Bytes.toString(endKey));
			}

			Scan scan = new Scan(new byte[0], new byte[0]);
			// scan.addColumn(Bytes.toBytes("preference"),
			// Bytes.toBytes("batchId"));
			PageFilter pageFilter = new PageFilter(2);
			scan.setFilter(pageFilter);
			ResultScanner rs = hPage.getScanner(scan);

			// if (rs != null && rs.iterator().hasNext()) {
			Integer count = 0;
			for (Result result : rs) {
				String url = Bytes.toString(result.getRow());
				Long value = Bytes.toLong(result.getValue(Bytes.toBytes("preference"),
						Bytes.toBytes("batchId")));
				count++;
				LOG.info("rowkey -> {},batchId -> {},count -> {}", url, value, count);

			}
			//
			// }

		} catch (IOException e) {
			LOG.error("fetch", e);
		} catch (Throwable e) {
			LOG.error("fetch", e);
		} finally {
			if (hPage != null) {
				try {
					hPage.close();
				} catch (IOException e) {
					LOG.error("", e);
				}
			}
			if (hbaseAdmin != null) {
				try {
					hbaseAdmin.close();
				} catch (IOException e) {
					LOG.error("", e);
				}
			}
			if (hConnection != null) {
				try {
					hConnection.close();
				} catch (IOException e) {
					LOG.error("", e);
				}
			}
		}

	}
}
