package com.swak.paxos.event;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.swak.paxos.enums.TimerType;
import com.swak.paxos.node.Instance;
import com.swak.paxos.protol.Propoal;
import com.swak.paxos.protol.ProposePromise;

/**
 * 事件处理器
 * 
 * @author DELL
 */
public class EventLoop extends Thread {

	/**
	 * 所有的事件： 阻塞在这个事件上
	 */
	private LinkedBlockingQueue<Event> events = new LinkedBlockingQueue<>();

	/**
	 * 超时处理器
	 */
	private Timer timer;
	private Instance instance;

	public EventLoop() {
		this.timer = new Timer();
	}

	/**
	 * 添加超时器
	 */
	public long addTimer(long absTime, int type) {
		return this.timer.addTimerWithType(absTime, type);
	}

	/**
	 * 添加议案
	 * 
	 * @param propoal
	 */
	public void addCommitMessage(Propoal propoal) {
		this.addEvent(Event.Propose(this, propoal));
	}

	/**
	 * 添加事件： 任何消息来都添加事件
	 * 
	 * @param event
	 */
	public void addEvent(Event event) {
		this.events.add(event);
	}

	/**
	 * 事件循环
	 */
	@Override
	public void run() {
		while (true) {
			try {
				// 优先处理超时事件
				int timeoutMs = this.dealWithTimeout();

				// 阻塞在事件的获取上, 最多阻塞指定的毫秒数
				Event event = this.events.poll(timeoutMs, TimeUnit.MILLISECONDS);

				// 处理提交的事件
				if (event.getType() == Event.Event_Propose) {
					this.dealWithCommitEvent(event);
				}
			} catch (Exception e) {

			}
		}
	}

	/**
	 * 处理超时，返回下次超时的时间
	 * 
	 * @return
	 */
	private int dealWithTimeout() {
		return 1000;
	}

	/**
	 * 处理提交的事件: 事件统一交给实例去处理
	 */
	private void dealWithCommitEvent(Event event) {
		this.instance.onCommit(event);
	}
}
