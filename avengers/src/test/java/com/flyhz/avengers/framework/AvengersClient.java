/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flyhz.avengers.framework;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.protocolrecords.GetNewApplicationResponse;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.YarnClusterMetrics;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.application.CrawlApplication;
import com.flyhz.avengers.framework.application.FetchApplication;
import com.flyhz.avengers.framework.application.InitEnvApplication;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class AvengersClient {

	private static final Logger	LOG				= LoggerFactory.getLogger(AvengersClient.class);

	// Configuration
	private Configuration		conf;
	private YarnClient			yarnClient;

	// App master priority
	private int					amPriority		= 0;
	// Queue for App master
	private String				amQueue			= "";
	// Amt. of memory resource to request for to run the App Master
	private int					amMemory		= 10;

	// log4j.properties file
	// if available, add to local resources and set into classpath
	private String				log4jPropFile	= "";

	// Start time for client
	private final long			clientStartTime	= System.currentTimeMillis();
	// Timeout threshold for client. Kill app after time interval expires.
	private long				clientTimeout	= 1200000;

	// Command line options
	private Options				opts;

	private Long				batchId;

	private String				hdfsPath;

	private List<List<String>>	avengersProcessCmds;

	private String				appJar;

	private boolean				copy			= true;

	/**
	 * @param args
	 *            Command line arguments
	 */
	public static void main(String[] args) {
		boolean result = false;
		try {
			AvengersClient client = new AvengersClient();
			LOG.info("Initializing Client");
			try {
				boolean doRun = client.init(args);
				if (!doRun) {
					System.exit(0);
				}
			} catch (IllegalArgumentException e) {
				System.err.println(e.getLocalizedMessage());
				client.printUsage();
				System.exit(-1);
			}
			for (List<String> cmds : client.avengersProcessCmds) {
				if (!client.run(cmds))
					break;
			}

		} catch (Throwable t) {
			LOG.error("Error running CLient", t);
			System.exit(1);
		}
		if (result) {
			LOG.info("Application completed successfully");
			System.exit(0);
		}
		LOG.error("Application failed to complete successfully");
		System.exit(2);
	}

	AvengersClient(Configuration conf) {

		batchId = System.currentTimeMillis();
		LOG.info("conf is {},batchId is {}", batchId, conf);
		this.conf = conf;
		System.setProperty("HADOOP_USER_NAME", "avengers");
		yarnClient = YarnClient.createYarnClient();
		conf.getSocketAddr(YarnConfiguration.RM_ADDRESS, YarnConfiguration.DEFAULT_RM_ADDRESS,
				YarnConfiguration.DEFAULT_RM_PORT);

		yarnClient.init(conf);

		opts = new Options();
		opts.addOption("jar", true, "Jar file containing the application master");

		Option all = new Option("all", false, "avengers all:include crawl fetch analyze");
		all.setArgs(0);

		Option crawl = new Option("crawl", true, "avengers crawl");
		crawl.setArgs(1);
		crawl.setArgName("batchId");

		Option fetch = new Option("fetch", true, "avengers fetch");
		fetch.setArgs(1);
		fetch.setArgName("batchId");

		Option analyze = new Option("analyze", true, "avengers analyze");
		analyze.setArgs(1);
		analyze.setArgName("batchId");

		opts.addOption(all);
		opts.addOption(crawl);
		opts.addOption(fetch);
		opts.addOption(analyze);

		avengersProcessCmds = new ArrayList<List<String>>();
		this.hdfsPath = "avengers/" + batchId + "/avengers.jar";
	}

	/**
   */
	public AvengersClient() throws Exception {
		this(new YarnConfiguration());
	}

	/**
	 * Helper function to print out usage
	 */
	private void printUsage() {
		new HelpFormatter().printHelp("Client", opts);
	}

	/**
	 * Parse command line options
	 * 
	 * @param args
	 *            Parsed command line options
	 * @return Whether the init was successful to run the client
	 * @throws ParseException
	 * @throws IOException
	 */
	public boolean init(String[] args) throws ParseException, IOException {

		LOG.info("env -> {}", conf.get("hadoop.tmp.dir"));

		CommandLine cliParser = new GnuParser().parse(opts, args);

		if (args.length == 0) {
			throw new IllegalArgumentException("No args specified for client to initialize");
		}

		amPriority = 0;
		amQueue = "default";
		amMemory = 10;

		if (!cliParser.hasOption("jar")) {
			throw new IllegalArgumentException("No jar file specified for application master");
		}

		appJar = cliParser.getOptionValue("jar");

		if (cliParser.hasOption("all")) {
			avengersProcessCmds.add(getInitEnvCmd());
			avengersProcessCmds.add(getCrawlCmd());
			avengersProcessCmds.add(getFetchCmd());
			avengersProcessCmds.add(runAnalyze());
		} else {
			if (cliParser.hasOption("crawl")) {
				avengersProcessCmds.add(getInitEnvCmd());
				avengersProcessCmds.add(getCrawlCmd());
			}
			if (cliParser.hasOption("fetch")) {
				avengersProcessCmds.add(getInitEnvCmd());
				avengersProcessCmds.add(getFetchCmd());
			}
			if (cliParser.hasOption("analyze")) {
				avengersProcessCmds.add(getInitEnvCmd());
				avengersProcessCmds.add(runAnalyze());
			}
		}

		return true;
	}

	private List<String> getInitEnvCmd() {
		// Set the necessary command to execute the application master
		Vector<CharSequence> vargs = new Vector<CharSequence>(30);
		// Set java executable command
		LOG.info("Setting up app master command");
		String appTempDir = conf.get("hadoop.tmp.dir");
		vargs.add("/bin/mkdir -p");
		vargs.add(appTempDir + "/" + batchId);
		vargs.add("&&");
		vargs.add(Environment.HADOOP_YARN_HOME.$() + "/bin/hadoop");
		vargs.add("fs");
		vargs.add("-copyToLocal");
		vargs.add(hdfsPath);
		vargs.add(appTempDir + "/" + batchId);
		vargs.add("&&");
		vargs.add(Environment.JAVA_HOME.$() + "/bin/java");
		vargs.add("-classpath");
		vargs.add("$CLASSPATH:" + appTempDir + "/" + batchId + "/avengers.jar");
		vargs.add("-Xmx" + amMemory + "m");
		vargs.add(InitEnvApplication.class.getName());
		vargs.add("-batchId");
		vargs.add(String.valueOf(this.batchId));
		vargs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/InitEnvApplication.stdout");
		vargs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/InitEnvApplication.stderr");

		// Get final commmand
		StringBuilder command = new StringBuilder();
		for (CharSequence str : vargs) {
			command.append(str).append(" ");
		}

		LOG.info("Completed setting up app client command " + command.toString());
		List<String> commands = new ArrayList<String>();
		commands.add(command.toString());
		return commands;

	}

	private List<String> getCrawlCmd() {
		// Set the necessary command to execute the application master
		Vector<CharSequence> vargs = new Vector<CharSequence>(30);
		// Set java executable command
		LOG.info("Setting up app crawl command");
		String appTempDir = conf.get("hadoop.tmp.dir");
		vargs.add(Environment.JAVA_HOME.$() + "/bin/java");
		vargs.add("-Xmx" + amMemory + "m");
		vargs.add("-classpath");
		vargs.add(Environment.CLASSPATH.$() + ":" + appTempDir + "/" + batchId + "/avengers.jar");
		vargs.add(CrawlApplication.class.getName());
		vargs.add("-batchId");
		vargs.add(String.valueOf(this.batchId));
		vargs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/CrawlApplication.stdout");
		vargs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/CrawlApplication.stderr");

		// Get final commmand
		StringBuilder command = new StringBuilder();
		for (CharSequence str : vargs) {
			command.append(str).append(" ");
		}

		LOG.info("Completed setting up app client command " + command.toString());
		List<String> commands = new ArrayList<String>();
		commands.add(command.toString());
		return commands;
	}

	private List<String> getFetchCmd() {
		// Set the necessary command to execute the application master
		Vector<CharSequence> vargs = new Vector<CharSequence>(30);
		// Set java executable command
		LOG.info("Setting up app fetch command");
		String appTempDir = conf.get("hadoop.tmp.dir");
		vargs.add(Environment.JAVA_HOME.$() + "/bin/java");
		vargs.add("-Xmx" + amMemory + "m");
		vargs.add("-classpath");
		vargs.add(Environment.CLASSPATH.$() + ":" + appTempDir + "/" + batchId + "/avengers.jar");
		vargs.add(FetchApplication.class.getName());
		vargs.add("-batchId");
		vargs.add(String.valueOf(this.batchId));
		vargs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/FetchApplication.stdout");
		vargs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/FetchApplication.stderr");

		// Get final commmand
		StringBuilder command = new StringBuilder();
		for (CharSequence str : vargs) {
			command.append(str).append(" ");
		}

		LOG.info("Completed setting up app client command " + command.toString());
		List<String> commands = new ArrayList<String>();
		commands.add(command.toString());
		return commands;
	}

	private List<String> runAnalyze() {
		// Set the necessary command to execute the application master
		Vector<CharSequence> vargs = new Vector<CharSequence>(30);
		// Set java executable command
		LOG.info("Setting up app analyze command");
		vargs.add(Environment.JAVA_HOME.$() + "/bin/java");
		vargs.add("-Xmx" + amMemory + "m");
		String appTempDir = conf.get("hadoop.tmp.dir");
		vargs.add("-classpath");
		vargs.add(Environment.CLASSPATH.$() + ":" + appTempDir + "/" + batchId + "/avengers.jar");
		vargs.add(Analyze.class.getName());
		vargs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/AnalyzeApplication.stdout");
		vargs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/AnalyzeApplication.stderr");

		// Get final commmand
		StringBuilder command = new StringBuilder();
		for (CharSequence str : vargs) {
			command.append(str).append(" ");
		}

		LOG.info("Completed setting up app client command " + command.toString());
		List<String> commands = new ArrayList<String>();
		commands.add(command.toString());
		return commands;
	}

	/**
	 * Main run function for the client
	 * 
	 * @return true if application completed successfully
	 * @throws IOException
	 * @throws YarnException
	 */
	private boolean run(List<String> commands) throws IOException, YarnException {

		LOG.info("Running Client");

		yarnClient.start();

		YarnClusterMetrics clusterMetrics = yarnClient.getYarnClusterMetrics();
		LOG.info("Got Cluster metric info from ASM" + ", numNodeManagers="
				+ clusterMetrics.getNumNodeManagers());

		List<NodeReport> clusterNodeReports = yarnClient.getNodeReports(NodeState.RUNNING);
		LOG.info("Got Cluster node info from ASM");
		for (NodeReport node : clusterNodeReports) {
			LOG.info("Got node report from ASM for" + ", nodeId=" + node.getNodeId()
					+ ", nodeAddress" + node.getHttpAddress() + ", nodeRackName"
					+ node.getRackName() + ", nodeNumContainers" + node.getNumContainers());
		}

		QueueInfo queueInfo = yarnClient.getQueueInfo(this.amQueue);
		LOG.info("Queue info" + ", queueName=" + queueInfo.getQueueName()
				+ ", queueCurrentCapacity=" + queueInfo.getCurrentCapacity()
				+ ", queueMaxCapacity=" + queueInfo.getMaximumCapacity()
				+ ", queueApplicationCount=" + queueInfo.getApplications().size()
				+ ", queueChildQueueCount=" + queueInfo.getChildQueues().size());

		List<QueueUserACLInfo> listAclInfo = yarnClient.getQueueAclsInfo();
		for (QueueUserACLInfo aclInfo : listAclInfo) {
			for (QueueACL userAcl : aclInfo.getUserAcls()) {
				LOG.info("User ACL Info for Queue" + ", queueName=" + aclInfo.getQueueName()
						+ ", userAcl=" + userAcl.name());
			}
		}

		// Get a new application id
		YarnClientApplication app = yarnClient.createApplication();
		GetNewApplicationResponse appResponse = app.getNewApplicationResponse();
		// if needed
		// If we do not have min/max, we may not be able to correctly request
		// the required resources from the RM for the app master
		// Memory ask has to be a multiple of min and less than max.
		// Dump out information about cluster capability as seen by the resource
		// manager
		int maxMem = appResponse.getMaximumResourceCapability().getMemory();
		LOG.info("Max mem capabililty of resources in this cluster " + maxMem);

		// A resource ask cannot exceed the max.
		if (amMemory > maxMem) {
			LOG.info("AM memory specified above max threshold of cluster. Using max value."
					+ ", specified=" + amMemory + ", max=" + maxMem);
			amMemory = maxMem;
		}

		// set the application name
		ApplicationSubmissionContext appContext = app.getApplicationSubmissionContext();
		ApplicationId appId = appContext.getApplicationId();
		appContext.setApplicationName("avengers");

		// Set up the container launch context for the application master
		ContainerLaunchContext amContainer = Records.newRecord(ContainerLaunchContext.class);

		// set local resources for the application master
		// local files or archives as needed
		// In this scenario, the jar file for the application master is part of
		// the local resources
		Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();

		FileSystem fs = DistributedFileSystem.get(conf);
		Path src = new Path(appJar);
		Path dst = new Path(fs.getHomeDirectory(), "avengers/" + batchId + "/avengers.jar");
		if (copy) {
			LOG.info("copy local jar to hdfs");
			fs.copyFromLocalFile(false, true, src, dst);
			copy = false;
		}
		this.hdfsPath = dst.toUri().toString();
		LOG.info("hdfs hdfsPath = {}", dst);
		FileStatus destStatus = fs.getFileStatus(dst);
		LocalResource amJarRsrc = Records.newRecord(LocalResource.class);

		amJarRsrc.setType(LocalResourceType.FILE);
		amJarRsrc.setVisibility(LocalResourceVisibility.APPLICATION);
		LOG.info("YarnURLFromPath ->{}", ConverterUtils.getYarnUrlFromPath(dst));
		amJarRsrc.setResource(ConverterUtils.getYarnUrlFromPath(dst));
		amJarRsrc.setTimestamp(destStatus.getModificationTime());
		amJarRsrc.setSize(destStatus.getLen());
		localResources.put("avengers.jar", amJarRsrc);

		// Set the log4j properties if needed
		if (!log4jPropFile.isEmpty()) {
			Path log4jSrc = new Path(log4jPropFile);
			Path log4jDst = new Path(fs.getHomeDirectory(), "log4j.props");
			fs.copyFromLocalFile(false, true, log4jSrc, log4jDst);
			FileStatus log4jFileStatus = fs.getFileStatus(log4jDst);
			LocalResource log4jRsrc = Records.newRecord(LocalResource.class);
			log4jRsrc.setType(LocalResourceType.FILE);
			log4jRsrc.setVisibility(LocalResourceVisibility.APPLICATION);
			log4jRsrc.setResource(ConverterUtils.getYarnUrlFromURI(log4jDst.toUri()));
			log4jRsrc.setTimestamp(log4jFileStatus.getModificationTime());
			log4jRsrc.setSize(log4jFileStatus.getLen());
			localResources.put("log4j.properties", log4jRsrc);
		}

		// The shell script has to be made available on the final container(s)
		// where it will be executed.
		// To do this, we need to first copy into the filesystem that is visible
		// to the yarn framework.
		// We do not need to set this as a local resource for the application
		// master as the application master does not need it.

		// Set local resource info into app master container launch context
		amContainer.setLocalResources(localResources);

		// Set the necessary security tokens as needed
		// amContainer.setContainerTokens(containerToken);

		// Set the env variables to be setup in the env where the application
		// master will be run
		LOG.info("Set the environment for the application master");
		Map<String, String> env = new HashMap<String, String>();
		StringBuilder classPathEnv = new StringBuilder(Environment.CLASSPATH.$()).append(File.pathSeparatorChar);
		for (String c : conf.getStrings(YarnConfiguration.YARN_APPLICATION_CLASSPATH,
				YarnConfiguration.DEFAULT_YARN_APPLICATION_CLASSPATH)) {
			classPathEnv.append(File.pathSeparatorChar);
			classPathEnv.append(c.trim());
		}
		classPathEnv.append(File.pathSeparatorChar).append("./log4j.properties");

		// add the runtime classpath needed for tests to work
		if (conf.getBoolean(YarnConfiguration.IS_MINI_YARN_CLUSTER, false)) {
			classPathEnv.append(':');
			classPathEnv.append(System.getProperty("java.class.path"));
		}
		LOG.info("CLASSPATH -> " + classPathEnv);
		env.put("CLASSPATH", classPathEnv.toString());

		amContainer.setEnvironment(env);

		for (String cmd : commands) {
			LOG.info("run command {},appId {}", cmd, appId.getId());
		}

		amContainer.setCommands(commands);

		// Set up resource type requirements
		// For now, only memory is supported so we set memory requirements
		Resource capability = Records.newRecord(Resource.class);
		capability.setMemory(amMemory);
		appContext.setResource(capability);

		// Service data is a binary blob that can be passed to the application
		// Not needed in this scenario
		// amContainer.setServiceData(serviceData);

		// Setup security tokens
		if (UserGroupInformation.isSecurityEnabled()) {
			Credentials credentials = new Credentials();
			String tokenRenewer = conf.get(YarnConfiguration.RM_PRINCIPAL);
			if (tokenRenewer == null || tokenRenewer.length() == 0) {
				throw new IOException(
						"Can't get Master Kerberos principal for the RM to use as renewer");
			}

			// For now, only getting tokens for the default file-system.
			final Token<?> tokens[] = fs.addDelegationTokens(tokenRenewer, credentials);
			if (tokens != null) {
				for (Token<?> token : tokens) {
					LOG.info("Got dt for " + fs.getUri() + "; " + token);
				}
			}
			DataOutputBuffer dob = new DataOutputBuffer();
			credentials.writeTokenStorageToStream(dob);
			ByteBuffer fsTokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());
			amContainer.setTokens(fsTokens);
		}

		appContext.setAMContainerSpec(amContainer);

		// Set the priority for the application master
		Priority pri = Records.newRecord(Priority.class);
		pri.setPriority(amPriority);
		appContext.setPriority(pri);

		// Set the queue to which this application is to be submitted in the RM
		appContext.setQueue(amQueue);

		// Submit the application to the applications manager
		// SubmitApplicationResponse submitResp =
		// applicationsManager.submitApplication(appRequest);
		// Ignore the response as either a valid response object is returned on
		// success
		// or an exception thrown to denote some form of a failure
		LOG.info("Submitting application to ASM");

		yarnClient.submitApplication(appContext);

		// Try submitting the same request again
		// app submission failure?

		// Monitor the application
		return monitorApplication(appId);

	}

	/**
	 * Monitor the submitted application for completion. Kill application if
	 * time expires.
	 * 
	 * @param appId
	 *            Application Id of application to be monitored
	 * @return true if application completed successfully
	 * @throws YarnException
	 * @throws IOException
	 */
	private boolean monitorApplication(ApplicationId appId) throws YarnException, IOException {

		while (true) {

			// Check app status every 1 second.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOG.debug("Thread sleep in monitoring loop interrupted");
			}

			// Get application report for the appId we are interested in
			ApplicationReport report = yarnClient.getApplicationReport(appId);

			LOG.info("Got application report from ASM for" + ", appId=" + appId.getId()
					+ ", clientToAMToken=" + report.getClientToAMToken() + ", appDiagnostics="
					+ report.getDiagnostics() + ", appMasterHost=" + report.getHost()
					+ ", appQueue=" + report.getQueue() + ", appMasterRpcPort="
					+ report.getRpcPort() + ", appStartTime=" + report.getStartTime()
					+ ", yarnAppState=" + report.getYarnApplicationState().toString()
					+ ", distributedFinalState=" + report.getFinalApplicationStatus().toString()
					+ ", appTrackingUrl=" + report.getTrackingUrl() + ", appUser="
					+ report.getUser());

			YarnApplicationState state = report.getYarnApplicationState();
			FinalApplicationStatus dsStatus = report.getFinalApplicationStatus();
			if (YarnApplicationState.FINISHED == state) {
				if (FinalApplicationStatus.SUCCEEDED == dsStatus) {
					LOG.info("Application has completed successfully. Breaking monitoring loop");
					return true;
				} else {
					LOG.info("Application did finished unsuccessfully." + " YarnState="
							+ state.toString() + ", DSFinalStatus=" + dsStatus.toString()
							+ ". Breaking monitoring loop");
					return false;
				}
			} else if (YarnApplicationState.KILLED == state || YarnApplicationState.FAILED == state) {
				LOG.info("Application did not finish." + " YarnState=" + state.toString()
						+ ", DSFinalStatus=" + dsStatus.toString() + ". Breaking monitoring loop");
				return false;
			}

			if (System.currentTimeMillis() > (clientStartTime + clientTimeout)) {
				LOG.info("Reached client specified timeout for application. Killing application");
				forceKillApplication(appId);
				return false;
			}
		}

	}

	/**
	 * Kill a submitted application by sending a call to the ASM
	 * 
	 * @param appId
	 *            Application Id to be killed.
	 * @throws YarnException
	 * @throws IOException
	 */
	private void forceKillApplication(ApplicationId appId) throws YarnException, IOException {
		// submitted and be running at
		// the same time.
		// If yes, can we kill a particular attempt only?

		// Response can be ignored as it is non-null on success or
		// throws an exception in case of failures
		yarnClient.killApplication(appId);
	}

}
