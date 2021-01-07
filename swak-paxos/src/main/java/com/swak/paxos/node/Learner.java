package com.swak.paxos.node;

import com.swak.paxos.config.Config;
import com.swak.paxos.enums.ProposalType;
import com.swak.paxos.protol.Proposal;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 学习议案
 * 
 * @author lifeng
 * @date 2020年12月28日 上午10:55:32
 */
@Getter
@Setter
@Accessors(chain = true)
public class Learner {

	private long instanceID;
	private Instance instance;
	private Config config;

	public Learner(Config config) {
	}

	public void askforLearn() {
		Proposal paxosMsg = new Proposal();
		paxosMsg.setInstanceID(getInstanceID());
		paxosMsg.setNodeID(this.config.getMyNodeID());
		paxosMsg.setMsgType(ProposalType.paxosLearnerAskforLearn.getValue());

		if (this.config.isIMFollower()) {
			// this is not proposal nodeid, just use this val to bring follow to nodeid
			// info.
			paxosMsg.setProposalNodeID(this.config.getFollowNodeID());
		}

		// 开始学习
//		broadcastMessage(paxosMsg, BroadcastMessageType.BroadcastMessage_Type_RunSelf_None.getType(),
//				MessageSendType.TCP.getValue());
//		broadcastMessageToTempNode(paxosMsg, MessageSendType.UDP.getValue());
	}
}