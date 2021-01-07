package com.swak.paxos.node;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.common.TimeStat;
import com.swak.paxos.config.Config;
import com.swak.paxos.enums.TimerType;
import com.swak.paxos.event.EventLoop;
import com.swak.paxos.protol.Proposal;
import com.swak.paxos.protol.ProposePromise;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 实例
 * 
 * @author lifeng
 * @date 2020年12月28日 上午10:50:24
 */
@Getter
@Setter
@Accessors(chain = true)
public class Instance {

	private final Logger logger = LoggerFactory.getLogger(Instance.class);
	private LinkedBlockingQueue<ProposePromise> proposeQueue = new LinkedBlockingQueue<>();
	private Config config;
	private Acceptor acceptor;
	private Learner learner;
	private Proposer proposer;
	private TimeStat timeStat = new TimeStat();
	private boolean started = false;
	private EventLoop eventLoop;

	/**
	 * 提交议案:委托master来提交
	 */
	public ProposePromise commit(Proposal propoal) {
		ProposePromise promise = new ProposePromise();
		promise.setProposal(propoal);
		eventLoop.addCommitMessage(propoal);
		if (propoal.getTimeoutMs() > 0) {
			promise.setTimeoutTimerId(eventLoop.addTimer(propoal.getTimeoutMs(), TimerType.CommitTimeout.getValue()));
		}
		return promise;
	}

	/**
	 * 开启一轮
	 */
	public void startNewRound() {

		// 如果正在工作则不用处理
		if (this.proposer.isWorking()) {
			return;
		}

		// 开启一个新的议案 -- 无阻塞的获取
		ProposePromise promise = proposeQueue.poll();
		if (promise != null) {
			this.proposer.propose(promise.getProposal());
		}
	}
}
