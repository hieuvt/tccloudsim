package kr.re.kisti.hieuvt.core;

import java.util.List;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.power.PowerDatacenterBroker;

public class TpDatacenterBroker extends PowerDatacenterBroker {

	public TpDatacenterBroker(String name) throws Exception {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create the virtual machines in a datacenter.
	 * 
	 * @param datacenterId
	 *            Id of the chosen PowerDatacenter
	 * @pre $none
	 * @post $none
	 */
	@Override
	protected void createVmsInDatacenter(int datacenterId) {
super.createVmsInDatacenter(datacenterId);
//		int requestedVms = 0;
//		String datacenterName = CloudSim.getEntityName(datacenterId);
//		List<Vm> arrangedVmList = HelperTp.updateVmTrafficTree(getVmList(),
//				RandomConstantsTp.trafficMatrixFile);
//		super.createVmsInDatacenter(datacenterId);
//		
//		
//		for (Vm vm : arrangedVmList) {
//			if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
//				Log.printLine(CloudSim.clock() + ": " + getName()
//						+ ": Trying to Create VM #" + vm.getId() + " in "
//						+ datacenterName);
//				sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
//				requestedVms++;
//			}
//		}
//
//		getDatacenterRequestedIdsList().add(datacenterId);
//
//		setVmsRequested(requestedVms);
//		setVmsAcks(0);

	}
}
