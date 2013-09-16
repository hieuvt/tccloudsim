package kr.re.kisti.hieuvt.run;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kr.kisti.re.hieuvt.tree.TreeDbServer;
import kr.re.kisti.hieuvt.core.HelperTp;
import kr.re.kisti.hieuvt.core.PowerVmSelectionPolicyMaximumTraffic;
import kr.re.kisti.hieuvt.core.RandomConstantsTp;
import kr.re.kisti.hieuvt.core.TpDatacenter;
import kr.re.kisti.hieuvt.core.TpDatacenterBroker;
import kr.re.kisti.hieuvt.core.TpPowerVmAllocationPolicyMigrationLocalRegression;
import kr.re.kisti.hieuvt.core.TpSw;
import kr.re.kisti.hieuvt.core.TpVm;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModelStochastic;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.examples.power.Constants;
import org.cloudbus.cloudsim.examples.power.Helper;
import org.cloudbus.cloudsim.examples.power.RunnerAbstract;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationAbstract;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationInterQuartileRange;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationLocalRegression;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationLocalRegressionRobust;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationMedianAbsoluteDeviation;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationStaticThreshold;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicySimple;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicyMaximumCorrelation;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicyMinimumMigrationTime;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicyMinimumUtilization;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicyRandomSelection;

public class RunnerAbstractTp extends RunnerAbstract {

	protected static List<TpSw> npSwList;
	protected static TpDatacenterBroker tpBroker;

	public RunnerAbstractTp(boolean enableOutput, boolean outputToFile,
			String inputFolder, String outputFolder, String workload,
			String vmAllocationPolicy, String vmSelectionPolicy,
			String parameter) {
		super(enableOutput, outputToFile, inputFolder, outputFolder, workload,
				vmAllocationPolicy, vmSelectionPolicy, parameter);
		// TODO Auto-generated constructor stub

	}

	@Override
	protected void init(String inputFolder) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void start(String experimentName, String outputFolder,
			VmAllocationPolicy vmAllocationPolicy) {
		System.out.println("Starting " + experimentName);

		try {
			TpDatacenter datacenter = (TpDatacenter) Helper.createDatacenter(
					"Datacenter", TpDatacenter.class, hostList,
					vmAllocationPolicy);

			datacenter.setDisableMigrations(false);

//			Collections.sort(vmList, new Comparator<Vm>(){
//				@Override
//				public int compare(Vm o1, Vm o2) {
//					// TODO Auto-generated method stub
//					return (o1.getCurrentRequestedMaxMips() > o2.getCurrentRequestedMaxMips() ? 1 : (o1.getCurrentRequestedMaxMips() == o2
//							.getCurrentRequestedMaxMips() ? 0 : -1));
//				}
//			});
			
			tpBroker.submitVmList(vmList);
			tpBroker.submitCloudletList(cloudletList);

			// print cpu utilization to files
			for (Vm vm : vmList) {
				File f;
				Integer i = vm.getId();
				Cloudlet tmpCloudlet = cloudletList.get(i);
				f = new File("/home/hieu/random/" + i.toString());
				if (!f.exists()) {
					f.createNewFile();
				}
				FileWriter fw = new FileWriter(f.getAbsolutePath());
				BufferedWriter bw = new BufferedWriter(fw);

				UtilizationModelStochastic tmpCloudletUtilizationModelStochastic = (UtilizationModelStochastic) tmpCloudlet
						.getUtilizationModelCpu();
				for (int scheduleInterval = 0; scheduleInterval < RandomConstantsTp.SIMULATION_PERIOD; scheduleInterval++) {
					Double tmpUtilization = tmpCloudletUtilizationModelStochastic
							.getUtilization(scheduleInterval * 300);
					bw.write(tmpUtilization.toString());
					bw.newLine();
				}
				bw.close();
			}
			
			
			
			CloudSim.terminateSimulation(RandomConstantsTp.SIMULATION_LIMIT);

			TreeDbServer.start();
			HelperTp.updateTrafficMap(vmList,
					RandomConstantsTp.trafficMatrixFile, 0);
			HelperTp.updateVmTrafficTree(vmList,
					RandomConstantsTp.trafficMatrixFile, 0);

			double lastClock = CloudSim.startSimulation();

			List<Cloudlet> newList = tpBroker.getCloudletReceivedList();
			Log.printLine("Received " + newList.size() + " cloudlets");

			CloudSim.stopSimulation();

			HelperTp.printResults(datacenter, vmList, lastClock,
					experimentName, Constants.OUTPUT_CSV, outputFolder);

		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
			System.exit(0);
		}

		Log.printLine("Finished " + experimentName);
	}

