
package com.flyhz.avengers.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.util.StringUtil;

public class Fetch extends AvengersExecutor {

	private static final Logger	LOG			= LoggerFactory.getLogger(Fetch.class);

	private static final String	FETCH_URL	= "fetch.url";

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
		context.put(FETCH_URL, url);
		return context;
	}

	@Override
	List<Event> initAvengersEvents(Map<String, Object> context) {

		return null;
	}

	@Override
	Logger getLog() {
		return null;
	}

}
