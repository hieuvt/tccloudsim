package kr.re.kisti.hieuvt.core;

import org.cloudbus.cloudsim.UtilizationModelStochastic;

public class UtilizationModelStochasticCpuIntensive extends
		UtilizationModelStochastic {
	
	public UtilizationModelStochasticCpuIntensive (){
		super();
	}
	
	public UtilizationModelStochasticCpuIntensive (long seed){
		super(seed);
	}
	@Override
	public double getUtilization(double time){
		if (getHistory().containsKey(time)) {
			return getHistory().get(time);
		}

		double utilization = getRandomGenerator().nextDouble() / 3;
		utilization = RandomConstantsTp.CPU_INTENSIVE_MIN_UTILIZATION + utilization;
		getHistory().put(time, utilization);
		return utilization;
	}

}
