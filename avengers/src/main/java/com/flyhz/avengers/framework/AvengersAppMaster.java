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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocol;
import org.apache.hadoop.yarn.api.ContainerManagementProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.StartContainerRequest;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerExitStatus;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.client.api.async.NMClientAsync;
import org.apache.hadoop.yarn.client.api.async.impl.NMClientAsyncImpl;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;

import com.flyhz.avengers.framework.config.XConfiguration;
import com.google.common.annotations.VisibleForTesting;

/**
 * An AvengersAppMaster for executing shell commands on a set of launched
 * containers using the YARN framework.
 * 
 * <p>
 * This class is meant to act as an example on how to write yarn-based
 * application masters.
 * </p>
 * 
 * <p>
 * The AvengersAppMaster is started on a container by the
 * <code>ResourceManager</code>'s launcher. The first thing that the
 * <code>AvengersAppMaster</code> needs to do is to connect and register itself
 * with the <code>ResourceManager</code>. The registration sets up information
 * within the <code>ResourceManager</code> regarding what host:port the
 * AvengersAppMaster is listening on to provide any form of functionality to a
 * client as well as a tracking url that a client can use to keep track of
 * status/job history if needed. However, in the distributedshell, trackingurl
 * and appMasterHost:appMasterRpcPort are not supported.
 * </p>
 * 
 * <p>
 * The <code>AvengersAppMaster</code> needs to send a heartbeat to the
 * <code>ResourceManager</code> at regular intervals to inform the
 * <code>ResourceManager</code> that it is up and alive. The
 * {@link ApplicationMasterProtocol#allocate} to the
 * <code>ResourceManager</code> from the <code>AvengersAppMaster</code> acts as
 * a heartbeat.
 * 
 * <p>
 * For the actual handling of the job, the <code>AvengersAppMaster</code> has to
 * request the <code>ResourceManager</code> via {@link AllocateRequest} for the
 * required no. of containers using {@link ResourceRequest} with the necessary
 * resource specifications such as node location, computational
 * (memory/disk/cpu) resource requirements. The <code>ResourceManager</code>
 * responds with an {@link AllocateResponse} that informs the
 * <code>AvengersAppMaster</code> of the set of newly allocated containers,
 * completed containers as well as current state of available resources.
 * </p>
 * 
 * <p>
 * For each allocated container, the <code>AvengersAppMaster</code> can then set
 * up the necessary launch context via {@link ContainerLaunchContext} to specify
 * the allocated container id, local resources required by the executable, the
 * environment to be setup for the executable, commands to execute, etc. and
 * submit a {@link StartContainerRequest} to the
 * {@link ContainerManagementProtocol} to launch and execute the defined
 * commands on the given allocated container.
 * </p>
 * 
 * <p>
 * The <code>AvengersAppMaster</code> can monitor the launched container by
 * either querying the <code>ResourceManager</code> using
 * {@link ApplicationMasterProtocol#allocate} to get updates on completed
 * containers or via the {@link ContainerManagementProtocol} by querying for the
 * status of the allocated container's {@link ContainerId}.
 * 
 * <p>
 * After the job has been completed, the <code>AvengersAppMaster</code> has to
 * send a {@link FinishApplicationMasterRequest} to the
 * <code>ResourceManager</code> to inform it that the
 * <code>AvengersAppMaster</code> has been completed.
 */
@InterfaceAudience.Public
@InterfaceStability.Unstable
public class AvengersAppMaster {

	private static final Log		LOG							= LogFactory.getLog(AvengersAppMaster.class);

	// XConfiguration
	private Configuration			conf;

	// Handle to communicate with the Resource Manager
	@SuppressWarnings("rawtypes")
	private AMRMClientAsync			amRMClient;

	// Handle to communicate with the Node Manager
	private NMClientAsync			nmClientAsync;
	// Listen to process the response from the Node Manager
	private NMCallbackHandler		containerListener;

