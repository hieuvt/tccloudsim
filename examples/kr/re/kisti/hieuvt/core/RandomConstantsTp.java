package kr.re.kisti.hieuvt.core;

import java.util.HashMap;
import java.util.Map;

/**
 * If you are using any algorithms, policies or workload included in the power
 * package please cite the following paper:
 * 
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic
 * Algorithms and Adaptive Heuristics for Energy and Performance Efficient
 * Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency
 * and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages:
 * 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 * 
 * @author Anton Beloglazov
 * @since Jan 5, 2012
 */
public class RandomConstantsTp {

	public final static int NUMBER_OF_VMS = 310;
	public final static long CLOUDLET_UTILIZATION_SEED = 1;
	
	public final static String trafficMatrixFile = "traffic/traffic_matrix.xml";
	public final static String dbName = "graphDb";

	/**
	 * VMs are classified into 3 categories based on their traffic 
	 * CPU intensive: traffic < 1 Mbps, CPU utilization > 0.66
	 * CPU - Network balance: 1 Mbps < traffic < 5, 0.33 < CPU utilization < 0.66 
	 * Network intensive: traffic > 5 Mbps, CPU utilization < 0.33
	 */
	public static final int CPU_INTENSIVE = 0;
	public static final int CPU_NETWORK_BALANCE = 1;
	public static final int NETWORK_INTENSIVE = 2;

	public static final double RANK_NETWORK_INTENSIVE = 15;
	public static final double RANK_CPU_NETWORK_BALANCE = 5;

	public static final double CPU_INTENSIVE_MIN_UTILIZATION = 0.66;
	public static final double CPU_NETWORK_BALANCE_MIN_UTILIZATION = 0.33;

	/**
	 * Data center topology: 1 pair of core switch 4 pair of aggression switch
	 * Each pair of aggregation switch has 5 partitions Each partitions has 10
	 * hosts (totally 10 partitions)
	 */

	public static final int CORE_LEVEL = 0;
	public static final int AGG_LEVEL = 1;
	public static final int EDGE_LEVEL = 2;

	public static int NUM_PORT_EDGE = 10;
	public static int NUM_PORT_AGG = 5;
	public static int NUM_PORT_CORE = 3;

	@SuppressWarnings("serial")
	public static Map<Integer, Integer> distanceCost = new HashMap<Integer, Integer>() {
		{
			put(0, 1);
			put(1, 3);
			put(3, 5);
		}
	};
	
	/**
	 * Edge remove ratio each round
	 */
	public static double REMOVE_RATIO = 0.5;
	
	/**
	 * #Simulation steps, each step equal to 5 mins
	 */
	public static int SIMULATION_PERIOD = 289;
	public static int SIMULATION_LIMIT = 24 *60 * 60;

}
