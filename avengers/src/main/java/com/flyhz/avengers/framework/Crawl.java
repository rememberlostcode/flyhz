
package com.flyhz.avengers.framework;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.config.AvengersConfiguration;
import com.flyhz.avengers.framework.config.xml.Domain;
import com.flyhz.avengers.framework.config.xml.Domain.CrawlEvents;
import com.flyhz.avengers.framework.config.xml.Domains;
import com.flyhz.avengers.framework.util.StringUtil;

public class Crawl extends AvengersExecutor {

	private static final Logger	LOG	= LoggerFactory.getLogger(Crawl.class);

	public static void main(String[] args) {
		try {
			AvengersExecutor crawl = new Crawl();
			crawl.execute(args);
		} catch (Throwable th) {
			LOG.error("", th);
			System.exit(0);
		}
	}

	public void initArgs(String[] args) {
		opts.addOption("url", true, "待爬网址");
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
		context.put("crawl.url", url);
	}

	@Override
	void initAvengersEvents() {
		Domains domains = AvengersConfiguration.getDomainsConfig();
		if (domains != null) {
			List<Domain> list = domains.getDomain();
			if (list != null && !list.isEmpty()) {
				for (Domain domain : list) {
					CrawlEvents crawlEvents = domain.getCrawlEvents();
					if (crawlEvents != null) {
						List<com.flyhz.avengers.framework.config.xml.Event> listEvent = crawlEvents.getEvent();
						if (listEvent != null && !listEvent.isEmpty()) {
							for (com.flyhz.avengers.framework.config.xml.Event event : listEvent) {

							}
						}

					}
				}
			}

		}
	}

	@Override
	void initAvengersContext() {

	}

	@Override
	Logger getLog() {
		return LOG;
	}
}
