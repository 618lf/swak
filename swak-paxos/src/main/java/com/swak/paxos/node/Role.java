package com.swak.paxos.node;

import com.swak.paxos.config.Config;

/**
 * 基本的角色
 * 
 * @author lifeng
 * @date 2020年12月28日 上午10:52:31
 */
public abstract class Role {
	protected Config config;
	protected long instanceID;
	protected Instance instance;

	public Role(Config config) {
		this.config = config;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public long getInstanceID() {
		return instanceID;
	}

	public void setInstanceID(long instanceID) {
		this.instanceID = instanceID;
	}

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}
}
