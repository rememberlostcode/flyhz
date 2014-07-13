
package com.flyhz.avengers.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.common.event.URLFetchInitEvent;
import com.flyhz.avengers.framework.config.XConfiguration;
import com.flyhz.avengers.framework.lang.AVTable;
import com.flyhz.avengers.framework.lang.Event;
import com.flyhz.avengers.framework.util.StringUtil;

public class FetchRange extends AvengersExecutor {
	private static final Logger	LOG					= LoggerFactory.getLogger(FetchRange.class);
	public static final String	FETCH_START_ROW_KEY	= "fetch.row.start";
	public static final String	FETCH_END_ROW_KEY	= "fetch.row.end";
	public static final String	DOMAIN_ROOT			= "domain.root";
	/**
	 * {@link BlockingQueue}
	 */
	public static final String	QUEUE				= "fetch.queue";

	HConnection					hConnection			= null;
	HBaseAdmin					hbaseAdmin			= null;
	HTable						hDomain				= null;
	HTable						hPage				= null;
	HTable						hCrawlRes			= null;
	HTable						hTempCrawlLog		= null;

	public FetchRange() {
		Configuration hconf = HBaseConfiguration.create();
		hconf.set("hbase.zookeeper.quorum", "m1,s1,s2");
		hconf.set("hbase.zookeeper.property.clientPort", "2181");
		try {
			hConnection = HConnectionManager.createConnection(hconf);
			hbaseAdmin = new HBaseAdmin(hConnection);
			hPage = new HTable(hconf, AVTable.av_page.name());
			hPage.setAutoFlush(false, true);
		} catch (IOException e) {
			LOG.error("URLCrawlEvent", e);
		}
	}

	public static void main(String[] args) {
		try {
			LOG.info("fetchRange begin..............");
			AvengersExecutor fetchRange = new FetchRange();
			fetchRange.init(args);
			fetchRange.execute();
			LOG.info("fetchRange second..............");
		} catch (Throwable th) {
			LOG.error("", th);
			System.exit(0);
		}
	}

	@Override
	Map<String, Object> initArgs(String[] args) {
		opts.addOption("start", true, "fetch the startKey");
		opts.addOption("end", true, "fetch the endKey");
		CommandLine cliParser;
		try {
			cliParser = new GnuParser().parse(opts, args);
		} catch (ParseException e) {
			StringBuffer sb = new StringBuffer();
			for (String arg : args) {
				sb.append(arg).append(" ");
			}
			throw new RuntimeException(sb.toString(), e);
		}
		if (args.length == 0) {
			printUsage(opts);
			throw new IllegalArgumentException("No args specified for Fetch to initialize");
		}

		String start = cliParser.getOptionValue("start");
		if (StringUtil.isBlank(start)) {
			System.exit(0);
		}
		String end = cliParser.getOptionValue("end");
		if (StringUtil.isBlank(end)) {
			System.exit(0);
		}

		Map<String, Object> context = new HashMap<String, Object>();
		context.put(FETCH_START_ROW_KEY, start);
		context.put(FETCH_END_ROW_KEY, end);
		Integer numOfThreads = (Integer) context.get(XConfiguration.NUM_FETCH_THREADS);
		if (numOfThreads < 1) {
			throw new RuntimeException("numOfThreads must > 0");
		}
		context.put(QUEUE, new ArrayBlockingQueue<String>(numOfThreads));
		return context;
	}

	@Override
	List<Event> initAvengersEvents() {
		Map<String, Object> context = getContext();
		// 默认调用URLFetchEvent
		List<Event> events = new ArrayList<Event>();
		events.add(new URLFetchInitEvent(context));
		return events;
	}

	@Override
	Logger getLog() {
		return LOG;
	}
}
