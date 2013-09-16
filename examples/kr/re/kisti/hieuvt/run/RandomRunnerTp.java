package kr.re.kisti.hieuvt.run;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kr.re.kisti.hieuvt.core.HelperTp;
import kr.re.kisti.hieuvt.core.TpVm;
import kr.re.kisti.hieuvt.core.RandomConstantsTp;
import kr.re.kisti.hieuvt.graph.Edge;
import kr.re.kisti.hieuvt.graph.Graph;
import kr.re.kisti.hieuvt.graph.HelperGraph;
import kr.re.kisti.hieuvt.graph.Vertex;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.examples.power.Helper;
import org.jdom2.JDOMException;

import uk.ac.ucl.ee.fnss.Parser;
import uk.ac.ucl.ee.fnss.TrafficMatrix;
import uk.ac.ucl.ee.fnss.TrafficMatrixSequence;

/**
 * The example runner for the random workload.
 * 
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
public class RandomRunnerTp extends RunnerAbstractTp {

	/**
	 * @param enableOutput
	 * @param outputToFile
	 * @param inputFolder
	 * @param outputFolder
	 * @param workload
	 * @param vmAllocationPolicy
	 * @param vmSelectionPolicy
	 * @param parameter
	 */
	public RandomRunnerTp(boolean enableOutput, boolean outputToFile,
			String inputFolder, String outputFolder, String workload,
			String vmAllocationPolicy, String vmSelectionPolicy,
			String parameter) {
		super(enableOutput, outputToFile, inputFolder, outputFolder, workload,
				vmAllocationPolicy, vmSelectionPolicy, parameter);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cloudbus.cloudsim.examples.power.RunnerAbstract#init(java.lang.String
	 * )
	 */
	@Override
	protected void init(String inputFolder) {
		try {

			CloudSim.init(1, Calendar.getInstance(), false);

			tpBroker = HelperTp.createTpBroker();
			int brokerId = tpBroker.getId();

			vmList = HelperTp.createTpVmList(brokerId,
					RandomConstantsTp.NUMBER_OF_VMS);
			
			HelperTp.classifyVms(vmList, RandomConstantsTp.trafficMatrixFile);
			cloudletList = RandomHelperTp.createRankedCloudletList(brokerId,
					vmList);
			hostList = HelperTp.createTpHostList(
					RandomConstantsTp.NUM_PORT_CORE,
					RandomConstantsTp.NUM_PORT_AGG,
					RandomConstantsTp.NUM_PORT_EDGE);

		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
			System.exit(0);
		}
	}
}
