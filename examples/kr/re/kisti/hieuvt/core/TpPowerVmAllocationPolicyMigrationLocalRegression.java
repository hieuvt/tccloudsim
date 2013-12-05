package kr.re.kisti.hieuvt.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kr.re.kisti.hieuvt.tree.HelperTreeDb;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationAbstract;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationLocalRegression;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;

public class TpPowerVmAllocationPolicyMigrationLocalRegression extends
		PowerVmAllocationPolicyMigrationLocalRegression {

	public TpPowerVmAllocationPolicyMigrationLocalRegression(
			List<? extends Host> hostList,
			PowerVmSelectionPolicy vmSelectionPolicy, double safetyParameter,
			double schedulingInterval,
			PowerVmAllocationPolicyMigrationAbstract fallbackVmAllocationPolicy) {
		super(hostList, vmSelectionPolicy, safetyParameter, schedulingInterval,
				fallbackVmAllocationPolicy);
		// TODO Auto-generated constructor stub
		// allocate cluster?
	}

	@Override
	public PowerHost findHostForVm(Vm vm, Set<? extends Host> excludedHosts) {
		// return super.findHostForVm(vm, excludedHosts);
		if (CloudSim.clock() < 1) {
			return findHostSimple((TpVm) vm);
		} else if (RandomConstantsTp.POLICY == 0){	
				return findHostTrafficEnergy((TpVm) vm, excludedHosts);
		} else if (RandomConstantsTp.POLICY == 1){
			return findHostTraffic((TpVm) vm, excludedHosts);
		} else {
			return super.findHostForVm(vm, excludedHosts);
		}
	}

	private PowerHost findHostSimple(TpVm vm) {
		System.out.println("VM: " + vm.getId());
		for (Host host : getHostList()) {
			if (host.isSuitableForVm(vm)) {
				return (PowerHost) host;
			}
		}
		return null;
	}

	private PowerHost findHostTrafficEnergy(TpVm vm,
			Set<? extends Host> excludedHosts) {
		System.out.println("VM: " + vm.getId());
		List<PowerHost> siblingHostList = getSiblingHosts(vm);
		int totalDistance = 0;
		int minDistance = Integer.MAX_VALUE;
		double minPower = Double.MAX_VALUE;
		HelperTp helperTp = new HelperTp();
		PowerHost allocatedHost = null;
		PowerHost currentHost = null;

		currentHost = (PowerHost) vm.getHost();

		for (PowerHost host : this.<PowerHost> getHostList()) {
			if (excludedHosts.contains(host)) {
				continue;
			}
			if (host.isSuitableForVm(vm)) {
				if (getUtilizationOfCpuMips(host) != 0
						&& isHostOverUtilizedAfterAllocation(host, vm)) {
					continue;
				}
				try {
					double powerAfterAllocation = getPowerAfterAllocation(host,
							vm);
					if (powerAfterAllocation != -1) {
						if (currentHost == null || siblingHostList.size() == 0) {
							System.out.println("option 1");
							double powerDiff = powerAfterAllocation
									- host.getPower();
							if (powerDiff < minPower) {
								minPower = powerDiff;
								allocatedHost = host;
							}
						} else {
							System.out.println("option 2");
							for (Host siblingHost : siblingHostList) {
								int distance = helperTp
										.calculateNodeDistance(
												((TpHostUtilizationHistory) siblingHost)
														.getEdgeSwNode(),
												((TpHostUtilizationHistory) host)
														.getEdgeSwNode());
								totalDistance += distance;
							}

							if (totalDistance < minDistance) {
								minDistance = totalDistance;
								allocatedHost = host;
							}
						}

					}
				} catch (Exception e) {
				}
			}
		}
		if (allocatedHost != null) {
			System.out.println("Allocated host " + allocatedHost.getId());
		}
		return allocatedHost;
	}

	private PowerHost findHostTraffic(Vm vm, Set<? extends Host> excludedHosts) {
		System.out.println("VM: " + vm.getId());
		List<PowerHost> siblingHostList = getSiblingHosts(vm);
		int totalDistance = 0;
		int minDistance = Integer.MAX_VALUE;
		double minPower = Double.MAX_VALUE;
		HelperTp helperTp = new HelperTp();
		PowerHost allocatedHost = null;

		for (PowerHost host : this.<PowerHost> getHostList()) {
			if (excludedHosts.contains(host)) {
				continue;
			}
			if (host.isSuitableForVm(vm)) {
				if (getUtilizationOfCpuMips(host) != 0
						&& isHostOverUtilizedAfterAllocation(host, vm)) {
					continue;
				}
				try {
					double powerAfterAllocation = getPowerAfterAllocation(host,
							vm);
					if (powerAfterAllocation != -1) {
						if (siblingHostList.size() == 0) {
							System.out.println("option 1");
							double powerDiff = powerAfterAllocation
									- host.getPower();
							if (powerDiff < minPower) {
								minPower = powerDiff;
								allocatedHost = host;
							}
						} else {
							System.out.println("option 2");
							for (Host siblingHost : siblingHostList) {
								int distance = helperTp
										.calculateNodeDistance(
												((TpHostUtilizationHistory) siblingHost)
														.getEdgeSwNode(),
												((TpHostUtilizationHistory) host)
														.getEdgeSwNode());
								totalDistance += distance;
							}

							if (totalDistance < minDistance) {
								minDistance = totalDistance;
								allocatedHost = host;
							}
						}

					}
				} catch (Exception e) {
				}
			}
		}
		if (allocatedHost != null) {
			System.out.println("Allocated host " + allocatedHost.getId());
		}
		return allocatedHost;
	}

	private List<PowerHost> getSiblingHosts(Vm vm) {
		double currentTime = CloudSim.clock();
		int seqId = 0;

		if (currentTime > 3600) {
			seqId = (int) Math.floor(currentTime / 3600);
		}

		String tableName = "graphTable" + ((Integer) seqId).toString();
		String itemName = "graph";
		List<PowerHost> siblingHostList = new ArrayList<PowerHost>();
		HelperTreeDb helperTreeDb = new HelperTreeDb(RandomConstantsTp.dbName);

		List<String> siblingNames = helperTreeDb.findSiblingLeaves(tableName,
				itemName, new Integer(vm.getId()).toString() + " ");

		Datacenter dataCenter = getHostList().get(0).getDatacenter();
		if (dataCenter.getVmList().size() == RandomConstantsTp.NUMBER_OF_VMS) {
			System.out.println("Vm List Size " + dataCenter.getVmList().size());
			if (siblingNames != null) {
				for (String siblingName : siblingNames) {
					siblingName = siblingName.substring(0,
							siblingName.length() - 1);
					int siblingId = Integer.parseInt(siblingName);
					System.out.println("Sibling " + siblingId);
					Vm siblingVm = dataCenter.getVmList().get(siblingId);
					if (siblingVm.getHost() != null) {
						siblingHostList.add((PowerHost) siblingVm.getHost());
					}
				}
			}
		}
		helperTreeDb.close();
		return siblingHostList;

	}
}
