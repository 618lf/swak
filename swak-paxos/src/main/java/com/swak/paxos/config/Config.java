package com.swak.paxos.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 系统全局的配置
 * 
 * @author DELL
 */
@Getter
@Setter
@Accessors(chain = true)
public class Config {

	private long myNodeID;
	private int myGroupIdx;
	private int groupCount;
	private boolean isIMFollower;
	private int prepareTimeout = 1000;
	private int acceptTimeout = 1000;
	private int askForLearnTimeout = 3000;
	private int learnerSendSpeed;
}
