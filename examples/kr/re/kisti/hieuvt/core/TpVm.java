package kr.re.kisti.hieuvt.core;

import java.util.List;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVm;

public class TpVm extends PowerVm {
	
	private int updateTime = 0;

	private double[] trafficToOthers = new double[RandomConstantsTp.NUMBER_OF_VMS];
	private List<TpVm> connectedVm;
	private PowerHost origHost;

	public TpVm(int id, int userId, double mips, int pesNumber, int ram,
			long bw, long size, int priority, String vmm,
			CloudletScheduler cloudletScheduler, double schedulingInterval) {
		super(id, userId, mips, pesNumber, ram, bw, size, priority, vmm,
				cloudletScheduler, schedulingInterval);
	}

	public int rank() {
		int rank;
		if (calTrafficToOthers() > RandomConstantsTp.RANK_NETWORK_INTENSIVE) {
			rank = RandomConstantsTp.NETWORK_INTENSIVE;
		} else if (calTrafficToOthers() > RandomConstantsTp.RANK_CPU_NETWORK_BALANCE) {
			rank = RandomConstantsTp.CPU_NETWORK_BALANCE;
		} else {
			rank = RandomConstantsTp.CPU_INTENSIVE;
		}
		return rank;
	}

	public double calTrafficToOthers() {
		double totalTraffic = 0;
		for (int i = 0; i < RandomConstantsTp.NUMBER_OF_VMS; i++) {
			totalTraffic += getTrafficToOthers()[i];
		}
		return totalTraffic;
	}

	public double calTrafficCostToOthers() {
		HelperTp helperTp = new HelperTp();
		double tmpTrafficCostToOthers = 0;
		int distance;
		TpHostUtilizationHistory currentHost = (TpHostUtilizationHistory) this
				.getHost();

		for (int i = 0; i < getTrafficToOthers().length; i++) {
			TpHostUtilizationHistory remoteHost = (TpHostUtilizationHistory) this
					.getHost().getDatacenter().getVmList().get(i).getHost();
			if ((currentHost != null) && (remoteHost != null)) {
				if (currentHost.getId() == remoteHost.getId()) {
					tmpTrafficCostToOthers += 0;
				} else {
					distance = helperTp.calculateNodeDistance(
							currentHost.getEdgeSwNode(),
							remoteHost.getEdgeSwNode());
					tmpTrafficCostToOthers += getTrafficToOthers()[i]
							* RandomConstantsTp.distanceCost.get(distance);
				}
			}
		}
		return tmpTrafficCostToOthers;
	}

	public double[] getTrafficToOthers() {
		return trafficToOthers;
	}

	public void setTrafficToOthers(double[] trafficToOthers) {
//		System.out.println("Update time " + updateTime);
//		updateTime++;
		this.trafficToOthers = trafficToOthers;
//		double totalTraffic = 0;
//		for (int i = 0; i < RandomConstantsTp.NUMBER_OF_VMS; i++) {
//			totalTraffic += trafficToOthers[i];
//		}
//		System.out.println("Total Traffic " + totalTraffic);
	}

	public List<TpVm> getConnectedVm() {
		return connectedVm;
	}

	public void setConnectedVm(List<TpVm> connectedVm) {
		this.connectedVm = connectedVm;
	}

	public PowerHost getOrigHost() {
		return origHost;
	}

	public void setOrigHost(PowerHost origHost) {
		this.origHost = origHost;
	}
	
	@Override
	public double getMips(){
		if (this.isBeingInstantiated()){
			double maxMips = super.getMips();
			if (this.rank() == RandomConstantsTp.NETWORK_INTENSIVE){
				return (maxMips * 0.33);
			} else if (this.rank() == RandomConstantsTp.CPU_NETWORK_BALANCE){
				return (maxMips * 0.66);
			}
		}
		return super.getMips();
	}

}
