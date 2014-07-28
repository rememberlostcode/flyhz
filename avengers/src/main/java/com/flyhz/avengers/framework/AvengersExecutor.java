
package com.flyhz.avengers.framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;

import com.flyhz.avengers.framework.config.XConfiguration;
import com.flyhz.avengers.framework.lang.Event;
import com.flyhz.avengers.framework.util.StringUtil;

public abstract class AvengersExecutor {

	/**
	 * LONG
	 */
	public static final String	BATCH_ID	= "batchId";

	private List<Event>			events		= new ArrayList<Event>();

	private Map<String, Object>	context;

	protected final Options		opts		= new Options();

	protected AvengersExecutor() {
		context = new HashMap<String, Object>();
		context.putAll(XConfiguration.getAvengersContext());
	}

	protected AvengersExecutor(Map<String, Object> context) {
		this.context = context;
	}

	abstract Logger getLog();

	void initAvengersContext(String[] args) {
		StringBuffer sb = new StringBuffer();
		for (String arg : args) {
			sb.append(arg).append(" ");
		}
		getLog().info(this.getClass() + " main args = " + sb.toString());
		opts.addOption("batchId", true, "批号");
		Map<String, Object> customerContext = initArgs(args);
		CommandLine cliParser;
		try {
			cliParser = new GnuParser().parse(opts, args);
		} catch (ParseException e) {
			getLog().error(sb.toString(), e);
			throw new RuntimeException(sb.toString(), e);
		}
		String batchId = cliParser.getOptionValue("batchId");
		if (StringUtil.isBlank(batchId)) {
			System.exit(0);
		}
		context.put(BATCH_ID, Long.valueOf(batchId));
		getLog().info("batchId = " + batchId);
		if (customerContext != null && !customerContext.isEmpty()) {
			context.putAll(customerContext);
		}
	}

	abstract List<Event> initAvengersEvents();

	public void init(String[] args) {
		initAvengersContext(args);
		List<Event> events = initAvengersEvents();
		this.events.addAll(events);
	}

	public void execute() {
		for (Event event : events) {
			event.call();
		}
	}

	abstract Map<String, Object> initArgs(String[] args);

	/**
	 * Helper function to print usage
	 * 
	 * @param opts
	 *            Parsed command line options
	 */
	protected void printUsage(Options opts) {
		new HelpFormatter().printHelp("Crawl", opts);
	}

	/**
	 * Dump out contents of $CWD and the environment to stdout for debugging
	 */
	protected void dumpOutDebugInfo() {
		getLog().info("Dump debug output");
		Map<String, String> envs = System.getenv();
		for (Map.Entry<String, String> env : envs.entrySet()) {
			getLog().info("System env: key=" + env.getKey() + ", val=" + env.getValue());
		}

		String cmd = "ls -al";
		Runtime run = Runtime.getRuntime();
		Process pr = null;
		try {
			pr = run.exec(cmd);
			pr.waitFor();

			BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = "";
			while ((line = buf.readLine()) != null) {
				getLog().info("System CWD content: " + line);
			}
			buf.close();
		} catch (IOException e) {
			getLog().error("", e);
		} catch (InterruptedException e) {
			getLog().error("", e);
		}
	}

	public List<Event> getEvents() {
		return Collections.unmodifiableList(events);
	}

	public Map<String, Object> getContext() {
		return Collections.unmodifiableMap(context);
	}

	protected void put(String key, Object value) {
		this.context.put(key, value);
	}
}
