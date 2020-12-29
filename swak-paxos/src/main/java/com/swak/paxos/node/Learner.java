package com.swak.paxos.node;

import com.swak.paxos.config.Config;
import com.swak.paxos.enums.PaxosMessageType;
import com.swak.paxos.protol.PaxosMessage;

/**
 * 学习议案
 * 
 * @author lifeng
 * @date 2020年12月28日 上午10:55:32
 */
public class Learner extends Role {

	public Learner(Config config) {
		super(config);
	}

	public void proposerSendSuccess(long learnInstanceID, long proposalID) {
		PaxosMessage msg = new PaxosMessage();
		msg.setMsgType(PaxosMessageType.paxosLearnerProposerSendSuccess.getValue());
		msg.setInstanceID(learnInstanceID);
		msg.setNodeID(this.config.getMyNodeID());
		msg.setProposalID(proposalID);
		msg.setLastChecksum(getLastChecksum());

		// broadcastMessage(msg,
		// BroadcastMessageType.BroadcastMessage_Type_RunSelf_First.getType(),
		// MessageSendType.UDP.getValue());
	}
}