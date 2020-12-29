package com.swak.paxos.protol;

/**
 * 提案
 * 
 * @author lifeng
 * @date 2020年12月28日 上午11:15:32
 */
public class ProposeResult {
	// ret, 0 if success
	private int result;
	// return when propose success
	private long instanceID = 0;

	public ProposeResult(int result, long instanceID) {
		super();
		this.result = result;
		this.instanceID = instanceID;
	}

	public ProposeResult(int result) {
		super();
		this.result = result;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public long getInstanceID() {
		return instanceID;
	}

	public void setInstanceID(long instanceID) {
		this.instanceID = instanceID;
	}
}
