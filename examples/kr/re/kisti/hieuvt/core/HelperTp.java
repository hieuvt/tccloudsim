package kr.re.kisti.hieuvt.core;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.kisti.re.hieuvt.tree.HelperTree;
import kr.kisti.re.hieuvt.tree.Node;
import kr.kisti.re.hieuvt.tree.TreeHsql;
import kr.kisti.re.hieuvt.tree.TreeDbServer;
import kr.kisti.re.hieuvt.tree.TreeMySql;
import kr.kisti.re.hieuvt.tree.TreeNoDb;
import kr.re.kisti.hieuvt.core.EdgeSw;
import kr.re.kisti.hieuvt.core.TpHostUtilizationHistory;
import kr.re.kisti.hieuvt.core.TpVm;
import kr.re.kisti.hieuvt.core.TpSw;
import kr.re.kisti.hieuvt.core.RandomConstantsTp;
import kr.re.kisti.hieuvt.graph.Edge;
import kr.re.kisti.hieuvt.graph.Graph;
import kr.re.kisti.hieuvt.graph.HelperGraph;
import kr.re.kisti.hieuvt.graph.Vertex;

import org.cloudbus.cloudsim.CloudletSchedulerDynamicWorkload;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSchedulerTimeSharedOverSubscription;
import org.cloudbus.cloudsim.examples.power.Constants;
import org.cloudbus.cloudsim.examples.power.Helper;
import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationAbstract;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.util.MathUtil;
import org.hsqldb.Server;
import org.jdom2.JDOMException;

import uk.ac.ucl.ee.fnss.Parser;
import uk.ac.ucl.ee.fnss.TrafficMatrix;
import uk.ac.ucl.ee.fnss.TrafficMatrixSequence;

public class HelperTp extends Helper {

