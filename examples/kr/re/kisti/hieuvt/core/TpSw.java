package kr.re.kisti.hieuvt.core;

public class TpSw {

	private int level;
	private int numPort;

	public TpSw(int level, int numPort) {
		this.level = level;
		this.numPort = numPort;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getNumPort() {
		return numPort;
	}

	public void setNumPort(int numPort) {
		this.numPort = numPort;
	}

}
