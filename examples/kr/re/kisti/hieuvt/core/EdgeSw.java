package kr.re.kisti.hieuvt.core;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.power.PowerHost;

public class EdgeSw extends TpSw {

	private List<PowerHost> tpHostList;
	
	public EdgeSw(int level, int numPort) {
		super(level, numPort);
		// TODO Auto-generated constructor stub
		setTpHostList(new ArrayList<PowerHost>());
	}
	public List<PowerHost> getTpHostList() {
		return tpHostList;
	}
	public void setTpHostList(List<PowerHost> tpHostList) {
		this.tpHostList = tpHostList;
	}

}
