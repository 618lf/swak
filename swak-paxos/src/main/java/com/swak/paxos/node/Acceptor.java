package com.swak.paxos.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.config.Config;
import com.swak.paxos.enums.PaxosMessageType;
import com.swak.paxos.protol.PaxosMessage;

/**
 * 接收议案
 * 
 * @author lifeng
 * @date 2020年12月28日 上午10:54:03
 */
public class Acceptor extends Role {

	private final Logger logger = LoggerFactory.getLogger(Acceptor.class);
	private AcceptorState acceptorState;

	public Acceptor(Config config) {
		super(config);
		this.acceptorState = new AcceptorState();
	}

	/**
	 * 准备阶段
	 * 
	 * @param paxosMsg
	 * @return
	 */
	public int onPrepare(PaxosMessage paxosMsg) {

		// 创建回复消息
		PaxosMessage replyPaxosMsg = new PaxosMessage();
		replyPaxosMsg.setInstanceID(getInstanceID());
		replyPaxosMsg.setNodeID(this.config.getMyNodeID());
		replyPaxosMsg.setProposalID(paxosMsg.getProposalID());
		replyPaxosMsg.setMsgType(PaxosMessageType.paxosPrepareReply.getValue());

		BallotNumber ballot = new BallotNumber(paxosMsg.getProposalID(), paxosMsg.getNodeID());
		BallotNumber pbn = this.acceptorState.getPromiseBallot();

		// 如果消息中提案ID比本地的大或者节点id比本地大
		if (ballot.ge(pbn)) {
			int ret = updateAcceptorState4Prepare(replyPaxosMsg, ballot);
			if (ret != 0)
				return ret;
		} else {
			replyPaxosMsg.setRejectByPromiseID(this.acceptorState.getPromiseBallot().getProposalID());
		}
	}

	private int updateAcceptorState4Prepare(PaxosMessage replyPaxosMsg, BallotNumber ballot) {
		replyPaxosMsg.setPreAcceptID(this.acceptorState.getAcceptedBallot().getProposalID());
		replyPaxosMsg.setPreAcceptNodeID(this.acceptorState.getAcceptedBallot().getNodeId());

		if (this.acceptorState.getAcceptedBallot().getProposalID() > 0) {
			replyPaxosMsg.setValue(this.acceptorState.getAcceptedValue());
		}

		this.acceptorState.setPromiseBallot(ballot);

		int ret = this.acceptorState.persist(getInstanceID(), getLastChecksum());
		if (ret != 0) {
			return -1;
		}
		return 0;
	}
}
