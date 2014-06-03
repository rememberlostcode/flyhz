
package com.flyhz.avengers.framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;

public abstract class AvengersExecutor implements Runnable {

	private final List<Event>			events	= new ArrayList<Event>();

	private final Map<String, Object>	context	= new HashMap<String, Object>();

	protected final Options				opts	= new Options();

	public AvengersExecutor() {

	}

	abstract Logger getLog();

	void initAvengersContext(String[] args) {
		opts.addOption("debug", false, "Dump out debug information");
		opts.addOption("help", false, "Print usage");
		Map<String, Object> customerContext = initArgs(args);
		if (customerContext != null && !customerContext.isEmpty()) {
			context.putAll(customerContext);
		}
	}

	abstract List<Event> initAvengersEvents(Map<String, Object> context);

	public void execute(String[] args) {
		initAvengersContext(args);
		initAvengersEvents(context);
		new Thread(this).start();
	}

	abstract Map<String, Object> initArgs(String[] args);

	@Override
	public void run() {
		for (Event event : events) {
			event.call(context);
		}
	}

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
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
