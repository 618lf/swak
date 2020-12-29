package com.swak.paxos.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.protol.CommitResult;
import com.swak.paxos.protol.ProposeParam;

/**
 * 消息发送器： 开启一轮消息发送
 * 
 * @author lifeng
 * @date 2020年12月28日 上午11:28:24
 */
public class Committer {

	private final Logger logger = LoggerFactory.getLogger(Committer.class);
	private CommitContext context;

	public CommitResult newValueGetIDNoRetry(ProposeParam propose) {

	}
}