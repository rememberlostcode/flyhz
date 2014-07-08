
package com.flyhz.avengers.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Avengers {
	private static final Logger	LOG					= LoggerFactory.getLogger(Avengers.class);

	// XConfiguration
	private Configuration		conf;
	private final String		appMasterMainClass;
	private YarnClient			yarnClient;
	// Application master specific info to register a new Application with
	// RM/ASM
	private String				appName				= "";
	// App master priority
	private int					amPriority			= 0;
	// Queue for App master
	private String				amQueue				= "";
	// Amt. of memory resource to request for to run the App Master
	private int					amMemory			= 10;

	// Application master jar file
	private String				appMasterJar		= "";

	// Shell command to be executed
	private String				shellCommand		= "";
	// Location of shell script
	private String				shellScriptPath		= "";
	// Args to be passed to the shell command
	private String				shellArgs			= "";
	// Env variables to be setup for the shell command
	private Map<String, String>	shellEnv			= new HashMap<String, String>();
	// Shell Command Container priority
	private int					shellCmdPriority	= 0;

	// Amt of memory to request for container in which shell script will be
	// executed
	private int					containerMemory		= 10;
	// No. of containers in which the shell script needs to be executed
	private int					numContainers		= 1;

	// log4j.properties file
	// if available, add to local resources and set into classpath
	private String				log4jPropFile		= "";

	// Start time for client
	private final long			clientStartTime		= System.currentTimeMillis();
	// Timeout threshold for client. Kill app after time interval expires.
	private long				clientTimeout		= 600000;

	// Debug flag
	boolean						debugFlag			= false;
	private Options				opts;

	static {

	}

	static class Singletone {
		private static Avengers	singletone	= new Avengers();
	}

	private Avengers() {
		this(new YarnConfiguration());
	}

	private Avengers(Configuration conf) {
		this("com.flyhz.avengers.framework.AvengersAppMaster", conf);
	}

	private Avengers(String appMasterMainClass, Configuration conf) {
		LOG.info("conf is " + conf);
		this.conf = conf;
		this.appMasterMainClass = appMasterMainClass;
		yarnClient = YarnClient.createYarnClient();
		LOG.info("huoding test path > "
				+ conf.getClass().getClassLoader().getResource("yarn-site.xml").getPath());
		conf.getSocketAddr(YarnConfiguration.RM_ADDRESS, YarnConfiguration.DEFAULT_RM_ADDRESS,
				YarnConfiguration.DEFAULT_RM_PORT);
		yarnClient.init(conf);

		opts = new Options();
		opts.addOption("appname", true, "Application Name. Default value - DistributedShell");
		opts.addOption("priority", true, "Application Priority. Default 0");
		opts.addOption("queue", true, "RM Queue in which this application is to be submitted");
		opts.addOption("timeout", true, "Application timeout in milliseconds");
		opts.addOption("master_memory", true,
				"Amount of memory in MB to be requested to run the application master");
		opts.addOption("jar", true, "Jar file containing the application master");

		opts.addOption("shell_script", true, "Location of the shell script to be executed");
		opts.addOption("shell_args", true, "Command line args for the shell script");
		opts.addOption("shell_env", true,
				"Environment for shell script. Specified as env_key=env_val pairs");
		opts.addOption("shell_cmd_priority", true, "Priority for the shell command containers");
		opts.addOption("container_memory", true,
				"Amount of memory in MB to be requested to run the shell command");
		opts.addOption("num_containers", true,
				"No. of containers on which the shell command needs to be executed");
		opts.addOption("log_properties", true, "log4j.properties file");
		opts.addOption("all", false, "all:crawl、fetch、out");
		opts.addOption("crawl", false, "crawl");
		opts.addOption("fetch", false, "fetch");
		opts.addOption("out", false, "out");
		opts.addOption("debug", false, "Dump out debug information");
		opts.addOption("help", false, "Print usage");
	}

	public static Avengers getInstance() {
		return Singletone.singletone;
	}

	boolean init(String[] args) throws ParseException {
		CommandLine cliParser = new GnuParser().parse(opts, args);
		if (args.length == 0) {
			throw new IllegalArgumentException("No args specified for avengers to initialize");
		}

		if (cliParser.hasOption("debug")) {
			LOG.info("debug = {}", cliParser.hasOption("debug"));
			debugFlag = true;
		}

		appName = cliParser.getOptionValue("appname", "DistributedShell");
		LOG.info("appName = {}", appName);
		amPriority = Integer.parseInt(cliParser.getOptionValue("priority", "0"));
		amQueue = cliParser.getOptionValue("queue", "default");
		amMemory = Integer.parseInt(cliParser.getOptionValue("master_memory", "10"));

		if (amMemory < 0) {
			throw new IllegalArgumentException(
					"Invalid memory specified for application master, exiting."
							+ " Specified memory=" + amMemory);
		}

		if (!cliParser.hasOption("jar")) {
			throw new IllegalArgumentException("No jar file specified for application master");
		}

		appMasterJar = cliParser.getOptionValue("jar");
		if (cliParser.hasOption("all")) {
			shellCommand = "all";
		} else if (cliParser.hasOption("crawl")) {
			shellCommand = "crawl";
		} else if (cliParser.hasOption("fetch")) {
			shellCommand = "fetch";
		} else if (cliParser.hasOption("analyze")) {
			shellCommand = "analyze";
		}

		if (cliParser.hasOption("shell_script")) {
			shellScriptPath = cliParser.getOptionValue("shell_script");
		}
		if (cliParser.hasOption("shell_args")) {
			shellArgs = cliParser.getOptionValue("shell_args");
		}
		if (cliParser.hasOption("shell_env")) {
			String envs[] = cliParser.getOptionValues("shell_env");
			for (String env : envs) {
				env = env.trim();
				int index = env.indexOf('=');
				if (index == -1) {
					shellEnv.put(env, "");
					continue;
				}
				String key = env.substring(0, index);
				String val = "";
				if (index < (env.length() - 1)) {
					val = env.substring(index + 1);
				}
				shellEnv.put(key, val);
			}
		}
		shellCmdPriority = Integer.parseInt(cliParser.getOptionValue("shell_cmd_priority", "0"));

		containerMemory = Integer.parseInt(cliParser.getOptionValue("container_memory", "10"));
		numContainers = Integer.parseInt(cliParser.getOptionValue("num_containers", "1"));

		if (containerMemory < 0 || numContainers < 1) {
			throw new IllegalArgumentException(
					"Invalid no. of containers or container memory specified, exiting."
							+ " Specified containerMemory=" + containerMemory + ", numContainer="
							+ numContainers);
		}

		clientTimeout = Integer.parseInt(cliParser.getOptionValue("timeout", "600000"));

		log4jPropFile = cliParser.getOptionValue("log_properties", "");
		return true;
	}

	private void printUsage() {
		new HelpFormatter().printHelp("AvengersClient", opts);
	}

	public boolean run() throws YarnException, IOException {
		LOG.info("Running AvengersClient");
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
		// TODO get min/max resource capabilities from RM and change memory ask
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
		appContext.setApplicationName(appName);

		// Set up the container launch context for the application master
		ContainerLaunchContext amContainer = Records.newRecord(ContainerLaunchContext.class);

		// set local resources for the application master
		// local files or archives as needed
		// In this scenario, the jar file for the application master is part of
		// the local resources
		Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();

		LOG.info("Copy App Master jar from local filesystem and add to local environment");
		// Copy the application master jar to the filesystem
		// Create a local resource to point to the destination jar path
		LOG.info("fs.hdfs.impl class = ", conf.getClass("fs.hdfs.impl", null));
		ServiceLoader<FileSystem> serviceLoader = ServiceLoader.load(FileSystem.class);
		for (FileSystem fs : serviceLoader) {
			LOG.info("scheme:{},class{}", fs.getScheme(), fs.getClass());
		}
		FileSystem fs = FileSystem.get(conf);
		Path src = new Path(appMasterJar);
		String pathSuffix = appName + "/" + appId.getId() + "/avengers-bin.jar";

		Path dst = new Path(fs.getHomeDirectory(), pathSuffix);
		fs.copyFromLocalFile(false, true, src, dst);
		LOG.info("huoding test >" + dst.getName());
		FileStatus destStatus = fs.getFileStatus(dst);
		LocalResource amJarRsrc = Records.newRecord(LocalResource.class);

		// Set the type of resource - file or archive
		// archives are untarred at destination
		// we don't need the jar file to be untarred for now
		amJarRsrc.setType(LocalResourceType.FILE);
		// Set visibility of the resource
		// Setting to most private option
		amJarRsrc.setVisibility(LocalResourceVisibility.APPLICATION);
		// Set the resource to be copied over
		amJarRsrc.setResource(ConverterUtils.getYarnUrlFromPath(dst));
		// Set timestamp and length of file so that the framework
		// can do basic sanity checks for the local resource
		// after it has been copied over to ensure it is the same
		// resource the client intended to use with the application
		amJarRsrc.setTimestamp(destStatus.getModificationTime());
		amJarRsrc.setSize(destStatus.getLen());
		localResources.put("AvengersAppMaster.jar", amJarRsrc);

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
		String hdfsShellScriptLocation = "";
		long hdfsShellScriptLen = 0;
		long hdfsShellScriptTimestamp = 0;
		if (!shellScriptPath.isEmpty()) {
			Path shellSrc = new Path(shellScriptPath);
			String shellPathSuffix = appName + "/" + appId.getId() + "/ExecShellScript.sh";
			Path shellDst = new Path(fs.getHomeDirectory(), shellPathSuffix);
			fs.copyFromLocalFile(false, true, shellSrc, shellDst);
			hdfsShellScriptLocation = shellDst.toUri().toString();
			FileStatus shellFileStatus = fs.getFileStatus(shellDst);
			hdfsShellScriptLen = shellFileStatus.getLen();
			hdfsShellScriptTimestamp = shellFileStatus.getModificationTime();
		}

		// Set local resource info into app master container launch context
		amContainer.setLocalResources(localResources);

		// Set the necessary security tokens as needed
		// amContainer.setContainerTokens(containerToken);

		// Set the env variables to be setup in the env where the application
		// master will be run
		LOG.info("Set the environment for the application master");
		Map<String, String> env = new HashMap<String, String>();

		// put location of shell script into env
		// using the env info, the application master will create the correct
		// local resource for the
		// eventual containers that will be launched to execute the shell
		// scripts

		// Add AvengersAppMaster.jar location to classpath
		// At some point we should not be required to add
		// the hadoop specific classpaths to the env.
		// It should be provided out of the box.
		// For now setting all required classpaths including
		// the classpath to "." for the application jar
		StringBuilder classPathEnv = new StringBuilder(Environment.CLASSPATH.$()).append(
				File.pathSeparatorChar).append("./*");
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

		env.put("CLASSPATH", classPathEnv.toString());

		amContainer.setEnvironment(env);

		// Set the necessary command to execute the application master
		Vector<CharSequence> vargs = new Vector<CharSequence>(30);

		// Set java executable command
		LOG.info("Setting up app master command");
		vargs.add(Environment.JAVA_HOME.$() + "/bin/java");
		// Set Xmx based on am memory size
		vargs.add("-Xmx" + amMemory + "m");
		// Set class name
		vargs.add(appMasterMainClass);
		// Set params for Application Master
		vargs.add("--container_memory " + String.valueOf(containerMemory));
		vargs.add("--num_containers " + String.valueOf(numContainers));
		vargs.add("--priority " + String.valueOf(shellCmdPriority));
		if (!shellCommand.isEmpty()) {
			vargs.add("--shell_command " + shellCommand + "");
		}
		if (!shellArgs.isEmpty()) {
			vargs.add("--shell_args " + shellArgs + "");
		}
		for (Map.Entry<String, String> entry : shellEnv.entrySet()) {
			vargs.add("--shell_env " + entry.getKey() + "=" + entry.getValue());
		}
		if (debugFlag) {
			vargs.add("--debug");
		}

		vargs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/AvengersAppMaster.stdout");
		vargs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/AvengersAppMaster.stderr");

		// Get final commmand
		StringBuilder command = new StringBuilder();
		for (CharSequence str : vargs) {
			command.append(str).append(" ");
		}

		LOG.info("Completed setting up app master command " + command.toString());
		List<String> commands = new ArrayList<String>();
		commands.add(command.toString());
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
		// TODO - what is the range for priority? how to decide?
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

		// TODO
		// Try submitting the same request again
		// app submission failure?

		// Monitor the application
		return monitorApplication(appId);
	}

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
	 *            Application Key to be killed.
	 * @throws YarnException
	 * @throws IOException
	 */
	private void forceKillApplication(ApplicationId appId) throws YarnException, IOException {
		// TODO clarify whether multiple jobs with the same app id can be
		// submitted and be running at
		// the same time.
		// If yes, can we kill a particular attempt only?

		// Response can be ignored as it is non-null on success or
		// throws an exception in case of failures
		yarnClient.killApplication(appId);
	}

	public static void main(String[] args) {
		System.setProperty("hadoop.home.dir", "/Users/huoding/Downloads/hadoop-2.2.0");
		System.setProperty("yarn.resourcemanager.adress", "10.203.3.39");
		System.setProperty("HADOOP_USER_NAME", "avengers");
		LOG.info("HADOOP_CONF_DIR = " + System.getenv("HADOOP_CONF_DIR"));
		boolean result = false;
		try {
			Avengers client = Avengers.getInstance();
			LOG.info("Initializing AvengersClient");
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
			result = client.run();
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

		URL url = null;
		HttpURLConnection connection = null;
		try {
			url = new URL("http://science.nuaa.edu.cn/js-szdw.asp");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");

			connection.connect();

			Map<String, List<String>> map = connection.getHeaderFields();

			// 遍历所有的响应头字段

			for (String key : map.keySet()) {

				System.out.println(key + "--->" + map.get(key));

			}

			// 定义BufferedReader输入流来读取URL的响应
			BufferedReader in;
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(),
					Charset.forName("gb2312")));

			StringBuffer sb = new StringBuffer();
			String line;

			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			// System.out.println(sb.toString());
			// URLCrawlEvent parser = URLCrawlEvent.createParser(sb.toString(),
			// "gb2312");
			// NodeFilter filter = new TagNameFilter("table");
			// NodeList list = parser.parse(tableFilter);
			Document doc = Jsoup.parse(sb.toString());
			System.out.println(doc.html());
			Elements tables = doc.getElementsByTag("table");
			System.out.println(tables.size());
			for (Element table : tables) {
				// System.out.println(tr.child(0).text());
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

	}
}