	// Application Attempt Key ( combination of attemptId and fail count )
	private ApplicationAttemptId	appAttemptID;

	// TODO
	// For status update for clients - yet to be implemented
	// Hostname of the container
	private String					appMasterHostname			= "";
	// Port on which the app master listens for status updates from clients
	private int						appMasterRpcPort			= -1;
	// Tracking url to which app master publishes info for clients to monitor
	private String					appMasterTrackingUrl		= "";

	// App Master configuration
	// No. of containers to run shell command on
	private int						numTotalContainers			= 1;
	// Memory to request for the container on which the shell command will run
	private int						containerMemory				= 10;
	// Priority of the request
	private int						requestPriority;

	// Counter for completed containers ( complete denotes successful or failed
	// )
	private AtomicInteger			numCompletedContainers		= new AtomicInteger();
	// Allocated container count so that we know how many containers has the RM
	// allocated to us
	private AtomicInteger			numAllocatedContainers		= new AtomicInteger();
	// Count of failed containers
	private AtomicInteger			numFailedContainers			= new AtomicInteger();
	// Count of containers already requested from the RM
	// Needed as once requested, we should not request for containers again.
	// Only request for more if the original requirement changes.
	private AtomicInteger			numRequestedContainers		= new AtomicInteger();

	// Shell command to be executed
	private String					shellCommand				= "";
	// Args to be passed to the shell command
	private String					shellArgs					= "";
	// Env variables to be setup for the shell command
	private Map<String, String>		shellEnv					= new HashMap<String, String>();

	// Location of shell script ( obtained from info set in env )
	// Shell script path in fs
	private String					shellScriptPath				= "";
	// Timestamp needed for creating a local resource
	private long					shellScriptPathTimestamp	= 0;
	// File length needed for local resource
	private long					shellScriptPathLen			= 0;

	// Hardcoded path to shell script in launch container's local env
	private final String			ExecShellStringPath			= "ExecShellScript.sh";

	private volatile boolean		done;
	private volatile boolean		success;

	private ByteBuffer				allTokens;

	// Launch threads
	private List<Thread>			launchThreads				= new ArrayList<Thread>();

	/**
	 * @param args
	 *            Command line args
	 */
	public static void main(String[] args) {
		boolean result = false;
		try {
			AvengersAppMaster avengersAppMaster = new AvengersAppMaster();
			LOG.info("Initializing AvengersAppMaster");
			boolean doRun = avengersAppMaster.init(args);
			if (!doRun) {
				System.exit(0);
			}
			result = avengersAppMaster.run();
		} catch (Throwable t) {
			LOG.fatal("Error running AvengersAppMaster", t);
			System.exit(1);
		}
		if (result) {
			LOG.info("Application Master completed successfully. exiting");
			System.exit(0);
		} else {
			LOG.info("Application Master failed. exiting");
			System.exit(2);
		}
	}

