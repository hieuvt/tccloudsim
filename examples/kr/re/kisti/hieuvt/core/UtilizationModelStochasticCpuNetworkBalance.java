package kr.re.kisti.hieuvt.core;

import org.cloudbus.cloudsim.UtilizationModelStochastic;

public class UtilizationModelStochasticCpuNetworkBalance extends
		UtilizationModelStochastic {
	
	public UtilizationModelStochasticCpuNetworkBalance() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	public UtilizationModelStochasticCpuNetworkBalance (long seed){
		super(seed);
	}
	
	@Override
	public double getUtilization(double time){
		if (getHistory().containsKey(time)) {
			return getHistory().get(time);
		}

		double utilization = getRandomGenerator().nextDouble() / 3;
		utilization = RandomConstantsTp.CPU_NETWORK_BALANCE_MIN_UTILIZATION + utilization;
		getHistory().put(time, utilization);
		return utilization;
	}
}
