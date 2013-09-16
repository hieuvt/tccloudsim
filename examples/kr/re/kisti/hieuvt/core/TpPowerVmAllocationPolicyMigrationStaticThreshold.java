package kr.re.kisti.hieuvt.core;

import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationStaticThreshold;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;

public class TpPowerVmAllocationPolicyMigrationStaticThreshold extends
		PowerVmAllocationPolicyMigrationStaticThreshold {

	public TpPowerVmAllocationPolicyMigrationStaticThreshold(
			List<? extends Host> hostList,
			PowerVmSelectionPolicy vmSelectionPolicy,
			double utilizationThreshold) {
		super(hostList, vmSelectionPolicy, utilizationThreshold);
		// TODO Auto-generated constructor stub
	}

}
