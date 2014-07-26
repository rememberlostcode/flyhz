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

package com.flyhz.avengers.framework.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.ApplicationConstants.Environment;
import org.apache.hadoop.yarn.api.ContainerManagementProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerExitStatus;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync;
import org.apache.hadoop.yarn.client.api.async.NMClientAsync;
import org.apache.hadoop.yarn.client.api.async.impl.NMClientAsyncImpl;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.security.AMRMTokenIdentifier;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.hadoop.yarn.util.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.Crawl;
import com.flyhz.avengers.framework.config.XConfiguration;
import com.flyhz.avengers.framework.lang.HBaseAVTable;
import com.flyhz.avengers.framework.lang.HBaseAVTable.HBaseAVColumn;
import com.flyhz.avengers.framework.lang.HBaseAVTable.HBaseAVFamily;
import com.google.common.annotations.VisibleForTesting;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class CrawlApplication {

	private static final Logger					LOG						= LoggerFactory.getLogger(CrawlApplication.class);

	// XConfiguration
	private Configuration						conf;

	// hbase
	private final Configuration					hbaseConf				= HBaseConfiguration.create();

	// Handle to communicate with the Resource Manager
	// @SuppressWarnings("rawtypes")
	private AMRMClientAsync<ContainerRequest>	amRMClient;

	// Handle to communicate with the Node Manager
	private NMClientAsync						nmClientAsync;
	// Listen to process the response from the Node Manager
	private NMCallbackHandler					containerListener;

	// Application Attempt Key ( combination of attemptId and fail count )
	private ApplicationAttemptId				appAttemptID;

	// For status update for clients - yet to be implemented
	// Hostname of the container
	private String								appMasterHostname		= "";
	// Port on which the app master listens for status updates from clients
	private int									appMasterRpcPort		= -1;
	// Tracking url to which app master publishes info for clients to monitor
	private String								appMasterTrackingUrl	= "";

	// 没有超过周期的root集合
	private Set<String>							domainRootForCrawlSet	= new HashSet<String>();
	// App Master configuration
	// No. of containers to run shell command on
	private int									numTotalContainers		= 1;
	// Memory to request for the container on which the shell command will run
	private int									containerMemory			= 64;
	// Priority of the request
	private int									requestPriority;

	// Counter for completed containers ( complete denotes successful or failed
	// )
	private AtomicInteger						numCompletedContainers	= new AtomicInteger();
	// Allocated container count so that we know how many containers has the RM
	// allocated to us
	private AtomicInteger						numAllocatedContainers	= new AtomicInteger();
	// Count of failed containers
	private AtomicInteger						numFailedContainers		= new AtomicInteger();
	// Count of containers already requested from the RM
	// Needed as once requested, we should not request for containers again.
	// Only request for more if the original requirement changes.
	private AtomicInteger						numRequestedContainers	= new AtomicInteger();

	private volatile boolean					done;
	private volatile boolean					success;

	private ByteBuffer							allTokens;

	// Launch threads
	private List<Thread>						launchThreads			= new ArrayList<Thread>();

	private long								batchId;

	private Options								opts;

	public CrawlApplication() {
		// Set up the configuration
		conf = new YarnConfiguration();
		hbaseConf.set("hbase.zookeeper.quorum", "m1,s1,s2");
		hbaseConf.set("hbase.zookeeper.property.clientPort", "2181");

	}

	/**
	 * @param args
	 *            Command line args
	 */
	public static void main(String[] args) {
		boolean result = false;
		LOG.info("classpath -> {}", System.getenv("CLASSPATH"));
		try {
			CrawlApplication crawlApplication = new CrawlApplication();
			LOG.info("Initializing CrawlvApplication");
			boolean doRun = crawlApplication.init(args);
			if (!doRun) {
				System.exit(0);
			}
			result = crawlApplication.run();
		} catch (Throwable t) {
			LOG.error("Error running CrawlApplication", t);
			System.exit(1);
		}
		if (result) {
			LOG.info("Application Crawl completed successfully. exiting");
			System.exit(0);
		} else {
			LOG.info("Application Crawl failed. exiting");
			System.exit(2);
		}
	}

	/**
	 * Dump out contents of $CWD and the environment to stdout for debugging
	 */
	@SuppressWarnings("unused")
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
		LOG.info("init args ...... first");
		if (args.length == 0) {
			throw new IllegalArgumentException("No args specified for client to initialize");
		}

		opts = new Options();
		opts.addOption("batchId", true, "batchId");
		CommandLine cliParser = new GnuParser().parse(opts, args);
		this.batchId = Long.valueOf(cliParser.getOptionValue("batchId"));

		Map<String, String> envs = System.getenv();

		if (!envs.containsKey(Environment.CONTAINER_ID.name())) {

			throw new IllegalArgumentException("Application Attempt Key not set in the environment");
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
				+ appAttemptID.getAttemptId() + ", batchId=" + batchId);

		return true;
	}

	@SuppressWarnings("unchecked")
	private void initHbase() {
		LOG.info("init hbase");
		HConnection hConnection = null;
		HBaseAdmin hbaseAdmin = null;
		HTable hDomain = null;
		try {
			hConnection = HConnectionManager.createConnection(hbaseConf);
			hbaseAdmin = new HBaseAdmin(hConnection);
			// av_fetch
			if (!hbaseAdmin.tableExists(HBaseAVTable.av_fetch.name())) {
				LOG.info("table[av_fetch] create it");
				HTableDescriptor tableDesc = new HTableDescriptor(
						TableName.valueOf(HBaseAVTable.av_fetch.name()));
				HColumnDescriptor info = new HColumnDescriptor(HBaseAVTable.HBaseAVFamily.i.name());
				tableDesc.addFamily(info);
				hbaseAdmin.createTable(tableDesc);
			}

			// av_page
			if (!hbaseAdmin.tableExists(HBaseAVTable.av_page.name())) {
				LOG.info("table[av_page] create it");
				HTableDescriptor tableDesc = new HTableDescriptor(
						TableName.valueOf(HBaseAVTable.av_page.name()));
				HColumnDescriptor info = new HColumnDescriptor(HBaseAVTable.HBaseAVFamily.i.name());
				tableDesc.addFamily(info);
				hbaseAdmin.createTable(tableDesc);
			}

			// av_crawl
			if (hbaseAdmin.tableExists(HBaseAVTable.av_crawl.name())) {
				LOG.info("table[av_crawl] exist drop it");
				if (hbaseAdmin.isTableEnabled(HBaseAVTable.av_crawl.name())) {
					hbaseAdmin.disableTable(HBaseAVTable.av_crawl.name());
				}
				hbaseAdmin.deleteTable(Bytes.toBytes(HBaseAVTable.av_crawl.name()));
				HTableDescriptor tableDesc = new HTableDescriptor(
						TableName.valueOf(HBaseAVTable.av_crawl.name()));
				HColumnDescriptor info = new HColumnDescriptor(HBaseAVTable.HBaseAVFamily.i.name());
				tableDesc.addFamily(info);
				LOG.info("table[av_crawl] dropped then create it");
				hbaseAdmin.createTable(tableDesc);
			} else {
				LOG.info("table[av_crawl] create it");
				HTableDescriptor tableDesc = new HTableDescriptor(
						TableName.valueOf(HBaseAVTable.av_crawl.name()));
				HColumnDescriptor info = new HColumnDescriptor(HBaseAVTable.HBaseAVFamily.i.name());
				tableDesc.addFamily(info);
				hbaseAdmin.createTable(tableDesc);
			}

			if (!hbaseAdmin.tableExists(HBaseAVTable.av_domain.name())) {
				HTableDescriptor tableDesc = new HTableDescriptor(
						TableName.valueOf(HBaseAVTable.av_domain.name()));
				HColumnDescriptor info = new HColumnDescriptor(HBaseAVTable.HBaseAVFamily.i.name());
				tableDesc.addFamily(info);
				hbaseAdmin.createTable(tableDesc);
			}

			Configuration configuration = HBaseConfiguration.create(hbaseConf);

			configuration.setLong("hbase.rpc.timeout", 600000);
			// 设置Scan缓存
			configuration.setLong("hbase.client.scanner.caching", 1000);

			Map<String, Object> context = XConfiguration.getAvengersContext();
			hDomain = new HTable(hbaseConf, HBaseAVTable.av_domain.name());
			for (String root : (Set<String>) context.get(XConfiguration.ROOTS)) {
				Map<String, Object> domainMap = (Map<String, Object>) context.get(root);
				Get hDomainGet = new Get(Bytes.toBytes(root));
				hDomainGet.addColumn(Bytes.toBytes(HBaseAVFamily.i.name()),
						Bytes.toBytes(HBaseAVColumn.bid.name()));
				Result result = hDomain.get(hDomainGet);
				LOG.info("root[{}] result.isEmpty -> {}", root,
						result == null ? null : result.isEmpty());
				if (result != null && !result.isEmpty()) {
					Cell cell = result.rawCells()[0];
					Calendar calendar = Calendar.getInstance();
					LOG.info("root[{}] av_domain.batchId -> ", root,
							Bytes.toLong(cell.getValueArray()));
					calendar.setTime(new Date(Bytes.toLong(cell.getValueArray())));
					// 小时
					Integer period = (Integer) domainMap.get(XConfiguration.CRAWL_PERIOD);
					calendar.add(Calendar.HOUR_OF_DAY, period.intValue());
					LOG.info("root[{}] calendar -> {},current.version -> {}",
							calendar.getTimeInMillis(), batchId);
					if (calendar.after(new Date(batchId))) {
						continue;
					}
				}
				domainRootForCrawlSet.add(root);
			}
		} catch (IOException e) {
			LOG.error("", e);
		} catch (Throwable e) {
			LOG.error("", e);
		} finally {
			if (hbaseAdmin != null) {
				try {
					hbaseAdmin.close();
				} catch (IOException e) {
					LOG.error("", e);
				}
			}
			if (hConnection != null) {
				try {
					hConnection.close();
				} catch (IOException e) {
					LOG.error("", e);
				}
			}
		}

	}

	private void crawl() {
		LOG.info("App crawl");
		numTotalContainers = domainRootForCrawlSet.size();
		LOG.info("initCrawl CLASSPATH -> " + System.getenv("CLASSPATH"));
		LOG.info("domainRootForCrawlSet.size() > {}", domainRootForCrawlSet.size());
		if (numTotalContainers > 0) {
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
		}
	}

	/**
	 * Main run function for the application master
	 * 
	 * @throws YarnException
	 * @throws IOException
	 */
	public boolean run() throws IOException, YarnException {
		LOG.info("Starting CrawlApplication");
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
		// the RPC server
		// send requests to this app master

		// Register self with ResourceManager
		// This will first hearInitializing Clienttbeating to the RM
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

		initHbase();

		crawl();

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
			LOG.info("Got response from RM for container ask, allocatedCnt={}",
					allocatedContainers.size());
			numAllocatedContainers.addAndGet(allocatedContainers.size());
			for (int i = 0; i < allocatedContainers.size(); i++) {
				Container allocatedContainer = allocatedContainers.get(i);
				Iterator<String> iterator = domainRootForCrawlSet.iterator();
				if (iterator.hasNext()) {
					String rootUrl = iterator.next();
					LOG.info("Launching shell command on a new container." + ", containerId="
							+ allocatedContainer.getId() + ", containerNode="
							+ allocatedContainer.getNodeId().getHost() + ":"
							+ allocatedContainer.getNodeId().getPort() + ", containerNodeURI="
							+ allocatedContainer.getNodeHttpAddress()
							+ ", containerResourceMemory="
							+ allocatedContainer.getResource().getMemory() + ", rootUrl=" + rootUrl);
					LaunchContainerRunnable runnableLaunchContainer = new LaunchContainerRunnable(
							allocatedContainer, containerListener, rootUrl);
					Thread launchThread = new Thread(runnableLaunchContainer);

					// launch and first the container on a separate thread to
					// keep
					// the main thread unblocked
					// as all containers may not be allocated at one go.
					launchThreads.add(launchThread);
					launchThread.start();
					iterator.remove();
				}
			}
		}

		@Override
		public void onShutdownRequest() {
			done = true;
		}

		@Override
		public void onNodesUpdated(List<NodeReport> updatedNodes) {
			if (updatedNodes != null && !updatedNodes.isEmpty()) {
				for (NodeReport nodeReport : updatedNodes) {
					LOG.info("updateNodes > {}", nodeReport.getNodeId().getHost());
				}
			}
		}

		@Override
		public float getProgress() {
			// set progress to deliver to RM on next heartbeat
			float progress = (float) numCompletedContainers.get() / numTotalContainers;
			return progress;
		}

		@Override
		public void onError(Throwable e) {
			LOG.error("RMCallbackHandler", e);
			done = true;
			amRMClient.stop();
		}
	}

	@VisibleForTesting
	static class NMCallbackHandler implements NMClientAsync.CallbackHandler {

		private ConcurrentMap<ContainerId, Container>	containers	= new ConcurrentHashMap<ContainerId, Container>();
		private final CrawlApplication					crawlApplication;

		public NMCallbackHandler(CrawlApplication crawlApplication) {
			this.crawlApplication = crawlApplication;
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
				LOG.debug("Succeeded to first Container {}", containerId);
			}
			Container container = containers.get(containerId);
			if (container != null) {
				crawlApplication.nmClientAsync.getContainerStatusAsync(containerId,
						container.getNodeId());
			}
		}

		@Override
		public void onStartContainerError(ContainerId containerId, Throwable t) {
			LOG.error("onStartContainerError", t);
			LOG.error("Failed to first Container {} ", containerId);
			containers.remove(containerId);
			crawlApplication.numCompletedContainers.incrementAndGet();
			crawlApplication.numFailedContainers.incrementAndGet();
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

		String				rootUrl;

		/**
		 * @param lcontainer
		 *            Allocated container
		 * @param containerListener
		 *            Callback handler of the container
		 */
		public LaunchContainerRunnable(Container lcontainer, NMCallbackHandler containerListener,
				String rootUrl) {
			this.container = lcontainer;
			this.containerListener = containerListener;
			this.rootUrl = rootUrl;
		}

		@Override
		/**
		 * Connects to CM, sets up container launch context 
		 * for shell command and eventually dispatches the container 
		 * first request to the CM. 
		 */
		public void run() {
			LOG.info("Setting up container launch container for containerid={},node={}",
					container.getId(), container.getNodeId().getHost());
			ContainerLaunchContext ctx = Records.newRecord(ContainerLaunchContext.class);
			Map<String, String> env = new HashMap<String, String>();
			for (Entry<String, String> v : System.getenv().entrySet()) {
				if (!v.getKey().equals("JAVA_HOME")) {
					env.put(v.getKey(), v.getValue());
				}

			}
			String localJarDir = conf.get("hadoop.tmp.dir") + "/" + batchId + "/avengers.jar";

			env.put("CLASSPATH", System.getenv("CLASSPATH") + ":" + localJarDir);

			ctx.setEnvironment(env);
			// Set the local resources
			Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();

			// localResources.put("", value)

			ctx.setLocalResources(localResources);

			// Set the necessary command to execute on the allocated
			// container
			Vector<CharSequence> vargs = new Vector<CharSequence>(30);
			vargs.add(Environment.JAVA_HOME.$() + "/bin/java");
			// Set Xmx based on am memory size
			vargs.add("-Xmx" + containerMemory + "m");
			// Set executable command
			vargs.add(Crawl.class.getName());
			// Set class name
			vargs.add("-url");
			vargs.add(rootUrl);
			vargs.add("-batchId");
			vargs.add(String.valueOf(batchId));
			// Add log redirect params
			vargs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/Crawl.stdout");
			vargs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/Crawl.stderr");

			// Get final commmand
			StringBuilder command = new StringBuilder();
			for (CharSequence str : vargs) {
				command.append(str).append(" ");
			}
			// Add log redirect params
			LOG.info("command {},run on node {},containerId is {},batchId is {}", command,
					this.container.getNodeId().getHost(), this.container.getId(),
					CrawlApplication.this.batchId);
			List<String> commands = new ArrayList<String>();
			commands.add(command.toString());
			ctx.setCommands(commands);
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

		pri.setPriority(requestPriority);

		// Set up resource type requirements
		// For now, only memory is supported so we set memory requirements
		Resource capability = Records.newRecord(Resource.class);
		capability.setMemory(containerMemory);
		ContainerRequest request = new ContainerRequest(capability, null, null, pri);
		LOG.info("Requested container ask: {},request node: {} ", request.toString(),
				request.getNodes() != null && !request.getNodes().isEmpty() ? request.getNodes()
																						.get(0)
						: "");
		return request;
	}
}