	public static TpDatacenterBroker createTpBroker() {
		TpDatacenterBroker tpBroker = null;
		try {
			tpBroker = new TpDatacenterBroker("TpBroker");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return tpBroker;
	}

	
	private static TrafficMatrix getTrafficMatrixAtTime(String trafficMatrixFile, int seqId) {
		TrafficMatrixSequence trafficMatrixSeq = null;
		try {
			trafficMatrixSeq = Parser
					.parseTrafficMatrixSequence(trafficMatrixFile);
		} catch (JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Successfully parsed topology from file "
				+ trafficMatrixFile);

		TrafficMatrix trafficMatrix = trafficMatrixSeq.getMatrix(seqId);
		return trafficMatrix;
	}
	
	public static void updateTrafficMap(List <Vm> vmList, String trafficMatrixFile, int seqId) {
		double[] totalTrafficToOthers = new double[RandomConstantsTp.NUMBER_OF_VMS];
		double[][] traffic = new double[RandomConstantsTp.NUMBER_OF_VMS][RandomConstantsTp.NUMBER_OF_VMS];
		Integer origin;
		Integer destination;

		TrafficMatrix trafficMatrix = getTrafficMatrixAtTime(trafficMatrixFile, seqId);
		for (int i = 0; i < RandomConstantsTp.NUMBER_OF_VMS; i++) {
			totalTrafficToOthers[i] = 0;
		}

		for (origin = 0; origin < RandomConstantsTp.NUMBER_OF_VMS; origin++) {
			TpVm tmpTpVm = (TpVm) vmList.get(origin);
			for (destination = 0; destination < RandomConstantsTp.NUMBER_OF_VMS; destination++) {
				if (origin == destination) {
					traffic[origin][destination] = 0;
				} else {
					traffic[origin][destination] = trafficMatrix.getFlow(
							origin.toString(), destination.toString());
				}
				totalTrafficToOthers[origin] = totalTrafficToOthers[origin]
						+ traffic[origin][destination];
			}
			tmpTpVm.setTrafficToOthers(traffic[origin]);
		}
	}

	public static void classifyVms(List<Vm> vmList, String trafficMatrixFile)
			throws IOException {

		int numCpuIntensive = 0;
		int numCpuNetworkBalance = 0;
		int numNetworkIntensive = 0;

		updateTrafficMap(vmList, trafficMatrixFile, 0);

		for (int i = 0; i < RandomConstantsTp.NUMBER_OF_VMS; i++) {
			TpVm tmpTpVm = (TpVm) vmList.get(i);
//			tmpTpVm.setTrafficToOthers(traffic[i]);
			int tmpTpVmRank = tmpTpVm.rank();
			if (tmpTpVmRank == RandomConstantsTp.CPU_INTENSIVE) {
				numCpuIntensive++;
			} else if (tmpTpVmRank == RandomConstantsTp.CPU_NETWORK_BALANCE) {
				numCpuNetworkBalance++;
			} else {
				numNetworkIntensive++;
			}
		}
		Log.formatLine("# Network Intensive: " + numNetworkIntensive);
		Log.formatLine("# CPU Network Balance: " + numCpuNetworkBalance);
		Log.formatLine("# CPU Intensive: " + numCpuIntensive);
	}

	
	public static void updateVmTrafficTree(List<Vm> vmList,
			String trafficMatrixFile, int trafficSeq) {
		List<Edge<Vm>> edgeList = new ArrayList<Edge<Vm>>();
		List<Vertex<Vm>> vmVertexList = new ArrayList<Vertex<Vm>>();
		TrafficMatrix trafficMatrix = getTrafficMatrixAtTime(trafficMatrixFile, trafficSeq);
		for (int i = 0; i < RandomConstantsTp.NUMBER_OF_VMS; i++) {
			Vertex<Vm> vertex = (Vertex<Vm>) new Vertex<>(i, vmList.get(i));
			vmVertexList.add(vertex);
		}

		Integer vm1Id;
		Integer vm2Id;
		int edgeId = 0;
		for (vm1Id = 0; vm1Id < RandomConstantsTp.NUMBER_OF_VMS; vm1Id++) {
			for (vm2Id = vm1Id + 1; vm2Id < RandomConstantsTp.NUMBER_OF_VMS; vm2Id++) {
				double traffic1 = trafficMatrix.getFlow(vm1Id.toString(),
						vm2Id.toString());
				double traffic2 = trafficMatrix.getFlow(vm2Id.toString(),
						vm1Id.toString());
				Edge<Vm> tmpEdgeTpVm = new Edge<>(edgeId,
						vmVertexList.get(vm1Id), vmVertexList.get(vm2Id),
						traffic1 + traffic2);
				edgeId++;
				edgeList.add(tmpEdgeTpVm);
			}
		}
		Graph<Vm> vmGraph = new Graph<Vm>(vmVertexList, edgeList);

		String tableName = "graphTable"  + ((Integer) trafficSeq).toString();
		String itemName = "graph";
		double removeRatio = RandomConstantsTp.REMOVE_RATIO;
		
//		TreeHsql<Graph<Vm>> graphTree = new TreeHsql<Graph<Vm>>(tableName, itemName, vmGraph.toString());
		TreeMySql<Graph<Vm>> graphTree = new TreeMySql<Graph<Vm>>(tableName, itemName, vmGraph.toString());
		addSubGraphToTree(graphTree, tableName, itemName, vmGraph, removeRatio);
}

	private static void addSubGraphToTree(TreeMySql<Graph<Vm>> graphTree,
			String tableName, String itemName, Graph<Vm> graph,
			double removeRatio) {
		// TODO Auto-generated method stub
		HelperGraph<Vm> helperGraph = new HelperGraph<Vm>();
		int graphId = graphTree.getNodeByContent(tableName, itemName,
				graph.toString());
		List<Graph<Vm>> subGraphList = helperGraph.divideToSubGraphs(graph,
				removeRatio);
		for (Graph<Vm> subGraph : subGraphList) {
			int id = graphTree.getNodeByContent(tableName, itemName,
					subGraph.toString());
			if (id == -1) {
				graphTree.addNode(tableName, itemName, graphId,
						subGraph.toString());
			}
			if (subGraph.getVertexList().size() != 1 && subGraph.getEdgeList().size() != 0) {
				addSubGraphToTree(graphTree, tableName, itemName, subGraph,
						removeRatio);
			}
		}
	}


	private static void addSubGraphToTree(TreeHsql<Graph<Vm>> graphTree,
			String tableName, String itemName, Graph<Vm> graph,
			double removeRatio) {
		HelperGraph<Vm> helperGraph = new HelperGraph<Vm>();
		int graphId = graphTree.getNodeByContent(tableName, itemName,
				graph.toString());
		List<Graph<Vm>> subGraphList = helperGraph.divideToSubGraphs(graph,
				removeRatio);
		for (Graph<Vm> subGraph : subGraphList) {
			int id = graphTree.getNodeByContent(tableName, itemName,
					subGraph.toString());
			if (id == -1) {
				graphTree.addNode(tableName, itemName, graphId,
						subGraph.toString());
			}
			if (subGraph.getVertexList().size() != 1 && subGraph.getEdgeList().size() != 0) {
				addSubGraphToTree(graphTree, tableName, itemName, subGraph,
						removeRatio);
			}
		}

	}

	public static List<Vm> createTpVmList(int brokerId, int tpVmsNumber) {
		List<Vm> vms = new ArrayList<Vm>();
		for (int i = 0; i < tpVmsNumber; i++) {
			int vmType = i
					/ (int) Math
							.ceil((double) tpVmsNumber / Constants.VM_TYPES);
			vms.add(new TpVm(
					i,
					brokerId,
					Constants.VM_MIPS[vmType],
					Constants.VM_PES[vmType],
					Constants.VM_RAM[vmType],
					Constants.VM_BW,
					Constants.VM_SIZE,
					1,
					"Xen",
					new CloudletSchedulerDynamicWorkload(
							Constants.VM_MIPS[vmType], Constants.VM_PES[vmType]),
					Constants.SCHEDULING_INTERVAL));
		}
		return vms;
	}

	/**
	 * Create topology as a tree of switches and attach hosts to edge switches.
	 * All hosts connected to a switch have the same type.
	 * 
	 * @param numPortCore
	 *            number of core switch port
	 * @param numPortAgg
	 *            number of aggregation switch port
	 * @param numPortEdge
	 *            number of edge switch port (or number of hosts connected to
	 *            that switch)
	 * @return list of PowerHost
	 */
	public static List<PowerHost> createTpHostList(int numPortCore,
			int numPortAgg, int numPortEdge) {
		// TODO Auto-generated method stub

		List<PowerHost> tpHostList = new ArrayList<PowerHost>();
		int id = 0;

		// initilize switch tree with core switch as root

		Node<TpSw> coreSwNode = new Node<TpSw>(new TpSw(
				RandomConstantsTp.CORE_LEVEL, numPortCore));
		TreeNoDb<TpSw> tpSwTree = new TreeNoDb<TpSw>(coreSwNode);

		// add agg switches
		HelperTree<TpSw> helperTree = new HelperTree<TpSw>();
		for (int i = 0; i < numPortCore; i++) {
			Node<TpSw> aggSwNode = new Node<TpSw>(new TpSw(
					RandomConstantsTp.AGG_LEVEL, numPortAgg));
			helperTree.addNode(tpSwTree, coreSwNode, aggSwNode);
			for (int j = 0; j < numPortAgg; j++) {
				Node<TpSw> edgeSwNode = new Node<TpSw>(new EdgeSw(
						RandomConstantsTp.EDGE_LEVEL, numPortEdge));
				helperTree.addNode(tpSwTree, aggSwNode, edgeSwNode);
				int hostType = (i + j) % Constants.HOST_TYPES;

				// all hosts connected to an edge switch have the same type

				for (int k = 0; k < numPortEdge; k++) {
					List<Pe> peList = new ArrayList<Pe>();
					for (int l = 0; l < Constants.HOST_PES[hostType]; l++) {
						peList.add(new Pe(l, new PeProvisionerSimple(
								Constants.HOST_MIPS[hostType])));
					}
					PowerHost tmpHost = new TpHostUtilizationHistory(id,
							new RamProvisionerSimple(
									Constants.HOST_RAM[hostType]),
							new BwProvisionerSimple(Constants.HOST_BW),
							Constants.HOST_STORAGE, peList,
							new VmSchedulerTimeSharedOverSubscription(peList),
							Constants.HOST_POWER[hostType], edgeSwNode);
					EdgeSw edgeSw = (EdgeSw) edgeSwNode.getData();
					edgeSw.getTpHostList().add(tmpHost);
					tpHostList.add(tmpHost);
					id++;
				}
			}
		}
		System.out.println("count = " + id);
		System.out.println(tpHostList.size());
		return tpHostList;
	}

	public <T> int calculateNodeDistance(Node<T> node1, Node<T> node2) {
		List<Integer> shorter;
		List<Integer> longer;
		int distance = 0;
		if (node1.getHierrachicalId().size() <= node2.getHierrachicalId()
				.size()) {
			shorter = new ArrayList<Integer>(node1.getHierrachicalId());
			longer = new ArrayList<Integer>(node2.getHierrachicalId());
		} else {
			shorter = new ArrayList<Integer>(node2.getHierrachicalId());
			longer = new ArrayList<Integer>(node1.getHierrachicalId());
		}
		for (int i = 0; i < shorter.size(); i++) {
			if (shorter.get(i) != longer.get(i)) {
				distance = (shorter.size() - 1 - i) + (longer.size() - 1 - i)
						+ 1;
				return distance;
			} else {
				distance = longer.size() - shorter.size();
			}
		}
		return distance;
	}

	public static void printResults(PowerDatacenter datacenter, List<Vm> vms,
			double lastClock, String experimentName, boolean outputInCsv,
			String outputFolder) {
		Log.enable();
		List<Host> hosts = datacenter.getHostList();

		int numberOfHosts = hosts.size();
		int numberOfVms = vms.size();

		double totalSimulationTime = lastClock;
		double energy = datacenter.getPower() / (3600 * 1000);

		double trafficCost = ((TpDatacenter) datacenter).getTrafficCost();

		int numberOfMigrations = datacenter.getMigrationCount();

		Map<String, Double> slaMetrics = getSlaMetrics(vms);

		double slaOverall = slaMetrics.get("overall");
		double slaAverage = slaMetrics.get("average");
		double slaDegradationDueToMigration = slaMetrics
				.get("underallocated_migration");
		// double slaTimePerVmWithMigration =
		// slaMetrics.get("sla_time_per_vm_with_migration");
		// double slaTimePerVmWithoutMigration =
		// slaMetrics.get("sla_time_per_vm_without_migration");
		// double slaTimePerHost = getSlaTimePerHost(hosts);
		double slaTimePerActiveHost = getSlaTimePerActiveHost(hosts);

		double sla = slaTimePerActiveHost * slaDegradationDueToMigration;

		List<Double> timeBeforeHostShutdown = getTimesBeforeHostShutdown(hosts);

		int numberOfHostShutdowns = timeBeforeHostShutdown.size();

		double meanTimeBeforeHostShutdown = Double.NaN;
		double stDevTimeBeforeHostShutdown = Double.NaN;
		if (!timeBeforeHostShutdown.isEmpty()) {
			meanTimeBeforeHostShutdown = MathUtil.mean(timeBeforeHostShutdown);
			stDevTimeBeforeHostShutdown = MathUtil
					.stDev(timeBeforeHostShutdown);
		}

		List<Double> timeBeforeVmMigration = getTimesBeforeVmMigration(vms);
		double meanTimeBeforeVmMigration = Double.NaN;
		double stDevTimeBeforeVmMigration = Double.NaN;
		if (!timeBeforeVmMigration.isEmpty()) {
			meanTimeBeforeVmMigration = MathUtil.mean(timeBeforeVmMigration);
			stDevTimeBeforeVmMigration = MathUtil.stDev(timeBeforeVmMigration);
		}

		if (outputInCsv) {
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
			}
			File folder1 = new File(outputFolder + "/stats");
			if (!folder1.exists()) {
				folder1.mkdir();
			}
			File folder2 = new File(outputFolder + "/time_before_host_shutdown");
			if (!folder2.exists()) {
				folder2.mkdir();
			}
			File folder3 = new File(outputFolder + "/time_before_vm_migration");
			if (!folder3.exists()) {
				folder3.mkdir();
			}
			File folder4 = new File(outputFolder + "/metrics");
			if (!folder4.exists()) {
				folder4.mkdir();
			}

			StringBuilder data = new StringBuilder();
			String delimeter = ",";

			data.append(experimentName + delimeter);
			data.append(parseExperimentName(experimentName));
			data.append(String.format("%d", numberOfHosts) + delimeter);
			data.append(String.format("%d", numberOfVms) + delimeter);
			data.append(String.format("%.2f", totalSimulationTime) + delimeter);
			data.append(String.format("%.5f", energy) + delimeter);
			data.append(String.format("%d", numberOfMigrations) + delimeter);
			data.append(String.format("%.10f", sla) + delimeter);
			data.append(String.format("%.10f", slaTimePerActiveHost)
					+ delimeter);
			data.append(String.format("%.10f", slaDegradationDueToMigration)
					+ delimeter);
			data.append(String.format("%.10f", slaOverall) + delimeter);
			data.append(String.format("%.10f", slaAverage) + delimeter);
			// data.append(String.format("%.5f", slaTimePerVmWithMigration) +
			// delimeter);
			// data.append(String.format("%.5f", slaTimePerVmWithoutMigration) +
			// delimeter);
			// data.append(String.format("%.5f", slaTimePerHost) + delimeter);
			data.append(String.format("%d", numberOfHostShutdowns) + delimeter);
			data.append(String.format("%.2f", meanTimeBeforeHostShutdown)
					+ delimeter);
			data.append(String.format("%.2f", stDevTimeBeforeHostShutdown)
					+ delimeter);
			data.append(String.format("%.2f", meanTimeBeforeVmMigration)
					+ delimeter);
			data.append(String.format("%.2f", stDevTimeBeforeVmMigration)
					+ delimeter);

			if (datacenter.getVmAllocationPolicy() instanceof PowerVmAllocationPolicyMigrationAbstract) {
				PowerVmAllocationPolicyMigrationAbstract vmAllocationPolicy = (PowerVmAllocationPolicyMigrationAbstract) datacenter
						.getVmAllocationPolicy();

				double executionTimeVmSelectionMean = MathUtil
						.mean(vmAllocationPolicy
								.getExecutionTimeHistoryVmSelection());
				double executionTimeVmSelectionStDev = MathUtil
						.stDev(vmAllocationPolicy
								.getExecutionTimeHistoryVmSelection());
				double executionTimeHostSelectionMean = MathUtil
						.mean(vmAllocationPolicy
								.getExecutionTimeHistoryHostSelection());
				double executionTimeHostSelectionStDev = MathUtil
						.stDev(vmAllocationPolicy
								.getExecutionTimeHistoryHostSelection());
				double executionTimeVmReallocationMean = MathUtil
						.mean(vmAllocationPolicy
								.getExecutionTimeHistoryVmReallocation());
				double executionTimeVmReallocationStDev = MathUtil
						.stDev(vmAllocationPolicy
								.getExecutionTimeHistoryVmReallocation());
				double executionTimeTotalMean = MathUtil
						.mean(vmAllocationPolicy.getExecutionTimeHistoryTotal());
				double executionTimeTotalStDev = MathUtil
						.stDev(vmAllocationPolicy
								.getExecutionTimeHistoryTotal());

				data.append(String.format("%.5f", executionTimeVmSelectionMean)
						+ delimeter);
				data.append(String
						.format("%.5f", executionTimeVmSelectionStDev)
						+ delimeter);
				data.append(String.format("%.5f",
						executionTimeHostSelectionMean) + delimeter);
				data.append(String.format("%.5f",
						executionTimeHostSelectionStDev) + delimeter);
				data.append(String.format("%.5f",
						executionTimeVmReallocationMean) + delimeter);
				data.append(String.format("%.5f",
						executionTimeVmReallocationStDev) + delimeter);
				data.append(String.format("%.5f", executionTimeTotalMean)
						+ delimeter);
				data.append(String.format("%.5f", executionTimeTotalStDev)
						+ delimeter);

				writeMetricHistory(hosts, vmAllocationPolicy, outputFolder
						+ "/metrics/" + experimentName + "_metric");
			}

			data.append("\n");

			writeDataRow(data.toString(), outputFolder + "/stats/"
					+ experimentName + "_stats.csv");
			writeDataColumn(timeBeforeHostShutdown, outputFolder
					+ "/time_before_host_shutdown/" + experimentName
					+ "_time_before_host_shutdown.csv");
			writeDataColumn(timeBeforeVmMigration, outputFolder
					+ "/time_before_vm_migration/" + experimentName
					+ "_time_before_vm_migration.csv");

		} else {
			Log.setDisabled(false);
			Log.printLine();
			Log.printLine(String.format("Experiment name: " + experimentName));
			Log.printLine(String.format("Number of hosts: " + numberOfHosts));
			Log.printLine(String.format("Number of VMs: " + numberOfVms));
			Log.printLine(String.format("Total simulation time: %.2f sec",
					totalSimulationTime));
			Log.printLine(String.format("Energy consumption: %.2f kWh", energy));

			Log.printLine(String.format(
					"Traffic cost: %.2f traffic*distance*sec", trafficCost));

			Log.printLine(String.format("Number of VM migrations: %d",
					numberOfMigrations));
			Log.printLine(String.format("SLA: %.5f%%", sla * 100));
			Log.printLine(String.format(
					"SLA perf degradation due to migration: %.2f%%",
					slaDegradationDueToMigration * 100));
			Log.printLine(String.format("SLA time per active host: %.2f%%",
					slaTimePerActiveHost * 100));
			Log.printLine(String.format("Overall SLA violation: %.2f%%",
					slaOverall * 100));
			Log.printLine(String.format("Average SLA violation: %.2f%%",
					slaAverage * 100));

			// ////////////////////
			System.out.println();
			System.out.println("Energy: " + energy);
			System.out.println("Traffic cost: " + trafficCost);
			System.out.println("# migration: " + numberOfMigrations);
			System.out.println("SLA: " + sla);

			// Log.printLine(String.format("SLA time per VM with migration: %.2f%%",
			// slaTimePerVmWithMigration * 100));
			// Log.printLine(String.format("SLA time per VM without migration: %.2f%%",
			// slaTimePerVmWithoutMigration * 100));
			// Log.printLine(String.format("SLA time per host: %.2f%%",
			// slaTimePerHost * 100));
			Log.printLine(String.format("Number of host shutdowns: %d",
					numberOfHostShutdowns));
			Log.printLine(String.format(
					"Mean time before a host shutdown: %.2f sec",
					meanTimeBeforeHostShutdown));
			Log.printLine(String.format(
					"StDev time before a host shutdown: %.2f sec",
					stDevTimeBeforeHostShutdown));
			Log.printLine(String.format(
					"Mean time before a VM migration: %.2f sec",
					meanTimeBeforeVmMigration));
			Log.printLine(String.format(
					"StDev time before a VM migration: %.2f sec",
					stDevTimeBeforeVmMigration));

			if (datacenter.getVmAllocationPolicy() instanceof PowerVmAllocationPolicyMigrationAbstract) {
				PowerVmAllocationPolicyMigrationAbstract vmAllocationPolicy = (PowerVmAllocationPolicyMigrationAbstract) datacenter
						.getVmAllocationPolicy();

				double executionTimeVmSelectionMean = MathUtil
						.mean(vmAllocationPolicy
								.getExecutionTimeHistoryVmSelection());
				double executionTimeVmSelectionStDev = MathUtil
						.stDev(vmAllocationPolicy
								.getExecutionTimeHistoryVmSelection());
				double executionTimeHostSelectionMean = MathUtil
						.mean(vmAllocationPolicy
								.getExecutionTimeHistoryHostSelection());
				double executionTimeHostSelectionStDev = MathUtil
						.stDev(vmAllocationPolicy
								.getExecutionTimeHistoryHostSelection());
				double executionTimeVmReallocationMean = MathUtil
						.mean(vmAllocationPolicy
								.getExecutionTimeHistoryVmReallocation());
				double executionTimeVmReallocationStDev = MathUtil
						.stDev(vmAllocationPolicy
								.getExecutionTimeHistoryVmReallocation());
				double executionTimeTotalMean = MathUtil
						.mean(vmAllocationPolicy.getExecutionTimeHistoryTotal());
				double executionTimeTotalStDev = MathUtil
						.stDev(vmAllocationPolicy
								.getExecutionTimeHistoryTotal());

				Log.printLine(String.format(
						"Execution time - VM selection mean: %.5f sec",
						executionTimeVmSelectionMean));
				Log.printLine(String.format(
						"Execution time - VM selection stDev: %.5f sec",
						executionTimeVmSelectionStDev));
				Log.printLine(String.format(
						"Execution time - host selection mean: %.5f sec",
						executionTimeHostSelectionMean));
				Log.printLine(String.format(
						"Execution time - host selection stDev: %.5f sec",
						executionTimeHostSelectionStDev));
				Log.printLine(String.format(
						"Execution time - VM reallocation mean: %.5f sec",
						executionTimeVmReallocationMean));
				Log.printLine(String.format(
						"Execution time - VM reallocation stDev: %.5f sec",
						executionTimeVmReallocationStDev));
				Log.printLine(String.format(
						"Execution time - total mean: %.5f sec",
						executionTimeTotalMean));
				Log.printLine(String.format(
						"Execution time - total stDev: %.5f sec",
						executionTimeTotalStDev));
			}
			Log.printLine();
		}

		Log.setDisabled(false);
//		TreeDbServer.stop();
	}
}
