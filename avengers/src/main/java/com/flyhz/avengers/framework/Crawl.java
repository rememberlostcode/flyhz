
package com.flyhz.avengers.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.common.event.URLCrawlEvent;
import com.flyhz.avengers.framework.config.XConfiguration;
import com.flyhz.avengers.framework.util.StringUtil;

public class Crawl extends AvengersExecutor {

	private static final Logger		LOG			= LoggerFactory.getLogger(Crawl.class);

	private final ExecutorService	es			= Executors.newFixedThreadPool(10);

	public static final String		CRAWL_URL	= "crawl.url";

	public static void main(String[] args) {
		try {
			LOG.info("crawl begin..............");
			LOG.info("crawl main CLASSPATH -> " + System.getenv("CLASSPATH"));
			AvengersExecutor crawl = new Crawl();
			crawl.execute(args);
		} catch (Throwable th) {
			LOG.error("", th);
		}
	}

	Map<String, Object> initArgs(String[] args) {
		opts.addOption("url", true, "crawl the url");
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

		String url = cliParser.getOptionValue("url");
		if (StringUtil.isBlank(url)) {
			throw new RuntimeException("crawl args url can't be null");
		}
		Map<String, Object> context = new HashMap<String, Object>();
		context.put(CRAWL_URL, url);
		return context;
	}

	@SuppressWarnings("unchecked")
	@Override
	List<Event> initAvengersEvents(Map<String, Object> context) {
		List<Event> list = null;
		if (context.get(XConfiguration.CRAWL_EVENTS) != null) {
			list = (List<Event>) context.get(XConfiguration.CRAWL_EVENTS);
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
