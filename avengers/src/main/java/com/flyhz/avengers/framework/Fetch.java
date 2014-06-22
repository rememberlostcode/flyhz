
package com.flyhz.avengers.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.common.event.URLFetchEvent;
import com.flyhz.avengers.framework.config.XConfiguration;
import com.flyhz.avengers.framework.util.StringUtil;

public class Fetch extends AvengersExecutor {
	private static final Logger	LOG			= LoggerFactory.getLogger(Fetch.class);
	public static final String	FETCH_URL	= "fetch.url";

	public static void main(String[] args) {
		try {
			LOG.info("fetch begin..............");
			AvengersExecutor fetch = new Fetch();
			fetch.execute(args);
			LOG.info("fetch end..............");
		} catch (Throwable th) {
			LOG.error("", th);
			System.exit(0);
		}
	}

	@Override
	Map<String, Object> initArgs(String[] args) {
		opts.addOption("url", true, "fetch the url");
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
		context.put(FETCH_URL, url);
		return context;
	}

	@SuppressWarnings("unchecked")
	@Override
	List<Event> initAvengersEvents(Map<String, Object> context) {
		// 默认调用URLFetchEvent
		List<Event> events = new ArrayList<Event>();
		events.add(new URLFetchEvent());
		// 查询是否有自定义FetchEvent
		String url = (String) context.get(FETCH_URL);
		if (StringUtils.isNotBlank(url)) {
			Map<String, Object> domains = (Map<String, Object>) context.get(XConfiguration.AVENGERS_DOMAINS);
			// 判断参数URL属于哪个domain
			if (domains != null && !domains.isEmpty()) {
				Set<String> domainRoots = domains.keySet();
				for (String domainRoot : domainRoots) {
					if (url.indexOf(domainRoot) > -1) {
						// 获取匹配domain的fetchEvents
						Map<String, Object> domain = (Map<String, Object>) domains.get(domainRoot);
						if (domain != null && domain.get(XConfiguration.FETCH_EVENTS) != null) {
							List<Event> customEvents = (List<Event>) domain.get(XConfiguration.FETCH_EVENTS);
							events.addAll(customEvents);
						}
						break;
					}
				}
			}
		}
		return events;
	}

	@Override
	Logger getLog() {
		return LOG;
	}
}
