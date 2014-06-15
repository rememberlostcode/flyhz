
package com.flyhz.avengers.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.common.event.URLCrawlEvent;
import com.flyhz.avengers.framework.config.XConfiguration;
import com.flyhz.avengers.framework.util.StringUtil;

public class Crawl extends AvengersExecutor {

	private static final Logger		LOG					= LoggerFactory.getLogger(Crawl.class);

	private final ExecutorService	es					= Executors.newFixedThreadPool(10);

	private static final String		CRAWL_URL			= "crawl.url";

	/**
	 * 
	 * 正则表达式过滤器，cral之前过滤，判断是否crawl
	 */
	private final Set<String>		beforeCrawlFilter	= new HashSet<String>();

	/**
	 * 正则表达式过滤器 cral之后过滤，判断是否入库
	 */
	private final Set<String>		afterCrawlFilter	= new HashSet<String>();

	public static void main(String[] args) {
		try {
			LOG.info("crawl begin..............");
			AvengersExecutor crawl = new Crawl();
			crawl.execute(args);
		} catch (Throwable th) {
			LOG.error("", th);
			System.exit(0);
		}
	}

	Map<String, Object> initArgs(String[] args) {
		opts.addOption("url", true, "crawl the url");
		opts.addOption("depth", true, "深度");
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
			throw new IllegalArgumentException("No args specified for Crawl to initialize");
		}

		if (cliParser.hasOption("help")) {
			printUsage(opts);
			System.exit(0);
		}

		if (cliParser.hasOption("debug")) {
			dumpOutDebugInfo();
			System.exit(0);
		}

		String url = cliParser.getOptionValue("url");
		if (StringUtil.isBlank(url)) {
			System.exit(0);
		}
		Map<String, Object> context = new HashMap<String, Object>();
		context.put(CRAWL_URL, url);
		return context;
	}

	@SuppressWarnings("unchecked")
	@Override
	List<Event> initAvengersEvents(Map<String, Object> context) {
		Map<String, Object> map = (Map<String, Object>) context.get(XConfiguration.AVENGERS_DOMAINS);
		List<Event> list = null;
		if (map.get(XConfiguration.CRAWL_EVENTS) != null) {
			list = (List<Event>) map.get(XConfiguration.CRAWL_EVENTS);
		} else {
			list = new ArrayList<Event>();
			list.add(new URLCrawlEvent());
		}
		return list;
	}

	@Override
	Logger getLog() {
		return LOG;
	}
}