	@Override
	protected VmAllocationPolicy getVmAllocationPolicy(
			String vmAllocationPolicyName, String vmSelectionPolicyName,
			String parameterName) {
		VmAllocationPolicy vmAllocationPolicy = null;
		PowerVmSelectionPolicy vmSelectionPolicy = null;
		if (!vmSelectionPolicyName.isEmpty()) {
			vmSelectionPolicy = getVmSelectionPolicy(vmSelectionPolicyName);
		}
		double parameter = 0;
		if (!parameterName.isEmpty()) {
			parameter = Double.valueOf(parameterName);
		}
		PowerVmAllocationPolicyMigrationAbstract fallbackVmSelectionPolicy = new PowerVmAllocationPolicyMigrationStaticThreshold(
				hostList, vmSelectionPolicy, 0.7);
		if (vmAllocationPolicyName.equals("iqr")) {
			vmAllocationPolicy = new PowerVmAllocationPolicyMigrationInterQuartileRange(
					hostList, vmSelectionPolicy, parameter,
					fallbackVmSelectionPolicy);
		} else if (vmAllocationPolicyName.equals("mad")) {
			vmAllocationPolicy = new PowerVmAllocationPolicyMigrationMedianAbsoluteDeviation(
					hostList, vmSelectionPolicy, parameter,
					fallbackVmSelectionPolicy);
		} else if (vmAllocationPolicyName.equals("lr")) {
			vmAllocationPolicy = new PowerVmAllocationPolicyMigrationLocalRegression(
					hostList, vmSelectionPolicy, parameter,
					Constants.SCHEDULING_INTERVAL, fallbackVmSelectionPolicy);
		} else if (vmAllocationPolicyName.equals("tpLr")) {
			vmAllocationPolicy = new TpPowerVmAllocationPolicyMigrationLocalRegression(
					hostList, vmSelectionPolicy, parameter,
					Constants.SCHEDULING_INTERVAL, fallbackVmSelectionPolicy);

		} else if (vmAllocationPolicyName.equals("lrr")) {
			vmAllocationPolicy = new PowerVmAllocationPolicyMigrationLocalRegressionRobust(
					hostList, vmSelectionPolicy, parameter,
					Constants.SCHEDULING_INTERVAL, fallbackVmSelectionPolicy);
		} else if (vmAllocationPolicyName.equals("thr")) {
			vmAllocationPolicy = new PowerVmAllocationPolicyMigrationStaticThreshold(
					hostList, vmSelectionPolicy, parameter);
		} else if (vmAllocationPolicyName.equals("dvfs")) {
			vmAllocationPolicy = new PowerVmAllocationPolicySimple(hostList);
		} else {
			System.out.println("Unknown VM allocation policy: "
					+ vmAllocationPolicyName);
			System.exit(0);
		}
		return vmAllocationPolicy;
	}

	@Override
	protected PowerVmSelectionPolicy getVmSelectionPolicy(
			String vmSelectionPolicyName) {
		PowerVmSelectionPolicy vmSelectionPolicy = null;
		if (vmSelectionPolicyName.equals("mc")) {
			vmSelectionPolicy = new PowerVmSelectionPolicyMaximumCorrelation(
					new PowerVmSelectionPolicyMinimumMigrationTime());
		} else if (vmSelectionPolicyName.equals("mmt")) {
			vmSelectionPolicy = new PowerVmSelectionPolicyMinimumMigrationTime();
		} else if (vmSelectionPolicyName.equals("mu")) {
			vmSelectionPolicy = new PowerVmSelectionPolicyMinimumUtilization();
		} else if (vmSelectionPolicyName.equals("rs")) {
			vmSelectionPolicy = new PowerVmSelectionPolicyRandomSelection();
		} else if (vmSelectionPolicyName.equals("maxTraffic")) {
			vmSelectionPolicy = new PowerVmSelectionPolicyMaximumTraffic();
		} else {
			System.out.println("Unknown VM selection policy: "
					+ vmSelectionPolicyName);
			System.exit(0);
		}
		return vmSelectionPolicy;
	}
}
