/*
 *
 */
package kr.re.kisti.hieuvt.run;

import java.util.ArrayList;
import java.util.List;

import kr.re.kisti.hieuvt.core.TpVm;
import kr.re.kisti.hieuvt.core.RandomConstantsTp;
import kr.re.kisti.hieuvt.core.UtilizationModelStochasticCpuIntensive;
import kr.re.kisti.hieuvt.core.UtilizationModelStochasticCpuNetworkBalance;
import kr.re.kisti.hieuvt.core.UtilizationModelStochasticNetworkIntensive;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelNull;
import org.cloudbus.cloudsim.UtilizationModelStochastic;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.examples.power.Constants;

/**
 * The Helper class for the random workload.
 * 
 * If you are using any algorithms, policies or workload included in the power package please cite
 * the following paper:
 * 
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic Algorithms and Adaptive
 * Heuristics for Energy and Performance Efficient Dynamic Consolidation of Virtual Machines in
 * Cloud Data Centers", Concurrency and Computation: Practice and Experience (CCPE), Volume 24,
 * Issue 13, Pages: 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 * 
 * @author Anton Beloglazov
 * @since Jan 5, 2012
 */
public class RandomHelperTp {

	/**
	 * Creates the cloudlet list.
	 * 
	 * @param brokerId the broker id
	 * @return the list< cloudlet>
	 */
	public static List<Cloudlet> createRankedCloudletList(int brokerId, List<Vm> vmList) {
		List<Cloudlet> list = new ArrayList<Cloudlet>();

		long fileSize = 300;
		long outputSize = 300;
		long seed = RandomConstantsTp.CLOUDLET_UTILIZATION_SEED;
		UtilizationModel utilizationModelNull = new UtilizationModelNull();

		for (int i = 0; i < vmList.size(); i++) {
			Cloudlet cloudlet = null;
			if (seed == -1) {
				cloudlet = new Cloudlet(
						i,
						Constants.CLOUDLET_LENGTH,
						Constants.CLOUDLET_PES,
						fileSize,
						outputSize,
						new UtilizationModelStochastic(),
						utilizationModelNull,
						utilizationModelNull);
			} else if (((TpVm) vmList.get(i)).rank() == RandomConstantsTp.CPU_INTENSIVE){
				cloudlet = new Cloudlet(
						i,
						Constants.CLOUDLET_LENGTH,
						Constants.CLOUDLET_PES,
						fileSize,
						outputSize,
						new UtilizationModelStochasticCpuIntensive(seed * i),
						utilizationModelNull,
						utilizationModelNull);
			} else if (((TpVm) vmList.get(i)).rank() == RandomConstantsTp.CPU_NETWORK_BALANCE){
				cloudlet = new Cloudlet(
						i,
						Constants.CLOUDLET_LENGTH,
						Constants.CLOUDLET_PES,
						fileSize,
						outputSize,
						new UtilizationModelStochasticCpuNetworkBalance(seed * i),
						utilizationModelNull,
						utilizationModelNull);
			} else {
				cloudlet = new Cloudlet(
						i,
						Constants.CLOUDLET_LENGTH,
						Constants.CLOUDLET_PES,
						fileSize,
						outputSize,
						new UtilizationModelStochasticNetworkIntensive(seed * i),
						utilizationModelNull,
						utilizationModelNull);
			}
			cloudlet.setUserId(brokerId);
			cloudlet.setVmId(i);
			list.add(cloudlet);
		}

		return list;
	}

}
