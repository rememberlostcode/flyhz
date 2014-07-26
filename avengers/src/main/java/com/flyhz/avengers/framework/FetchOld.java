//
//package com.flyhz.avengers.framework;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.cli.CommandLine;
//import org.apache.commons.cli.GnuParser;
//import org.apache.commons.cli.ParseException;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.hbase.HBaseConfiguration;
//import org.apache.hadoop.hbase.client.HBaseAdmin;
//import org.apache.hadoop.hbase.client.HConnection;
//import org.apache.hadoop.hbase.client.HConnectionManager;
//import org.apache.hadoop.hbase.client.HTable;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.flyhz.avengers.framework.common.event.URLFetchEventOld;
//import com.flyhz.avengers.framework.config.XConfiguration;
//import com.flyhz.avengers.framework.lang.Event;
//import com.flyhz.avengers.framework.util.StringUtil;
//
//public class FetchOld extends AvengersExecutor {
//
//	private static final Logger	LOG				= LoggerFactory.getLogger(FetchOld.class);
//
//	public static final String	FETCH_URL		= "fetch.url";
//	/**
//	 * av_page.rowKey
//	 */
//	public static final String	FETCH_ID		= "fetch.id";
//	public static final String	FETCH_ENCODING	= "fetch.encoding";
//	public static final String	FETCH_ROOT		= "fetch.root";
//
//	HConnection					hConnection		= null;
//	HBaseAdmin					hbaseAdmin		= null;
//	HTable						hPage			= null;
//
//	public FetchOld() {
//		super();
//		LOG.info("FetchOld()");
//		Configuration hconf = HBaseConfiguration.create();
//		hconf.set("hbase.zookeeper.quorum", "m1,s1,s2");
//		hconf.set("hbase.zookeeper.property.clientPort", "2181");
//		try {
//			hConnection = HConnectionManager.createConnection(hconf);
//			hbaseAdmin = new HBaseAdmin(hConnection);
//			hPage = new HTable(hconf, "av_page");
//			hPage.setAutoFlush(false, true);
//		} catch (IOException e) {
//			LOG.error("FetchOld", e);
//			throw new RuntimeException("", e);
//		}
//	}
//
//	public FetchOld(Map<String, Object> context) {
//		super(context);
//	}
//
//	public static void main(String[] args) {
//		try {
//			LOG.info("fetch begin..............");
//			AvengersExecutor fetch = new FetchOld();
//			fetch.init(args);
//			fetch.execute();
//			LOG.info("fetch second..............");
//		} catch (Throwable th) {
//			LOG.error("", th);
//			System.exit(0);
//		}
//	}
//
//	@Override
//	Map<String, Object> initArgs(String[] args) {
//		opts.addOption("url", true, "av_page.i:url");
//		opts.addOption("id", true, "av_page.i:id");
//		opts.addOption("encoding", true, "av_page.i:e");
//		opts.addOption("root", true, "av_page.i:r");
//		CommandLine cliParser;
//
//		try {
//			cliParser = new GnuParser().parse(opts, args);
//		} catch (ParseException e) {
//			StringBuffer sb = new StringBuffer();
//			for (String arg : args) {
//				sb.append(arg).append(" ");
//			}
//			throw new RuntimeException(sb.toString(), e);
//		}
//		if (args.length == 0) {
//			printUsage(opts);
//			throw new IllegalArgumentException("No args specified for FetchOld to initialize");
//		}
//
//		String url = cliParser.getOptionValue("url");
//		if (StringUtil.isBlank(url)) {
//			System.exit(0);
//		}
//
//		String id = cliParser.getOptionValue("id");
//		if (StringUtil.isBlank(id)) {
//			System.exit(0);
//		}
//
//		String encoding = cliParser.getOptionValue("encoding");
//		if (StringUtil.isBlank(encoding)) {
//			System.exit(0);
//		}
//		String root = cliParser.getOptionValue("root");
//		if (StringUtil.isBlank(root)) {
//			System.exit(0);
//		}
//
//		Map<String, Object> context = new HashMap<String, Object>();
//		context.put(FETCH_URL, url);
//		context.put(FETCH_ID, Long.valueOf(id));
//		context.put(FETCH_ENCODING, encoding);
//		context.put(FETCH_ROOT, root);
//		return context;
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	List<Event> initAvengersEvents() {
//		Map<String, Object> context = getContext();
//		// 默认调用URLFetchEvent
//		List<Event> events = new ArrayList<Event>();
//		// 查询是否有自定义FetchEvent
//		String root = (String) context.get(FETCH_ROOT);
//
//		// 判断参数URL属于哪个domain
//
//		Map<String, Object> domain = (Map<String, Object>) context.get(root);
//		if (domain != null && domain.get(XConfiguration.FETCH_EVENTS) != null) {
//			List<Event> customEvents = (List<Event>) domain.get(XConfiguration.FETCH_EVENTS);
//			if (customEvents != null && !customEvents.isEmpty()) {
//				events.addAll(customEvents);
//			} else {
//				events.add(new URLFetchEventOld(context));
//			}
//		} else {
//			events.add(new URLFetchEventOld(context));
//		}
//
//		return events;
//	}
//
//	@Override
//	Logger getLog() {
//		return LOG;
//	}
// }
