package com.swak.paxos.node;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.config.Config;

/**
 * 统计： 提案，接受过程中的统计
 * 
 * @author lifeng
 * @date 2020年12月28日 下午12:47:42
 */
public class ProposerStat {

	private static final Logger logger = LoggerFactory.getLogger(ProposerStat.class);
	public Config config;
	public Set<Long> receiveMsgNodeID = new HashSet<Long>();
	public Set<Long> rejectMsgNodeID = new HashSet<Long>();
	public Set<Long> promiseOrAcceptMsgNodeID = new HashSet<Long>();

	public ProposerStat(Config config) {
		this.config = config;
		this.startNewRound();
	}

	public void addReceive(Long nodeID) {
		if (!this.receiveMsgNodeID.contains(nodeID)) {
			this.receiveMsgNodeID.add(nodeID);
		}
	}

	public void addReject(long nodeID) {
		if (!this.rejectMsgNodeID.contains(nodeID)) {
			this.rejectMsgNodeID.add(nodeID);
		}
	}

	public void addPromiseOrAccept(long nodeID) {
		if (!this.promiseOrAcceptMsgNodeID.contains(nodeID)) {
			this.promiseOrAcceptMsgNodeID.add(nodeID);
		}
	}

	public boolean isPassedOnThisRound() {
		logger.debug("passedOn size {}.", this.promiseOrAcceptMsgNodeID.size());
		return this.promiseOrAcceptMsgNodeID.size() >= this.config.getMajorityCount();
	}

	public boolean isRejectedOnThisRound() {
		return this.rejectMsgNodeID.size() >= this.config.getMajorityCount();
	}

	public boolean isAllReceiveOnThisRound() {
		return this.receiveMsgNodeID.size() == this.config.getNodeCount();
	}

	public void startNewRound() {
		if (this.receiveMsgNodeID != null) {
			this.receiveMsgNodeID.clear();
		}
		if (this.rejectMsgNodeID != null) {
			this.rejectMsgNodeID.clear();
		}
		if (this.promiseOrAcceptMsgNodeID != null) {
			this.promiseOrAcceptMsgNodeID.clear();
		}
	}
}
