package com.swak.paxos.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.common.Crc32;
import com.swak.paxos.common.OtherUtils;
import com.swak.paxos.common.SerialLock;
import com.swak.paxos.config.Config;
import com.swak.paxos.enums.PaxosTryCommitRet;
import com.swak.paxos.protol.CommitResult;
import com.wuba.wpaxos.storemachine.SMCtx;

/**
 * 数据发送上下文： 保存最新的数据
 * 
 * @author lifeng
 * @date 2020年12月28日 上午11:39:34
 */
public class CommitContext {
	private final Logger logger = LoggerFactory.getLogger(CommitContext.class);
	private Config config;
	private volatile long instanceID;
	private int commitRet;
	private boolean isCommitEnd;
	private int timeoutMs;
	private byte[] psValue;
	private SMCtx smCtx;
	private SerialLock serialLock = new SerialLock();

	public CommitContext(Config config) {
		this.config = config;
		this.newCommit(null, null, 0);
	}

	public void newCommit(byte[] psValue, SMCtx pSMCtx, int timeoutMs) {
		serialLock.lock();
		this.instanceID = -1;
		this.commitRet = -1;
		this.isCommitEnd = false;
		this.timeoutMs = timeoutMs;

		this.psValue = psValue;
		this.smCtx = pSMCtx;

		if (psValue != null) {
			logger.debug("OK, valuesize {}.", psValue.length);
		}
		this.serialLock.unLock();
	}

	public boolean isNewCommit() {
		return this.instanceID == (long) -1 && this.psValue != null;
	}

	public byte[] getCommitValue() {
		return this.psValue;
	}

	public void startCommit(long instanceID) {
		serialLock.lock();
		this.instanceID = instanceID;
		serialLock.unLock();
	}

	public boolean isMycommit(long instanceID, byte[] sLearnValue, SMCtx smCtx) {
		serialLock.lock();
		boolean isMyCommit = false;

		try {
			if ((!isCommitEnd) && (this.instanceID == instanceID)) {
				if (psValue.length == sLearnValue.length) {
					int crc1 = Crc32.crc32(psValue);
					int crc2 = Crc32.crc32(sLearnValue);
					if (crc1 == crc2) {
						isMyCommit = true;
					}
				}
			}

			if (isMyCommit) {
				smCtx.setpCtx(this.smCtx.getpCtx());
				smCtx.setSmId(this.smCtx.getSmId());
			}
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			this.serialLock.unLock();
		}

		return isMyCommit;
	}

	public void setResult(int commitRet, long instanceID, byte[] sLearnValue) {

		this.serialLock.lock();
		try {
			if (isCommitEnd || (this.instanceID != instanceID)) {
				return;
			}

			this.commitRet = commitRet;
			if (this.commitRet == 0) {
				if (psValue.length == sLearnValue.length) {
					int crc1 = Crc32.crc32(psValue);
					int crc2 = Crc32.crc32(sLearnValue);
					if (crc1 != crc2) {
						this.commitRet = PaxosTryCommitRet.PaxosTryCommitRet_Conflict.getRet();
						logger.warn("crc check failed.smid {}", smCtx.getSmId());
					}
				} else {
					this.commitRet = PaxosTryCommitRet.PaxosTryCommitRet_Conflict.getRet();
				}
			}

			this.isCommitEnd = true;
			this.psValue = null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			this.serialLock.interupt();
			this.serialLock.unLock();
		}
	}

	public void setResultOnlyRet(int commitRet) {
		setResult(commitRet, -1, null);
	}

	public CommitResult getResult() {
		long succInstanceID = -1;
		this.serialLock.lock();
		try {
			long start = OtherUtils.getSystemMS();
			while (!isCommitEnd) {
				this.serialLock.waitTime(1000);
			}

			if (this.commitRet == 0) {
				logger.debug("commit success, instanceid {}", this.instanceID);
				succInstanceID = this.instanceID;
			} else {
				logger.error("commit failed, ret {}.smid {}", this.commitRet, smCtx.getSmId());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			this.serialLock.unLock();
		}

		CommitResult commitResult = new CommitResult(this.commitRet, succInstanceID);
		return commitResult;
	}

	public int getTimeoutMs() {
		return this.timeoutMs;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public void setCommitValue(byte[] psValue) {
		this.psValue = psValue;
	}

}
