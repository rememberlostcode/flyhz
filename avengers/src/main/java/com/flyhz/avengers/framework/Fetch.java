
package com.flyhz.avengers.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.common.event.TemplateApplyEvent;
import com.flyhz.avengers.framework.common.event.URLFetchEvent;
import com.flyhz.avengers.framework.config.XConfiguration;
import com.flyhz.avengers.framework.lang.Event;
import com.flyhz.avengers.framework.util.StringUtil;

public class Fetch extends AvengersExecutor {
	private static final Logger	LOG					= LoggerFactory.getLogger(Fetch.class);
	public static final String	FETCH_START_ROW_KEY	= "fetch.row.start";
	public static final String	FETCH_END_ROW_KEY	= "fetch.row.end";
	public static final String	DOMAIN_ROOT			= "domain.root";
	public static final String	FETCH_CONTENT		= "fetch.content";

	public Fetch() {
	}

	public static void main(String[] args) {
		try {
			LOG.info("fetchRange begin..............");
			AvengersExecutor fetch = new Fetch();
			fetch.init(args);
			fetch.execute();
			LOG.info("fetchRange end..............");
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
			throw new IllegalArgumentException("No args specified for FetchOld to initialize");
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
		context.put(FETCH_START_ROW_KEY, Long.valueOf(start));
		context.put(FETCH_END_ROW_KEY, Long.valueOf(end));
		Integer numOfThreads = (Integer) getContext().get(XConfiguration.NUM_FETCH_THREADS);
		LOG.info("Fetch start > {} end > {} numOfThreads > {}", start, end, numOfThreads);
		if (numOfThreads < 1) {
			throw new RuntimeException("numOfThreads must > 0");
		}
		return context;
	}

	@Override
	List<Event> initAvengersEvents() {
		Map<String, Object> context = getContext();
		// 默认调用URLFetchEvent
		List<Event> events = new ArrayList<Event>();
		events.add(new URLFetchEvent(context));
		events.add(new TemplateApplyEvent(context));
		return events;
	}

	@Override
	Logger getLog() {
		return LOG;
	}
}
