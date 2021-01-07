package com.swak.paxos.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.common.OtherUtils;
import com.swak.paxos.common.TimeStat;
import com.swak.paxos.config.Config;
import com.swak.paxos.enums.ProposalType;
import com.swak.paxos.enums.TimerType;
import com.swak.paxos.event.EventLoop;
import com.swak.paxos.protol.Proposal;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 议员
 * 
 * @author lifeng
 * @date 2020年12月28日 下午1:22:39
 */
@Getter
@Setter
@Accessors(chain = true)
public class Proposer {
	private final Logger logger = LoggerFactory.getLogger(Proposer.class);
	private long instanceID;
	private Instance instance;
	private Config config;
	private ProposerState proposerState;
	private ProposerStat proposerStat;
	private boolean isPreparing;
	private boolean isAccepting;
	private long prepareTimerID;
	private int lastPrepareTimeoutMs;
	private long acceptTimerID;
	private int lastAcceptTimeoutMs;
	private long timeoutInstanceID;
	private boolean canSkipPrepare;
	private boolean wasRejectBySomeone;
	private EventLoop eventLoop;
	private TimeStat timeStat = new TimeStat();
	private Learner learner;

	public Proposer(Config config, Instance instance) {
		this.config = config;
		this.instance = instance;
		this.isPreparing = false;
		this.isAccepting = false;
		this.canSkipPrepare = false;
		this.prepareTimerID = 0;
		this.acceptTimerID = 0;
		this.timeoutInstanceID = 0;
		this.lastPrepareTimeoutMs = config.getPrepareTimeout();
		this.lastAcceptTimeoutMs = config.getAcceptTimeout();
		this.wasRejectBySomeone = false;
	}

	public boolean isWorking() {
		return this.isPreparing || this.isAccepting;
	}

	/**
	 * 提案一个新值: 不能直接调用这个，必须通过队列来调用： 同时只能协调一个值
	 * 
	 * @param value
	 */
	public void propose(Proposal proposal) {

		// 设置一个新值
		if (this.proposerState.getValue().length == 0) {
			this.proposerState.setValue(proposal.getValue());
		}

		// 设置准备阶段的过期时间， 接受阶段的过期时间
		this.lastPrepareTimeoutMs = this.config.getStartPrepareTimeoutMs();
		this.lastAcceptTimeoutMs = this.config.getStartAcceptTimeoutMs();

		// 是否可以跳过准备阶段，且必须没有被别人拒绝过
		if (this.canSkipPrepare && !this.wasRejectBySomeone) {
			accept();
		}
		// if not reject by someone, no need to increase ballot
		else {
			prepare(this.wasRejectBySomeone);
		}
	}

	/**
	 * 准备阶段
	 * 
	 * @param needNewBallot
	 */
	public void prepare(boolean needNewBallot) {
		this.timeStat.point();

		exitAccept();
		this.isPreparing = true;
		this.canSkipPrepare = false;
		this.wasRejectBySomeone = false;

		this.proposerState.resetHighestOtherPreAcceptBallot();

		// 如果被拒绝过，则需要重新发起选举
		if (needNewBallot) {
			logger.info("START ProposalID {} HighestOther {} MyNodeID {}.", this.proposerState.getProposalID(),
					this.proposerState.getHighestOtherProposalID(), this.config.getMyNodeID());
			this.proposerState.newPrepare();
		}

		// 创建Paxos消息
		Proposal paxosMsg = new Proposal();
		paxosMsg.setMsgType(ProposalType.paxosPrepare.getValue());
		paxosMsg.setInstanceID(this.getInstanceID());
		paxosMsg.setNodeID(this.config.getMyNodeID());
		paxosMsg.setProposalID(this.proposerState.getProposalID());

		// 开启新一轮的统计
		this.proposerStat.startNewRound();

		addPrepareTimer(0);

		paxosMsg.setTimestamp(System.currentTimeMillis());

		// 发送消息
		// broadcastMessage
	}

	private void exitAccept() {
		if (this.isAccepting) {
			this.isAccepting = false;
			this.eventLoop.removeTimer(this.acceptTimerID);
			this.acceptTimerID = 0;
		}
	}

	private void addPrepareTimer(int timeoutMs) {
		if (this.prepareTimerID > 0) {
			this.eventLoop.removeTimer(this.prepareTimerID);
			this.prepareTimerID = 0L;
		}

		if (timeoutMs > 0) {
			this.prepareTimerID = this.eventLoop.addTimer(timeoutMs, TimerType.PrepareTimeout.getValue());
			return;
		}

		this.prepareTimerID = this.eventLoop.addTimer(this.lastPrepareTimeoutMs, TimerType.PrepareTimeout.getValue());
		this.timeoutInstanceID = getInstanceID();

		this.lastPrepareTimeoutMs *= 2;
		int maxPrepareTimeoutMs = this.config.getMaxPrepareTimeoutMs();
		if (this.lastPrepareTimeoutMs > maxPrepareTimeoutMs) {
			this.lastPrepareTimeoutMs = maxPrepareTimeoutMs;
		}
	}

