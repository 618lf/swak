package com.swak.paxos.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.common.EventLoop;
import com.swak.paxos.config.Config;
import com.swak.paxos.enums.MsgCmd;
import com.swak.paxos.protol.BaseMsg;
import com.swak.paxos.protol.CheckpointMsg;
import com.swak.paxos.protol.Header;
import com.swak.paxos.protol.PaxosMessage;
import com.swak.paxos.transport.Message;

/**
 * 实例
 * 
 * @author lifeng
 * @date 2020年12月28日 上午10:50:24
 */
public class Instance {

	private final Logger logger = LoggerFactory.getLogger(Instance.class);
	private Config config;
	private Acceptor acceptor;
	private Learner learner;
	private Proposer proposer;
	private Committer committer;
	private CommitContext committerCtx;
	private int lastChecksum;
	private EventLoop eventLoop;

	public Instance(Config config) {
		this.config = config;
		this.lastChecksum = 0;
	}

	public int getLastChecksum() {
		return this.lastChecksum;
	}

	/**
	 * 全局的消息监听： 1. 收到消息
	 * 
	 * @param receiveMsg
	 * @return
	 */
	public int onReceiveMessage(Message receiveMsg) {
		this.eventLoop.addMessage(receiveMsg);
		return 0;
	}

	/**
	 * 全局的消息监听：2. 顺序的处理消息
	 * 
	 * @param buf
	 */
	public void onReceive(byte[] buf) {
		if (buf.length <= 0) {
			logger.error("buffer size {} too short.", buf.length);
			return;
		}
		BaseMsg baseMsg = Role.unPackBaseMsg(buf);
		if (baseMsg == null) {
			return;
		}
		Header header = baseMsg.getHeader();

		int cmd = header.getCmdid();
		if (cmd == MsgCmd.paxosMsg.getValue()) {
			PaxosMessage paxosMsg = (PaxosMessage) baseMsg.getBodyProto();
			if (paxosMsg == null) {
				return;
			}
			onReceivePaxosMsg(paxosMsg, false);
		} else if (cmd == MsgCmd.checkpointMsg.getValue()) {
			CheckpointMsg checkpointMsg = (CheckpointMsg) baseMsg.getBodyProto();
			if (checkpointMsg == null) {
				return;
			}

			onReceiveCheckpointMsg(checkpointMsg);
		}
	}

	private void onReceivePaxosMsg(PaxosMessage paxosMsg, boolean isRetry) {

	}

	private void onReceiveCheckpointMsg(CheckpointMsg checkpointMsg) {

	}
}
