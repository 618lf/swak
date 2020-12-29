package com.swak.paxos.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.config.Config;
import com.swak.paxos.protol.BaseMsg;

/**
 * 基本的角色
 * 
 * @author lifeng
 * @date 2020年12月28日 上午10:52:31
 */
public abstract class Role {
	private static final Logger logger = LoggerFactory.getLogger(Instance.class);
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

	public int getLastChecksum() {
		return this.instance.getLastChecksum();
	}

	public static BaseMsg unPackBaseMsg(byte[] vBuffer) {
		BaseMsg baseMsg = null;
		try {
			baseMsg = BaseMsg.fromBytes(vBuffer);
		} catch (Exception e) {
			logger.error("unPackBaseMsg error.", e);
		}

		return baseMsg;
	}

}
