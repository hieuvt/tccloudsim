package kr.re.kisti.hieuvt.core;

import java.util.List;

import kr.kisti.re.hieuvt.tree.Node;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHostUtilizationHistory;
import org.cloudbus.cloudsim.power.models.PowerModel;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

public class TpHostUtilizationHistory extends PowerHostUtilizationHistory {

	private Node<TpSw> tpSwNode;

	public TpHostUtilizationHistory(int id, RamProvisioner ramProvisioner,
			BwProvisioner bwProvisioner, long storage,
			List<? extends Pe> peList, VmScheduler vmScheduler,
			PowerModel powerModel, Node<TpSw> tpSwNode) {
		super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler,
				powerModel);
		// TODO Auto-generated constructor stub
		this.setEdgeSwNode(tpSwNode);
	}

	@Override
	public double updateVmsProcessing(double currentTime) {
		double smallerTime = Double.MAX_VALUE;

		for (Vm vm : getVmList()) {
			double time = vm.updateVmProcessing(currentTime, getVmScheduler()
					.getAllocatedMipsForVm(vm));
			if (time > 0.0 && time < smallerTime) {
				smallerTime = time;
			}
		}

		setPreviousUtilizationMips(getUtilizationMips());
		setUtilizationMips(0);
		double hostTotalRequestedMips = 0;

		Log.printLine("-----------------------");
		for (Vm vm : getVmList()) {
			getVmScheduler().deallocatePesForVm(vm);
		}

		for (Vm vm : getVmList()) {
			getVmScheduler().allocatePesForVm(vm, vm.getCurrentRequestedMips());
		}

		for (Vm vm : getVmList()) {
			double totalRequestedMips = vm.getCurrentRequestedTotalMips();
			double totalAllocatedMips = getVmScheduler()
					.getTotalAllocatedMipsForVm(vm);

			if (!Log.isDisabled()) {
				Log.formatLine(
						"%.2f: [Host #"
								+ getId()
								+ "] Total allocated MIPS for VM #"
								+ vm.getId()
								+ " (Host #"
								+ vm.getHost().getId()
								+ ") is %.2f, was requested %.2f out of total %.2f (%.2f%%)",
						CloudSim.clock(), totalAllocatedMips,
						totalRequestedMips, vm.getMips(), totalRequestedMips
								/ vm.getMips() * 100);

				List<Pe> pes = getVmScheduler().getPesAllocatedForVM(vm);
				StringBuilder pesString = new StringBuilder();
				for (Pe pe : pes) {
					pesString.append(String.format(" PE #" + pe.getId()
							+ ": %.2f.", pe.getPeProvisioner()
							.getTotalAllocatedMipsForVm(vm)));
				}
				Log.formatLine("%.2f: [Host #" + getId() + "] MIPS for VM #"
						+ vm.getId() + " by PEs (" + getNumberOfPes() + " * "
						+ getVmScheduler().getPeCapacity() + ")." + pesString,
						CloudSim.clock());

			}

			if (getVmsMigratingIn().contains(vm)) {
				Log.formatLine(
						"%.2f: [Host #" + getId() + "] VM #" + vm.getId()
								+ " is being migrated to Host #" + getId(),
						CloudSim.clock());
			} else {
				Log.formatLine("%.2f: [Host #" + getId()
						+ "] Traffic from VM #" + vm.getId() + ": "
						+ ((TpVm) vm).calTrafficToOthers(), CloudSim.clock());
				
				Log.formatLine("%.2f: [Host #" + getId()
						+ "] Traffic cost from VM #" + vm.getId() + ": "
						+ ((TpVm) vm).calTrafficCostToOthers(), CloudSim.clock());
				
			
				
				if (totalAllocatedMips + 0.1 < totalRequestedMips) {
					Log.formatLine("%.2f: [Host #" + getId()
							+ "] Under allocated MIPS for VM #" + vm.getId()
							+ ": %.2f", CloudSim.clock(), totalRequestedMips
							- totalAllocatedMips);
				}

				vm.addStateHistoryEntry(currentTime, totalAllocatedMips,
						totalRequestedMips,
						(vm.isInMigration() && !getVmsMigratingIn()
								.contains(vm)));

				if (vm.isInMigration()) {
					Log.formatLine(
							"%.2f: [Host #" + getId() + "] VM #" + vm.getId()
									+ " is in migration", CloudSim.clock());
					totalAllocatedMips /= 0.9; // performance degradation due to
												// migration - 10%
				}
			}

			setUtilizationMips(getUtilizationMips() + totalAllocatedMips);
			hostTotalRequestedMips += totalRequestedMips;
		}

		addStateHistoryEntry(currentTime, getUtilizationMips(),
				hostTotalRequestedMips, (getUtilizationMips() > 0));

		return smallerTime;
	}

	// calculate traffic for all VMs running on the PM during a time frame
	public double calTrafficCostTimeFrame(double timeDiff) {
		double trafficCost = 0;
		for (Vm vm: getVmList()){
			// cal traffic only after migration
			if(!getVmsMigratingIn().contains(vm)){
				trafficCost += ((TpVm)vm).calTrafficCostToOthers() * timeDiff;
			}
		}
		return trafficCost;
	}
	
	public Node<TpSw> getEdgeSwNode() {
		return tpSwNode;
	}

	public void setEdgeSwNode(Node<TpSw> tpSwNode) {
		this.tpSwNode = tpSwNode;
	}

}
