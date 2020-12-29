package com.swak.paxos.protol;

/**
 * 提案参数
 * 
 * @author lifeng
 * @date 2020年12月28日 上午11:16:27
 */
public class ProposeParam {

	private int group;
	private byte[] value;

	public ProposeParam(int group, byte[] value) {
		this.group = group;
		this.value = value;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}
}
