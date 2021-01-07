package com.swak.paxos.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 分组
 * 
 * @author lifeng
 * @date 2020年12月28日 上午10:49:33
 */
@Getter
@Setter
@Accessors(chain = true)
public class Group {

	private final Logger logger = LoggerFactory.getLogger(Group.class);
	private Instance instance;

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}
}