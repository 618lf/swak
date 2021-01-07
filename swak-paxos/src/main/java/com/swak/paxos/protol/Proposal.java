package com.swak.paxos.protol;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 提案
 * 
 * @author lifeng
 * @date 2020年12月29日 下午5:04:35
 */
@Getter
@Setter
@Accessors(chain = true)
public class Proposal {
	private int msgType;
	private long instanceID;
	private long nodeID;
	private long proposalID;
	private long proposalNodeID;
	private long preAcceptID;
	private long preAcceptNodeID;
	private long rejectByPromiseID;
	private long nowInstanceID;
	private long minChosenInstanceID;
	private int lastChecksum;
	private int flag;
	private long timestamp;
	private byte[] systemVariables;
	private byte[] masterVariables;
	private byte[] value;
	private long timeoutMs;
}