	/**
	 * 准备阶段： 消息回复
	 * 
	 * @param paxosMsg
	 */
	public void onPrepareReply(Proposal paxosMsg) {

		// 当前议员是否处理准备阶段
		if (!this.isPreparing) {
			return;
		}

		// 消息是否和当前节点的议案id一致
		if (paxosMsg.getProposalID() != this.proposerState.getProposalID()) {
			return;
		}

		// 统计收到消息的数量
		this.proposerStat.addReceive(paxosMsg.getNodeID());

		// 统计同意的节点
		if (paxosMsg.getRejectByPromiseID() == 0) {
			BallotNumber ballot = new BallotNumber(paxosMsg.getPreAcceptID(), paxosMsg.getPreAcceptNodeID());
			this.proposerStat.addPromiseOrAccept(paxosMsg.getNodeID());
			this.proposerState.addPreAcceptValue(ballot, paxosMsg.getValue());
		}
		// 统计拒绝的节点
		else {
			this.proposerStat.addReject(paxosMsg.getNodeID());
			this.wasRejectBySomeone = true;
			this.proposerState.setOtherProposalID(paxosMsg.getRejectByPromiseID());
		}

		// 通过本轮投票
		if (this.proposerStat.isPassedOnThisRound()) {
			// int useTimeMs = this.timeStat.point();
			this.canSkipPrepare = true;
			accept();
		}
		// 没有通过本轮投票
		else if (this.proposerStat.isRejectedOnThisRound() || this.proposerStat.isAllReceiveOnThisRound()) {
			addPrepareTimer(OtherUtils.fastRand() % 30 + 10);
		}
	}

	public void accept() {
		exitPrepare();
		this.isAccepting = true;

		Proposal paxosMsg = new Proposal();
		paxosMsg.setMsgType(ProposalType.paxosAccept.getValue());
		paxosMsg.setInstanceID(getInstanceID());
		paxosMsg.setNodeID(this.config.getMyNodeID());
		paxosMsg.setProposalID(this.proposerState.getProposalID());
		paxosMsg.setValue(this.proposerState.getValue());
		// paxosMsg.setLastChecksum(this.instance.);

		this.proposerStat.startNewRound();
		addAcceptTimer(0);

		paxosMsg.setTimestamp(System.currentTimeMillis());
		// broadcastMessage(paxosMsg, runSelfFirst, sendType);
	}

	private void exitPrepare() {
		if (this.isPreparing) {
			this.isPreparing = false;
			this.eventLoop.removeTimer(this.prepareTimerID);
			this.prepareTimerID = 0;
		}
	}

	private void addAcceptTimer(int timeoutMs) {
		if (this.acceptTimerID > 0) {
			this.eventLoop.removeTimer(this.acceptTimerID);
			this.acceptTimerID = 0;
		}

		if (timeoutMs > 0) {
			this.acceptTimerID = this.eventLoop.addTimer(timeoutMs, TimerType.AcceptTimeout.getValue());
			return;
		}

		this.acceptTimerID = this.eventLoop.addTimer(this.lastAcceptTimeoutMs, TimerType.AcceptTimeout.getValue());
		this.timeoutInstanceID = getInstanceID();

		logger.debug("accept timeout mills {}.", this.lastAcceptTimeoutMs);
		this.lastAcceptTimeoutMs *= 2;
		int maxAcceptTimeoutMs = this.config.getMaxAcceptTimeoutMs();
		if (this.lastAcceptTimeoutMs > maxAcceptTimeoutMs) {
			this.lastAcceptTimeoutMs = maxAcceptTimeoutMs;
		}
	}

	public void onAcceptReply(Proposal paxosMsg) {

		if (!this.isAccepting) {
			return;
		}

		if (paxosMsg.getProposalID() != this.proposerState.getProposalID()) {
			return;
		}

		this.proposerStat.addReceive(paxosMsg.getNodeID());
		if (paxosMsg.getRejectByPromiseID() == 0) {
			this.proposerStat.addPromiseOrAccept(paxosMsg.getNodeID());
		} else {
			this.proposerStat.addReject(paxosMsg.getNodeID());
			this.wasRejectBySomeone = true;
			this.proposerState.setOtherProposalID(paxosMsg.getRejectByPromiseID());
		}

		if (this.proposerStat.isPassedOnThisRound()) {
			// int useTimeMs = this.timeStat.point();
			exitAccept();
			//this.learner.proposerSendSuccess(getInstanceID(), this.proposerState.getProposalID());
		} else if (this.proposerStat.isRejectedOnThisRound() || this.proposerStat.isAllReceiveOnThisRound()) {
			addAcceptTimer(OtherUtils.fastRand() % 30 + 10);
		}
	}

	/**
	 * Prepare阶段 -- 超时回调
	 */
	public void onPrepareTimeout() {
		if (getInstanceID() != this.timeoutInstanceID) {
			return;
		}

		prepare(this.wasRejectBySomeone);
	}

	/**
	 * Accept阶段 -- 超时回调
	 */
	public void onAcceptTimeout() {
		if (getInstanceID() != this.timeoutInstanceID) {
			return;
		}

		prepare(this.wasRejectBySomeone);
	}
}