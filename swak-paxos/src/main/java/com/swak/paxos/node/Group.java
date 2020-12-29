package com.swak.paxos.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.master.Master;

/**
 * 分组
 * 
 * @author lifeng
 * @date 2020年12月28日 上午10:49:33
 */
public class Group {

	private final Logger logger = LoggerFactory.getLogger(Group.class);
	private Instance instance;
	private Master master;

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	public Master getMaster() {
		return master;
	}

	public void setMaster(Master master) {
		this.master = master;
	}

	public Logger getLogger() {
		return logger;
	}
}