	/**
	 * Dump out contents of $CWD and the environment to stdout for debugging
	 */
	private void dumpOutDebugInfo() {

		LOG.info("Dump debug output");
		Map<String, String> envs = System.getenv();
		for (Map.Entry<String, String> env : envs.entrySet()) {
			LOG.info("System env: key=" + env.getKey() + ", val=" + env.getValue());
			System.out.println("System env: key=" + env.getKey() + ", val=" + env.getValue());
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
				LOG.info("System CWD content: " + line);
				System.out.println("System CWD content: " + line);
			}
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public AvengersAppMaster() {
		// Set up the configuration
		conf = new YarnConfiguration();
	}

	/**
	 * URLCrawlEvent command line options
	 * 
	 * @param args
	 *            Command line args
	 * @return Whether init successful and run should be invoked
	 * @throws ParseException
	 * @throws IOException
	 */
	public boolean init(String[] args) throws ParseException, IOException {

		Options opts = new Options();
		opts.addOption("all", false, "all:crawl、fetch、out");
		opts.addOption("crawl", false, "crawl");
		opts.addOption("fetch", false, "fetch");
		opts.addOption("out", false, "out");
		opts.addOption("debug", false, "Dump out debug information");
		opts.addOption("help", false, "Print usage");
		CommandLine cliParser = new GnuParser().parse(opts, args);

		if (args.length == 0) {
			printUsage(opts);
			throw new IllegalArgumentException(
					"No args specified for application master to initialize");
		}

		if (cliParser.hasOption("help")) {
			printUsage(opts);
			return false;
		}

		if (cliParser.hasOption("debug")) {
			dumpOutDebugInfo();
		}

		Map<String, String> envs = System.getenv();

		if (!envs.containsKey(Environment.CONTAINER_ID.name())) {
			if (cliParser.hasOption("app_attempt_id")) {
				String appIdStr = cliParser.getOptionValue("app_attempt_id", "");
				appAttemptID = ConverterUtils.toApplicationAttemptId(appIdStr);
			} else {
				throw new IllegalArgumentException(
						"Application Attempt Key not set in the environment");
			}
		} else {
			ContainerId containerId = ConverterUtils.toContainerId(envs.get(Environment.CONTAINER_ID.name()));
			appAttemptID = containerId.getApplicationAttemptId();
		}

		if (!envs.containsKey(ApplicationConstants.APP_SUBMIT_TIME_ENV)) {
			throw new RuntimeException(ApplicationConstants.APP_SUBMIT_TIME_ENV
					+ " not set in the environment");
		}
		if (!envs.containsKey(Environment.NM_HOST.name())) {
			throw new RuntimeException(Environment.NM_HOST.name() + " not set in the environment");
		}
		if (!envs.containsKey(Environment.NM_HTTP_PORT.name())) {
			throw new RuntimeException(Environment.NM_HTTP_PORT + " not set in the environment");
		}
		if (!envs.containsKey(Environment.NM_PORT.name())) {
			throw new RuntimeException(Environment.NM_PORT.name() + " not set in the environment");
		}

		LOG.info("Application master for app" + ", appId="
				+ appAttemptID.getApplicationId().getId() + ", clustertimestamp="
				+ appAttemptID.getApplicationId().getClusterTimestamp() + ", attemptId="
				+ appAttemptID.getAttemptId());

		if (!cliParser.hasOption("shell_command")) {
			throw new IllegalArgumentException(
					"No shell command specified to be executed by application master");
		}
		if (cliParser.hasOption("crawl")) {
			Map<String, Object> context = XConfiguration.getAvengersContext();
			@SuppressWarnings("unchecked")
			Map<String, Object> domainsMap = (Map<String, Object>) context.get(XConfiguration.AVENGERS_DOMAINS);
			numTotalContainers = domainsMap.size();
		} else if (cliParser.hasOption("fetch")) {
			LOG.info("AvengersAppMaster run fetch");
		} else if (cliParser.hasOption("out")) {
			LOG.info("AvengersAppMaster run out");
		}
		// shellCommand = cliParser.getOptionValue("shell_command");

		if (cliParser.hasOption("shell_env")) {
			String shellEnvs[] = cliParser.getOptionValues("shell_env");
			for (String env : shellEnvs) {
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

		if (envs.containsKey(DSConstants.DISTRIBUTEDSHELLSCRIPTLOCATION)) {
			shellScriptPath = envs.get(DSConstants.DISTRIBUTEDSHELLSCRIPTLOCATION);

			if (envs.containsKey(DSConstants.DISTRIBUTEDSHELLSCRIPTTIMESTAMP)) {
				shellScriptPathTimestamp = Long.valueOf(envs.get(DSConstants.DISTRIBUTEDSHELLSCRIPTTIMESTAMP));
			}
			if (envs.containsKey(DSConstants.DISTRIBUTEDSHELLSCRIPTLEN)) {
				shellScriptPathLen = Long.valueOf(envs.get(DSConstants.DISTRIBUTEDSHELLSCRIPTLEN));
			}

			if (!shellScriptPath.isEmpty()
					&& (shellScriptPathTimestamp <= 0 || shellScriptPathLen <= 0)) {
				LOG.error("Illegal values in env for shell script path" + ", path="
						+ shellScriptPath + ", len=" + shellScriptPathLen + ", timestamp="
						+ shellScriptPathTimestamp);
				throw new IllegalArgumentException("Illegal values in env for shell script path");
			}
		}

		containerMemory = Integer.parseInt(cliParser.getOptionValue("container_memory", "10"));
		if (numTotalContainers == 0) {
			throw new IllegalArgumentException("Cannot run distributed shell with no containers");
		}
		requestPriority = Integer.parseInt(cliParser.getOptionValue("priority", "0"));

		return true;
	}

	/**
	 * Helper function to print usage
	 * 
	 * @param opts
	 *            Parsed command line options
	 */
	private void printUsage(Options opts) {
		new HelpFormatter().printHelp("AvengersAppMaster", opts);
	}

	/**
	 * Main run function for the application master
	 * 
	 * @throws YarnException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked" })
	public boolean run() throws YarnException, IOException {
		LOG.info("Starting AvengersAppMaster");

		Credentials credentials = UserGroupInformation.getCurrentUser().getCredentials();
		DataOutputBuffer dob = new DataOutputBuffer();
		credentials.writeTokenStorageToStream(dob);
		// Now remove the AM->RM token so that containers cannot access it.
		Iterator<Token<?>> iter = credentials.getAllTokens().iterator();
		while (iter.hasNext()) {
			Token<?> token = iter.next();
			if (token.getKind().equals(AMRMTokenIdentifier.KIND_NAME)) {
				iter.remove();
			}
		}
		allTokens = ByteBuffer.wrap(dob.getData(), 0, dob.getLength());

		AMRMClientAsync.CallbackHandler allocListener = new RMCallbackHandler();
		amRMClient = AMRMClientAsync.createAMRMClientAsync(1000, allocListener);
		amRMClient.init(conf);
		amRMClient.start();

		containerListener = createNMCallbackHandler();
		nmClientAsync = new NMClientAsyncImpl(containerListener);
		nmClientAsync.init(conf);
		nmClientAsync.start();

		// Setup local RPC Server to accept status requests directly from
		// clients
		// TODO need to setup a protocol for client to be able to communicate to
		// the RPC server
		// TODO use the rpc port info to register with the RM for the client to
		// send requests to this app master

		// Register self with ResourceManager
		// This will start hearInitializing Clienttbeating to the RM
		appMasterHostname = NetUtils.getHostname();
		RegisterApplicationMasterResponse response = amRMClient.registerApplicationMaster(
				appMasterHostname, appMasterRpcPort, appMasterTrackingUrl);
		// Dump out information about cluster capability as seen by the
		// resource manager
		int maxMem = response.getMaximumResourceCapability().getMemory();
		LOG.info("Max mem capabililty of resources in this cluster " + maxMem);

		// A resource ask cannot exceed the max.
		if (containerMemory > maxMem) {
			LOG.info("Container memory specified above max threshold of cluster."
					+ " Using max value." + ", specified=" + containerMemory + ", max=" + maxMem);
			containerMemory = maxMem;
		}

		// Setup ask for containers from RM
		// Send request for containers to RM
		// Until we get our fully allocated quota, we keep on polling RM for
		// containers
		// Keep looping until all the containers are launched and shell script
		// executed on them ( regardless of success/failure).
		for (int i = 0; i < numTotalContainers; ++i) {
			ContainerRequest containerAsk = setupContainerAskForRM();
			amRMClient.addContainerRequest(containerAsk);
		}
		numRequestedContainers.set(numTotalContainers);

		while (!done && (numCompletedContainers.get() != numTotalContainers)) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException ex) {
			}
		}
		finish();

		return success;
	}

	@VisibleForTesting
	NMCallbackHandler createNMCallbackHandler() {
		return new NMCallbackHandler(this);
	}

	private void finish() {
		// Join all launched threads
		// needed for when we time out
		// and we need to release containers
		for (Thread launchThread : launchThreads) {
			try {
				launchThread.join(10000);
			} catch (InterruptedException e) {
				LOG.info("Exception thrown in thread join: " + e.getMessage());
				e.printStackTrace();
			}
		}

		// When the application completes, it should stop all running containers
		LOG.info("Application completed. Stopping running containers");
		nmClientAsync.stop();

		// When the application completes, it should send a finish application
		// signal to the RM
		LOG.info("Application completed. Signalling finish to RM");

		FinalApplicationStatus appStatus;
		String appMessage = null;
		success = true;
		if (numFailedContainers.get() == 0 && numCompletedContainers.get() == numTotalContainers) {
			appStatus = FinalApplicationStatus.SUCCEEDED;
		} else {
			appStatus = FinalApplicationStatus.FAILED;
			appMessage = "Diagnostics." + ", total=" + numTotalContainers + ", completed="
					+ numCompletedContainers.get() + ", allocated=" + numAllocatedContainers.get()
					+ ", failed=" + numFailedContainers.get();
			success = false;
		}
		try {
			amRMClient.unregisterApplicationMaster(appStatus, appMessage, null);
		} catch (YarnException ex) {
			LOG.error("Failed to unregister application", ex);
		} catch (IOException e) {
			LOG.error("Failed to unregister application", e);
		}

		amRMClient.stop();
	}

	private class RMCallbackHandler implements AMRMClientAsync.CallbackHandler {
		@SuppressWarnings("unchecked")
		@Override
		public void onContainersCompleted(List<ContainerStatus> completedContainers) {
			LOG.info("Got response from RM for container ask, completedCnt="
					+ completedContainers.size());
			for (ContainerStatus containerStatus : completedContainers) {
				LOG.info("Got container status for containerID=" + containerStatus.getContainerId()
						+ ", state=" + containerStatus.getState() + ", exitStatus="
						+ containerStatus.getExitStatus() + ", diagnostics="
						+ containerStatus.getDiagnostics());

				// non complete containers should not be here
				assert (containerStatus.getState() == ContainerState.COMPLETE);

				// increment counters for completed/failed containers
				int exitStatus = containerStatus.getExitStatus();
				if (0 != exitStatus) {
					// container failed
					if (ContainerExitStatus.ABORTED != exitStatus) {
						// shell script failed
						// counts as completed
						numCompletedContainers.incrementAndGet();
						numFailedContainers.incrementAndGet();
					} else {
						// container was killed by framework, possibly preempted
						// we should re-try as the container was lost for some
						// reason
						numAllocatedContainers.decrementAndGet();
						numRequestedContainers.decrementAndGet();
						// we do not need to release the container as it would
						// be done
						// by the RM
					}
				} else {
					// nothing to do
					// container completed successfully
					numCompletedContainers.incrementAndGet();
					LOG.info("Container completed successfully." + ", containerId="
							+ containerStatus.getContainerId());
				}
			}

			// ask for more containers if any failed
			int askCount = numTotalContainers - numRequestedContainers.get();
			numRequestedContainers.addAndGet(askCount);

			if (askCount > 0) {
				for (int i = 0; i < askCount; ++i) {
					ContainerRequest containerAsk = setupContainerAskForRM();
					amRMClient.addContainerRequest(containerAsk);
				}
			}

			if (numCompletedContainers.get() == numTotalContainers) {
				done = true;
			}
		}

		@Override
		public void onContainersAllocated(List<Container> allocatedContainers) {
			LOG.info("Got response from RM for container ask, allocatedCnt="
					+ allocatedContainers.size());
			numAllocatedContainers.addAndGet(allocatedContainers.size());
			for (Container allocatedContainer : allocatedContainers) {
				LOG.info("Launching shell command on a new container." + ", containerId="
						+ allocatedContainer.getId() + ", containerNode="
						+ allocatedContainer.getNodeId().getHost() + ":"
						+ allocatedContainer.getNodeId().getPort() + ", containerNodeURI="
						+ allocatedContainer.getNodeHttpAddress() + ", containerResourceMemory"
						+ allocatedContainer.getResource().getMemory());
				// + ", containerToken"
				// +allocatedContainer.getContainerToken().getIdentifier().toString());

				LaunchContainerRunnable runnableLaunchContainer = new LaunchContainerRunnable(
						allocatedContainer, containerListener);
				Thread launchThread = new Thread(runnableLaunchContainer);

				// launch and start the container on a separate thread to keep
				// the main thread unblocked
				// as all containers may not be allocated at one go.
				launchThreads.add(launchThread);
				launchThread.start();
			}
		}

		@Override
		public void onShutdownRequest() {
			done = true;
		}

		@Override
		public void onNodesUpdated(List<NodeReport> updatedNodes) {
		}

		@Override
		public float getProgress() {
			// set progress to deliver to RM on next heartbeat
			float progress = (float) numCompletedContainers.get() / numTotalContainers;
			return progress;
		}

		@Override
		public void onError(Throwable e) {
			done = true;
			amRMClient.stop();
		}
	}

	@VisibleForTesting
	static class NMCallbackHandler implements NMClientAsync.CallbackHandler {

		private ConcurrentMap<ContainerId, Container>	containers	= new ConcurrentHashMap<ContainerId, Container>();
		private final AvengersAppMaster					avengersAppMaster;

		public NMCallbackHandler(AvengersAppMaster avengersAppMaster) {
			this.avengersAppMaster = avengersAppMaster;
		}

		public void addContainer(ContainerId containerId, Container container) {
			containers.putIfAbsent(containerId, container);
		}

		@Override
		public void onContainerStopped(ContainerId containerId) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Succeeded to stop Container " + containerId);
			}
			containers.remove(containerId);
		}

		@Override
		public void onContainerStatusReceived(ContainerId containerId,
				ContainerStatus containerStatus) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Container Status: id=" + containerId + ", status=" + containerStatus);
			}
		}

		@Override
		public void onContainerStarted(ContainerId containerId,
				Map<String, ByteBuffer> allServiceResponse) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Succeeded to start Container " + containerId);
			}
			Container container = containers.get(containerId);
			if (container != null) {
				avengersAppMaster.nmClientAsync.getContainerStatusAsync(containerId,
						container.getNodeId());
			}
		}

		@Override
		public void onStartContainerError(ContainerId containerId, Throwable t) {
			LOG.error("Failed to start Container " + containerId);
			containers.remove(containerId);
			avengersAppMaster.numCompletedContainers.incrementAndGet();
			avengersAppMaster.numFailedContainers.incrementAndGet();
		}

		@Override
		public void onGetContainerStatusError(ContainerId containerId, Throwable t) {
			LOG.error("Failed to query the status of Container " + containerId);
		}

		@Override
		public void onStopContainerError(ContainerId containerId, Throwable t) {
			LOG.error("Failed to stop Container " + containerId);
			containers.remove(containerId);
		}
	}

	/**
	 * Thread to connect to the {@link ContainerManagementProtocol} and launch
	 * the container that will execute the shell command.
	 */
	private class LaunchContainerRunnable implements Runnable {

		// Allocated container
		Container			container;

		NMCallbackHandler	containerListener;

		/**
		 * @param lcontainer
		 *            Allocated container
		 * @param containerListener
		 *            Callback handler of the container
		 */
		public LaunchContainerRunnable(Container lcontainer, NMCallbackHandler containerListener) {
			this.container = lcontainer;
			this.containerListener = containerListener;
		}

		@Override
		/**
		 * Connects to CM, sets up container launch context 
		 * for shell command and eventually dispatches the container 
		 * start request to the CM. 
		 */
		public void run() {
			LOG.info("Setting up container launch container for containerid=" + container.getId());
			ContainerLaunchContext ctx = Records.newRecord(ContainerLaunchContext.class);

			// Set the environment
			ctx.setEnvironment(shellEnv);

			// Set the local resources
			Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();

			// The container for the eventual shell commands needs its own local
			// resources too.
			// In this scenario, if a shell script is specified, we need to have
			// it
			// copied and made available to the container.
			if (!shellScriptPath.isEmpty()) {
				LocalResource shellRsrc = Records.newRecord(LocalResource.class);
				shellRsrc.setType(LocalResourceType.FILE);
				shellRsrc.setVisibility(LocalResourceVisibility.APPLICATION);
				try {
					shellRsrc.setResource(ConverterUtils.getYarnUrlFromURI(new URI(shellScriptPath)));
				} catch (URISyntaxException e) {
					LOG.error("Error when trying to use shell script path specified"
							+ " in env, path=" + shellScriptPath);
					e.printStackTrace();

					// A failure scenario on bad input such as invalid shell
					// script path
					// We know we cannot continue launching the container
					// so we should release it.
					// TODO
					numCompletedContainers.incrementAndGet();
					numFailedContainers.incrementAndGet();
					return;
				}
				shellRsrc.setTimestamp(shellScriptPathTimestamp);
				shellRsrc.setSize(shellScriptPathLen);
				localResources.put(ExecShellStringPath, shellRsrc);
			}
			ctx.setLocalResources(localResources);

			// Set the necessary command to execute on the allocated container
			Vector<CharSequence> vargs = new Vector<CharSequence>(5);

			// Set executable command
			vargs.add(shellCommand);
			// Set shell script path
			if (!shellScriptPath.isEmpty()) {
				vargs.add(ExecShellStringPath);
			}

			// Set args for the shell command if any
			vargs.add(shellArgs);
			// Add log redirect params
			vargs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout");
			vargs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr");

			// Get final commmand
			StringBuilder command = new StringBuilder();
			for (CharSequence str : vargs) {
				command.append(str).append(" ");
			}

			List<String> commands = new ArrayList<String>();
			commands.add(command.toString());
			ctx.setCommands(commands);

			// Set up tokens for the container too. Today, for normal shell
			// commands,
			// the container in distribute-shell doesn't need any tokens. We are
			// populating them mainly for NodeManagers to be able to download
			// any
			// files in the distributed file-system. The tokens are otherwise
			// also
			// useful in cases, for e.g., when one is running a "hadoop dfs"
			// command
			// inside the distributed shell.
			ctx.setTokens(allTokens.duplicate());
			containerListener.addContainer(container.getId(), container);
			nmClientAsync.startContainerAsync(container, ctx);
		}
	}

	/**
	 * Setup the request that will be sent to the RM for the container ask.
	 * 
	 * @return the setup ResourceRequest to be sent to RM
	 */
	private ContainerRequest setupContainerAskForRM() {
		// setup requirements for hosts
		// using * as any host will do for the distributed shell app
		// set the priority for the request
		Priority pri = Records.newRecord(Priority.class);
		// TODO - what is the range for priority? how to decide?
		pri.setPriority(requestPriority);

		// Set up resource type requirements
		// For now, only memory is supported so we set memory requirements
		Resource capability = Records.newRecord(Resource.class);
		capability.setMemory(containerMemory);

		ContainerRequest request = new ContainerRequest(capability, null, null, pri);
		LOG.info("Requested container ask: " + request.toString());
		return request;
	}
}
