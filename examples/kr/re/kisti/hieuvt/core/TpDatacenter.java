package kr.re.kisti.hieuvt.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModelStochastic;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.predicates.PredicateType;
import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerHost;

public class TpDatacenter extends PowerDatacenter {

	private double trafficCost;

	public TpDatacenter(String name, DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList,
			double schedulingInterval) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList,
				schedulingInterval);
		// TODO Auto-generated constructor stub
		setTrafficCost(0);
	}

	@Override
	protected void updateCloudletProcessing() {
		if (getCloudletSubmitted() == -1
				|| getCloudletSubmitted() == CloudSim.clock()) {
			CloudSim.cancelAll(getId(), new PredicateType(
					CloudSimTags.VM_DATACENTER_EVENT));
			schedule(getId(), getSchedulingInterval(),
					CloudSimTags.VM_DATACENTER_EVENT);
			return;
		}
		double currentTime = CloudSim.clock();

		// if some time passed since last processing
		if (currentTime > getLastProcessTime()) {
			System.out.print(currentTime + " ");

			double minTime = updateCloudetProcessingWithoutSchedulingFutureEventsForce();

			if (!isDisableMigrations()) {
				List<Map<String, Object>> migrationMap = getVmAllocationPolicy()
						.optimizeAllocation(getVmList());

				if (migrationMap != null) {
					for (Map<String, Object> migrate : migrationMap) {
						Vm vm = (Vm) migrate.get("vm");
						TpHostUtilizationHistory targetHost = (TpHostUtilizationHistory) migrate
								.get("host");
						TpHostUtilizationHistory oldHost = (TpHostUtilizationHistory) vm
								.getHost();

						if (oldHost == null) {
							Log.formatLine(
									"%.2f: Migration of VM #%d to Host #%d is started",
									currentTime, vm.getId(), targetHost.getId());
						} else {
							Log.formatLine(
									"%.2f: Migration of VM #%d from Host #%d to Host #%d is started",
									currentTime, vm.getId(), oldHost.getId(),
									targetHost.getId());
						}

						targetHost.addMigratingInVm(vm);
						incrementMigrationCount();

						send(getId(),
								//calculateDelay(vm.getRam(), oldHost, targetHost),
								vm.getRam() / ((double) targetHost.getBw() / (2 * 8000)),
								CloudSimTags.VM_MIGRATE, migrate);
					}
				}
			}

			// schedules an event to the next time
			if (minTime != Double.MAX_VALUE) {
				CloudSim.cancelAll(getId(), new PredicateType(
						CloudSimTags.VM_DATACENTER_EVENT));
				send(getId(), getSchedulingInterval(),
						CloudSimTags.VM_DATACENTER_EVENT);
			}

			setLastProcessTime(currentTime);
		}
	}

	/**
	 * For migration between hosts connected to an edge switch delay = RAM /
	 * bandwidth we use BW / 2 to model BW available for migration purposes, the
	 * other half of BW is for VM communication around 16 seconds for 1024 MB
	 * using 1 Gbit/s network
	 * 
	 * For migration between hosts connected to different switches delay = delay
	 * * (1 + distance between two edge switches)
	 * 
	 * @param ram
	 *            VM ram
	 * @param oldHost
	 * @param targetHost
	 * @return
	 */
	private double calculateDelay(int ram, TpHostUtilizationHistory oldHost,
			TpHostUtilizationHistory targetHost) {
		// TODO Auto-generated method stub
		HelperTp helperTp = new HelperTp();
		double delay = ram / ((double) targetHost.getBw() / (2 * 8000));
		int distance = helperTp.calculateNodeDistance(oldHost.getEdgeSwNode(),
				targetHost.getEdgeSwNode());
		// System.out.println("delay " + delay + "distance " + distance);
		delay = delay * RandomConstantsTp.distanceCost.get(distance);
		return delay;
	}

	@Override
	protected double updateCloudetProcessingWithoutSchedulingFutureEventsForce() {
		double currentTime = CloudSim.clock();
		double minTime = Double.MAX_VALUE;
		double timeDiff = currentTime - getLastProcessTime();
		double timeFrameDatacenterEnergy = 0.0;

		double timeFrameDatacenterTrafficCost = 0.0;

		Log.printLine("\n\n--------------------------------------------------------------\n\n");
		Log.formatLine(
				"New resource usage for the time frame starting at %.2f:",
				currentTime);

		for (TpHostUtilizationHistory host : this
				.<TpHostUtilizationHistory> getHostList()) {
			Log.printLine();

			double time = host.updateVmsProcessing(currentTime); // inform VMs
																	// to update
																	// processing
			if (time < minTime) {
				minTime = time;
			}

			Log.formatLine("%.2f: [Host #%d] utilization is %.2f%%",
					currentTime, host.getId(), host.getUtilizationOfCpu() * 100);
		}

		if (timeDiff > 0) {
			Log.formatLine(
					"\nEnergy consumption for the last time frame from %.2f to %.2f:",
					getLastProcessTime(), currentTime);

			for (PowerHost host : this.<PowerHost> getHostList()) {
				double previousUtilizationOfCpu = host
						.getPreviousUtilizationOfCpu();
				double utilizationOfCpu = host.getUtilizationOfCpu();
				double timeFrameHostEnergy = host.getEnergyLinearInterpolation(
						previousUtilizationOfCpu, utilizationOfCpu, timeDiff);
				timeFrameDatacenterEnergy += timeFrameHostEnergy;

				// datacenter traffic cost
				double timeFrameHostTrafficCost = ((TpHostUtilizationHistory) host)
						.calTrafficCostTimeFrame(timeDiff);
				timeFrameDatacenterTrafficCost += timeFrameHostTrafficCost;

				Log.printLine();
				Log.formatLine(
						"%.2f: [Host #%d] utilization at %.2f was %.2f%%, now is %.2f%%",
						currentTime, host.getId(), getLastProcessTime(),
						previousUtilizationOfCpu * 100, utilizationOfCpu * 100);
				Log.formatLine("%.2f: [Host #%d] energy is %.2f W*sec",
						currentTime, host.getId(), timeFrameHostEnergy);

				Log.formatLine(
						"%.2f: [Host #%d] traffic cost is %.2f Mbps*distanceCost*sec",
						currentTime, host.getId(), timeFrameHostTrafficCost);

			}

			Log.formatLine("\n%.2f: Data center's energy is %.2f W*sec\n",
					currentTime, timeFrameDatacenterEnergy);

			Log.formatLine(
					"\n%.2f: Data center's traffic cost is %.2f Mpbs*distanceCost*sec\n",
					currentTime, timeFrameDatacenterTrafficCost);

			if ((Math.round(currentTime) % 3600) == 0) {
				int seqId = (int) Math.floor(currentTime / 3600);
//				System.out.println("SeqId = " + seqId);
				HelperTp.updateTrafficMap(getVmList(),
						RandomConstantsTp.trafficMatrixFile, seqId);
			 HelperTp.updateVmTrafficTree(getVmList(),
						RandomConstantsTp.trafficMatrixFile, seqId);
			}

		}

		setPower(getPower() + timeFrameDatacenterEnergy);

		setTrafficCost(getTrafficCost() + timeFrameDatacenterTrafficCost);

		checkCloudletCompletion();

		/** Remove completed VMs **/
		for (PowerHost host : this.<PowerHost> getHostList()) {
			for (Vm vm : host.getCompletedVms()) {
				getVmAllocationPolicy().deallocateHostForVm(vm);
				getVmList().remove(vm);
				Log.printLine("VM #" + vm.getId()
						+ " has been deallocated from host #" + host.getId());
			}
		}

		Log.printLine();

		setLastProcessTime(currentTime);
		return minTime;
	}

	public double getTrafficCost() {
		return trafficCost;
	}

	protected void setTrafficCost(double trafficCost) {
		this.trafficCost = trafficCost;
	}

}
