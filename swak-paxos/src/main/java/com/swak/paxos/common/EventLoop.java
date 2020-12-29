/*
 * Copyright (C) 2005-present, 58.com.  All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.swak.paxos.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.common.Timer.TimerObj;
import com.swak.paxos.config.Config;
import com.swak.paxos.config.Def;
import com.swak.paxos.node.Instance;
import com.swak.paxos.protol.PaxosMessage;
import com.swak.paxos.transport.Message;

/**
 * 单线程：处理一个实例的所有事件： 消息发送、消息接收、超时处理 <br>
 * 相当于是一个任务队列： 顺序的处理任务
 */
public class EventLoop extends Thread {
	private final Logger logger = LoggerFactory.getLogger(EventLoop.class);
	private boolean isEnd;
	private Timer timer;
	private Map<Long, Boolean> timerIDExist = new HashMap<Long, Boolean>();
	private LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();
	private LinkedBlockingQueue<PaxosMessage> retryQueue = new LinkedBlockingQueue<PaxosMessage>();
	private AtomicInteger queueMemSize = new AtomicInteger();
	private Config poConfig;
	private Instance poInstance;
	public static final int RETRY_QUEUE_MAX_LEN = 300;

	public EventLoop(Config poConfig, Instance poInstance) {
		super();
		this.poConfig = poConfig;
		this.poInstance = poInstance;
		this.isEnd = false;
		this.timer = new Timer();
	}

	/**
	 * 停止事件轮询
	 */
	public void shutdown() {
		if (!this.isEnd) {
			this.isEnd = true;
			try {
				this.join();
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void run() {
		this.isEnd = false;
		while (true) {
			try {
				int nextTimeout = 5000;

				// 处理超时
				nextTimeout = dealwithTimeout(nextTimeout);

				// 处理接收消息
				this.dealWithMessage(nextTimeout);

				// 处理消息重试
				this.dealWithRetry();

				// 处理提交新的消息
				this.dealWithCommitMessage();

				if (this.isEnd) {
					logger.info("IOLoop [End]");
					break;
				}
			} catch (Throwable th) {
				logger.error("io loop error", th);
			}
		}
	}

	private int dealwithTimeout(int nextTimeout) {
		boolean hasTimeout = true;

		while (hasTimeout) {

			TimerObj timerObj = new TimerObj();
			hasTimeout = this.timer.popTimeout(timerObj);

			if (hasTimeout) {
				dealwithTimeoutOne(timerObj.getTimerID(), timerObj.getType());
				nextTimeout = this.timer.getNextTimeout(nextTimeout);
				if (nextTimeout != 0) {
					break;
				}
			} else {
				nextTimeout = this.timer.getNextTimeout(nextTimeout);
			}
		}

		return nextTimeout;
	}

	private void dealwithTimeoutOne(long timerID, int type) {
		if (!this.timerIDExist.containsKey(timerID)) {
			return;
		}

		this.timerIDExist.remove(timerID);
		this.poInstance.onTimeout(timerID, type);
	}

	private void dealWithMessage(int timeoutMs) {

		try {
			Message receiveMsg = this.messageQueue.poll(timeoutMs, TimeUnit.MILLISECONDS);
			if (receiveMsg != null && receiveMsg.getReceiveLen() > 0 && !receiveMsg.isNotifyMsg()) {
				this.queueMemSize.addAndGet(-receiveMsg.getReceiveLen());
				this.poInstance.onReceive(receiveMsg.getReceiveBuf());
			}

			this.poInstance.checkNewValue();
		} catch (Exception e) {
			logger.error("oneLoop error", e);
		}
	}

	private void dealWithCommitMessage() {
		try {
			this.poInstance.checkNewValue();
		} catch (Exception e) {
			logger.error("oneLoop error", e);
		}
	}

	private void dealWithRetry() {
		if (this.retryQueue.isEmpty()) {
			return;
		}

		boolean haveRetryOne = false;
		while (!this.retryQueue.isEmpty()) {
			try {
				PaxosMessage paxosMsg = this.retryQueue.peek();
				if (paxosMsg.getInstanceID() > this.poInstance.getNowInstanceID() + 1) {
					break;
				} else if (paxosMsg.getInstanceID() == this.poInstance.getNowInstanceID() + 1) {
					if (haveRetryOne) {
						logger.debug("retry msg (i+1). instanceid {}.", paxosMsg.getInstanceID());
						this.poInstance.onReceivePaxosMsg(paxosMsg, true);
					} else {
						break;
					}
				} else if (paxosMsg.getInstanceID() == this.poInstance.getNowInstanceID()) {
					logger.debug("retry msg. instanceid {}.", paxosMsg.getInstanceID());
					this.poInstance.onReceivePaxosMsg(paxosMsg, false);
					haveRetryOne = true;
				}

				this.retryQueue.poll();
			} catch (Exception e) {
				logger.error("ioloop dealWithRetry error", e);
			}
		}
	}

	public void clearRetryQueue() {
		while (!this.retryQueue.isEmpty()) {
			this.retryQueue.poll();
		}
	}

	/**
	 * 添加接收到的消息
	 * 
	 * @param receiveMsg
	 * @return
	 */
	public int addMessage(Message receiveMsg) {
		try {

			if (this.messageQueue.size() > this.poConfig.getMaxIOLoopQueueLen()) {
				logger.error("Queue full, skip msg.");
				return -2;
			}

			if (this.queueMemSize.get() > Def.MAX_QUEUE_MEM) {
				logger.error("Queue memsize {} too large, can't enqueue", this.queueMemSize);
				return -2;
			}

			this.messageQueue.offer(receiveMsg);
			this.queueMemSize.addAndGet(receiveMsg.getReceiveLen());
			return 0;
		} catch (Exception e) {
			logger.error("ioloop addMessage error", e);
		}
		return -2;
	}

	/**
	 * 添加重试消息
	 * 
	 * @param paxosMsg
	 */
	public void addRetryPaxosMsg(PaxosMessage paxosMsg) {
		if (this.retryQueue.size() > RETRY_QUEUE_MAX_LEN) {
			this.retryQueue.poll();
		}
		this.retryQueue.offer(paxosMsg);
	}

	/**
	 * 添加通知消息： 需要发送消息时，先添加一个这样的任务，来发送消息
	 */
	public void addNotify() {
		this.messageQueue.offer(Message.getNotifyNullMsg());
	}

	/**
	 * 添加过期的事件
	 * 
	 * @param timeout
	 * @param type
	 * @return
	 */
	public long addTimer(int timeout, int type) {
		if (timeout == -1) {
			return -1;
		}
		long absTime = Time.getSteadyClockMS() + timeout;
		long timerID = this.timer.addTimerWithType(absTime, type);
		this.timerIDExist.put(timerID, true);
		this.addNotify();
		return timerID;
	}

	/**
	 * 删除事件
	 * 
	 * @param timeout
	 * @param type
	 * @return
	 */
	public void removeTimer(long timerID) {
		if (this.timerIDExist.containsKey(timerID)) {
			this.timerIDExist.remove(timerID);
		}
	}
}
