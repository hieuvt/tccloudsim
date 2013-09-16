package kr.re.kisti.hieuvt.core;

import org.cloudbus.cloudsim.UtilizationModelStochastic;

public class UtilizationModelStochasticNetworkIntensive extends
		UtilizationModelStochastic {

	public UtilizationModelStochasticNetworkIntensive (){
		super();
	}
	
	public UtilizationModelStochasticNetworkIntensive (long seed){
		super(seed);
	}
	
	@Override
	public double getUtilization(double time){
		if (getHistory().containsKey(time)) {
			return getHistory().get(time);
		}

		double utilization = getRandomGenerator().nextDouble() / 3;
		getHistory().put(time, utilization);
		return utilization;
	}
}
