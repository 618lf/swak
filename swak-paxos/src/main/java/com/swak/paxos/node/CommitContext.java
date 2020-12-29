package com.swak.paxos.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据发送上下文： 保存最新的数据
 * 
 * @author lifeng
 * @date 2020年12月28日 上午11:39:34
 */
public class CommitContext {
	private final Logger logger = LoggerFactory.getLogger(CommitContext.class);
	private volatile long instanceID;
	
}
