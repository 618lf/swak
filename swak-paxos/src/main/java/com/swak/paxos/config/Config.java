package com.swak.paxos.config;

import com.swak.paxos.common.OtherUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 系统全局的配置
 * 
 * @author DELL
 */
@Getter
@Setter
@Accessors(chain = true)
public class Config {

	private long myNodeID;
	private int myGroupIdx;
	private int groupCount;
	private boolean isIMFollower;
	private int prepareTimeout = 1000;
	private int acceptTimeout = 1000;
	private int askForLearnTimeout = 3000;
	private int learnerSendSpeed;
	private boolean isLargeBufferMode;
	private int majorityCount;
	private int nodeCount;
	private long followNodeID;

	public int getMaxBufferSize() {
		if (this.isLargeBufferMode) {
			// 50M
			return 52428800;
		} else {
			// 10M
			return 10485760;
		}
	}

	public int getStartPrepareTimeoutMs() {
		if (this.isLargeBufferMode) {
			return 15000;
		} else {
			return 1500;
		}
	}

	public int getStartAcceptTimeoutMs() {
		if (isLargeBufferMode) {
			return 15000;
		} else {
			return 1000;
		}
	}

	public int getMaxPrepareTimeoutMs() {
		if (isLargeBufferMode) {
			return 90000;
		} else {
			// return 8000;
			return 8000;
		}
	}

	public int getMaxAcceptTimeoutMs() {
		if (isLargeBufferMode) {
			return 90000;
		} else {
			// return 8000;
			return 8000;
		}
	}

	public int getMaxIOLoopQueueLen() {
		if (isLargeBufferMode) {
			return 1024 / this.groupCount + 100;
		} else {
			return 10240 / this.groupCount + 10000;
		}
	}

	public int getMaxQueueLen() {
		if (isLargeBufferMode) {
			return 1024;
		} else {
			return 10240;
		}
	}

	public int getAskforLearnInterval() {
		// 如果自己不是 follower 那么可以发送给 learn 请求节点更长时间的数据。
		if (!this.isIMFollower) {
			if (isLargeBufferMode) {
				return 50000 + (OtherUtils.fastRand() % 10000);
			} else {
				return 2500 + (OtherUtils.fastRand() % 500);
			}
		} else {
			if (isLargeBufferMode) {
				return 30000 + (OtherUtils.fastRand() % 15000);
			} else {
				return 2000 + (OtherUtils.fastRand() % 1000);
			}
		}
	}

	public int getLearnerReceiverAckLead() {
		if (isLargeBufferMode) {
			return 2;
		} else {
			return 4;
		}
	}

	public int getLearnerSenderPrepareTimeoutMs() {
		if (isLargeBufferMode) {
			return 6000;
		} else {
			return 5000;
		}
	}

	public int getLearnerSenderAckTimeoutMs() {
		if (isLargeBufferMode) {
			return 60000;
		} else {
			return 5000;
		}
	}

	public int getLearnerSenderAckLead() {
		if (isLargeBufferMode) {
			return 5;
		} else {
			return 21;
		}
	}

	public int getTcpOutQueueDropTimeMs() {
		if (isLargeBufferMode) {
			return 20000;
		} else {
			return 5000;
		}
	}

	public int getLogFileMaxSize() {
		if (isLargeBufferMode) {
			// 100M
			return 524288000;
		} else {
			// 100M
			return 104857600;
		}
	}

	public int getTcpConnectionNonActiveTimeout() {
		if (isLargeBufferMode) {
			return 600000;
		} else {
			return 60000;
		}
	}

	public int getLearnerSenderSendQps() {
		if (isLargeBufferMode) {
			return 10000 / this.groupCount;
		} else {
			return 100000 / this.groupCount;
		}
	}

	public int getCleanerDeleteQps() {
		if (isLargeBufferMode) {
			return 30000 / groupCount;
		} else {
			return 300000 / groupCount;
		}
	}
}
