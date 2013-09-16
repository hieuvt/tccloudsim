package kr.re.kisti.hieuvt.core;

import java.util.List;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVm;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;

public class PowerVmSelectionPolicyMaximumTraffic extends
		PowerVmSelectionPolicy {

	@Override
	public Vm getVmToMigrate(PowerHost host) {
		// TODO Auto-generated method stub
		List<PowerVm> migratableVms = getMigratableVms(host);
		if (migratableVms.isEmpty()) {
			return null;
		}
		Vm vmToMigrate = null;
		double minTrafficCost = Double.MIN_VALUE;
		for (Vm vm : migratableVms) {
			if (vm.isInMigration()) {
				continue;
			}
			double trafficCost = ((TpVm)vm).calTrafficCostToOthers();
			if (minTrafficCost > trafficCost) {
				minTrafficCost = trafficCost;
				vmToMigrate = vm;
			}
		}
		return vmToMigrate;
	}

}
