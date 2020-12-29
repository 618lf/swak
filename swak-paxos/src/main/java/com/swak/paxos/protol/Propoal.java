package com.swak.paxos.protol;

/**
 * 一个提案
 * 
 * @author lifeng
 * @date 2020年12月29日 下午5:04:35
 */
public class Propoal {
	private int group;
	private byte[] value;

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