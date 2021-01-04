package com.swak.paxos.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.config.Config;
import com.swak.paxos.enums.ProposalType;
import com.swak.paxos.protol.Proposal;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 接收议案
 * 
 * @author lifeng
 * @date 2020年12月28日 上午10:54:03
 */
@Getter
@Setter
@Accessors(chain = true)
public class Acceptor {

	private final Logger logger = LoggerFactory.getLogger(Acceptor.class);
	private AcceptorState acceptorState;
	private long instanceID;
	private Instance instance;
	private Config config;

	public Acceptor(Config config) {
		this.acceptorState = new AcceptorState();
	}

	/**
	 * 准备阶段
	 * 
	 * @param paxosMsg
	 * @return
	 */
	public void onPrepare(Proposal paxosMsg) {

		// 创建回复消息
		Proposal replyPaxosMsg = new Proposal();
		replyPaxosMsg.setInstanceID(getInstanceID());
		replyPaxosMsg.setNodeID(this.config.getMyNodeID());
		replyPaxosMsg.setProposalID(paxosMsg.getProposalID());
		replyPaxosMsg.setMsgType(ProposalType.paxosPrepareReply.getValue());

		BallotNumber ballot = new BallotNumber(paxosMsg.getProposalID(), paxosMsg.getNodeID());
		BallotNumber pbn = this.acceptorState.getPromiseBallot();

		// 如果消息中提案ID比本地的大或者节点id比本地大
		if (ballot.ge(pbn)) {
			int ret = updateAcceptorState4Prepare(replyPaxosMsg, ballot);
			if (ret != 0)
				return;
		} else {
			replyPaxosMsg.setRejectByPromiseID(this.acceptorState.getPromiseBallot().getProposalID());
		}

		// 给定点的node发送回复信息
		// long replyNodeId = paxosMsg.getNodeID();
		// sendMessage(replyNodeId, replyPaxosMsg);
	}

	private int updateAcceptorState4Prepare(Proposal replyPaxosMsg, BallotNumber ballot) {
		replyPaxosMsg.setPreAcceptID(this.acceptorState.getAcceptedBallot().getProposalID());
		replyPaxosMsg.setPreAcceptNodeID(this.acceptorState.getAcceptedBallot().getNodeId());

		if (this.acceptorState.getAcceptedBallot().getProposalID() > 0) {
			replyPaxosMsg.setValue(this.acceptorState.getAcceptedValue());
		}

		this.acceptorState.setPromiseBallot(ballot);

		int ret = this.acceptorState.persist(getInstanceID(), 0);// getLastChecksum());
		if (ret != 0) {
			return -1;
		}
		return 0;
	}

	public void onAccept(Proposal paxosMsg) {
		Proposal replyPaxosMsg = new Proposal();
		replyPaxosMsg.setInstanceID(getInstanceID());
		replyPaxosMsg.setNodeID(this.config.getMyNodeID());
		replyPaxosMsg.setProposalID(paxosMsg.getProposalID());
		replyPaxosMsg.setMsgType(ProposalType.paxosAcceptReply.getValue());

		BallotNumber ballot = new BallotNumber(paxosMsg.getProposalID(), paxosMsg.getNodeID());
		BallotNumber promiseBallot = this.acceptorState.getPromiseBallot();
		if (ballot.ge(promiseBallot)) {
			this.acceptorState.setPromiseBallot(ballot);
			BallotNumber acceptedBallot = new BallotNumber(ballot.getProposalID(), ballot.getNodeId());
			this.acceptorState.setAcceptedBallot(acceptedBallot);
			this.acceptorState.setAcceptedValue(paxosMsg.getValue());
			updateAcceptorState4Accept(replyPaxosMsg);
		} else {
			replyPaxosMsg.setRejectByPromiseID(this.acceptorState.getPromiseBallot().getProposalID());
		}

		// 定点回复消息
		// long replyNodeId = paxosMsg.getNodeID();
		// sendMessage(replyNodeId, replyPaxosMsg);
	}

	private void updateAcceptorState4Accept(Proposal replyPaxosMsg) {
		int ret = this.acceptorState.persist(getInstanceID(), 0);// getLastChecksum());
		if (ret != 0) {
			return;
		}
	}
}
