package com.swak.paxos.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.common.EventLoop;
import com.swak.paxos.common.OtherUtils;
import com.swak.paxos.common.TimeStat;
import com.swak.paxos.common.WaitLock;
import com.swak.paxos.config.Config;
import com.swak.paxos.config.Def;
import com.swak.paxos.enums.PaxosTryCommitRet;
import com.swak.paxos.protol.CommitResult;
import com.wuba.wpaxos.comm.breakpoint.Breakpoint;
import com.wuba.wpaxos.storemachine.SMCtx;
import com.wuba.wpaxos.utils.JavaOriTypeWrapper;

/**
 * 消息发送器： 开启一轮消息发送
 * 
 * @author lifeng
 * @date 2020年12月28日 上午11:28:24
 */
public class Committer {

	private final Logger logger = LoggerFactory.getLogger(Committer.class);
	private CommitContext context;
	private Config config;
	private EventLoop ioLoop;
	private WaitLock waitLock = new WaitLock();
	private int timeoutMs;
	private long lastLogTime;

	public CommitResult newValueGetID(byte[] sValue, JavaOriTypeWrapper<Long> instanceIdWrap) {
		return newValueGetID(sValue, instanceIdWrap, null);
	}

	public CommitResult newValueGetID(byte[] sValue, JavaOriTypeWrapper<Long> instanceIdWrap, SMCtx smCtx) {
		CommitResult commitRet = new CommitResult(PaxosTryCommitRet.PaxosTryCommitRet_OK.getRet(),
				instanceIdWrap.getValue());
		int retryCount = 3;
		while (retryCount > 0) {
			TimeStat timestat = new TimeStat();
			timestat.point();

			commitRet = newValueGetIDNoRetry(sValue, instanceIdWrap, smCtx);
			if (commitRet.getCommitRet() != PaxosTryCommitRet.PaxosTryCommitRet_Conflict.getRet()) {
				if (commitRet.getCommitRet() == 0) {
				} else {
				}
				break;
			}
			if (smCtx != null && smCtx.getSmId() == Def.MASTER_V_SMID) {
				break;
			}
			retryCount--;
		}

		return commitRet;
	}

	public CommitResult newValueGetIDNoRetry(byte[] sValue, JavaOriTypeWrapper<Long> instanceIdWrap, SMCtx smCtx) {
		logStatus();
		CommitResult commitRet = new CommitResult(-1, instanceIdWrap.getValue());
		int lockUseTimeMS = 0;
		long beginLock = OtherUtils.getSystemMS();
		boolean hasLock = this.waitLock.lock(this.timeoutMs);
		long endLock = OtherUtils.getSystemMS();
		lockUseTimeMS = (int) (hasLock && (endLock > beginLock) ? (endLock - beginLock) : 0);

		if (!hasLock) {
			if (lockUseTimeMS > 0) {
				logger.error("try get lock, but timeout, lockusetime {}, groupId {}.", lockUseTimeMS,
						this.config.getMyGroupIdx());
				commitRet.setCommitRet(PaxosTryCommitRet.PaxosTryCommitRet_Timeout.getRet());
				logger.info("wait threads {} avg thread wait ms {} reject rate {}, groupIdx {} wait threads set {}.",
						this.waitLock.getNowHoldThreadCount(), this.waitLock.getNowAvgThreadWaitTime(),
						this.waitLock.getNowRejectRate(), this.config.getMyGroupIdx(), this.waitLock.getWaitThdSet());
				return commitRet;
			} else {
				logger.error("try get lock, but too many thread waiting, reject, groupId {}.",
						this.config.getMyGroupIdx());
				commitRet.setCommitRet(PaxosTryCommitRet.PaxosTryCommitRet_TooManyThreadWaiting_Reject.getRet());
				logger.info("wait threads {} avg thread wait ms {} reject rate {}, groupIdx {} wait threads set {}.",
						this.waitLock.getNowHoldThreadCount(), this.waitLock.getNowAvgThreadWaitTime(),
						this.waitLock.getNowRejectRate(), this.config.getMyGroupIdx(), this.waitLock.getWaitThdSet());
				return commitRet;
			}
		}

		int leftTimeoutMS = -1;
		if (this.timeoutMs > 0) {
			leftTimeoutMS = this.timeoutMs > lockUseTimeMS ? (this.timeoutMs - lockUseTimeMS) : 0;
			if (leftTimeoutMS < 200) {
				logger.error("get lock ok, but lockusetime {} too long, lefttimeout {}.", lockUseTimeMS, leftTimeoutMS);
				this.waitLock.unLock();
				commitRet.setCommitRet(PaxosTryCommitRet.PaxosTryCommitRet_Timeout.getRet());
				return commitRet;
			}
		}

		logger.debug("getlock ok, use time {}.", lockUseTimeMS);

		int smID = smCtx != null ? smCtx.getSmId() : 0;

		byte[] packSMIDValue = this.smFac.packPaxosValue(sValue, sValue.length, smID);

		this.context.newCommit(packSMIDValue, smCtx, leftTimeoutMS);
		this.ioLoop.addNotify();

		commitRet = this.context.getResult();
		instanceIdWrap.setValue(commitRet.getSuccInstanceID());

		this.waitLock.unLock();
		return commitRet;
	}

	public void setTimeoutMs(int timeoutMs) {
		this.timeoutMs = timeoutMs;
	}

	public void setMaxHoldThreads(int maxHoldThreads) {
		this.waitLock.setMaxWaitLogCount(maxHoldThreads);
	}

	public void setProposeWaitTimeThresholdMS(int waitTimeThresholdMS) {
		this.waitLock.setLockWaitTimeThreshold(waitTimeThresholdMS);
	}

	public void logStatus() {
		long nowTime = OtherUtils.getSystemMS();
		if (nowTime > this.lastLogTime && nowTime - this.lastLogTime > 30000) {
			this.lastLogTime = nowTime;
			logger.info("wait threads {} avg thread wait ms {} reject rate {}, groupIdx {} wait threads set {}.",
					this.waitLock.getNowHoldThreadCount(), this.waitLock.getNowAvgThreadWaitTime(),
					this.waitLock.getNowRejectRate(), this.config.getMyGroupIdx(), this.waitLock.getWaitThdSet());
		}
	}
}