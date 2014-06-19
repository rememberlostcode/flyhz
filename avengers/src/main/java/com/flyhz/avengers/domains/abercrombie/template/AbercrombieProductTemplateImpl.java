
package com.flyhz.avengers.domains.abercrombie.template;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.Analyze;
import com.flyhz.avengers.framework.Template;

public class AbercrombieProductTemplateImpl implements Template {
	private static final Logger	LOG	= LoggerFactory.getLogger(AbercrombieProductTemplateImpl.class);

	@Override
	public void apply(Map<String, Object> context) {
		LOG.info("abercrombie template begin..............");
		String analyzeUrl = (String) context.get(Analyze.ANALYZE_URL);
		String htmlDoc = getAnalyzeHtml(analyzeUrl);
		Document doc = Jsoup.parse(htmlDoc);
		LOG.info(doc.html());
		// 连接HBase获取response数据
		LOG.info("abercrombie template end..............");
	}

	private String getAnalyzeHtml(String analyzeUrl) {
		LOG.info("getAnalyzeHtml begin..............");
		if (StringUtils.isNotBlank(analyzeUrl)) {
			Configuration hconf = HBaseConfiguration.create();
			hconf.set("hbase.zookeeper.quorum", "m1,s1,s2");
			hconf.set("hbase.zookeeper.property.clientPort", "2181");
			HConnection hConnection = null;
			try {
				hConnection = HConnectionManager.createConnection(hconf);
				HBaseAdmin hbaseAdmin = new HBaseAdmin(hConnection);
				if (hbaseAdmin.tableExists("av_page")) {
					HTable table = null;
					try {
						table = new HTable(hconf, "av_page");
						Get get = new Get(Bytes.toBytes(analyzeUrl));
						get.addFamily(Bytes.toBytes("info"));
						get.addColumn(Bytes.toBytes("info"), Bytes.toBytes("response"));
						Result result = table.get(get);
						for (Cell cell : result.rawCells()) {
							return new String(cell.getValueArray());
						}
					} catch (Exception e) {
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
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		LOG.info("getAnalyzeHtml end..............");
		return null;
	}
}
