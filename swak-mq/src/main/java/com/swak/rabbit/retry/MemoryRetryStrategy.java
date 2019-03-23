package com.swak.rabbit.retry;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

import com.swak.rabbit.RabbitMQTemplate;
import com.swak.rabbit.message.PendingConfirm;
import com.swak.utils.ConcurrentHashSet;

/**
 * 基于内存的消息重试器
 * 
 * @author lifeng
 */
public class MemoryRetryStrategy extends AbstractRetryStrategy {
	private RabbitMQTemplate template;
	private ConcurrentHashSet<String> confirms = new ConcurrentHashSet<>();
	private TransferQueue<PendingConfirm> _confirms;

	public MemoryRetryStrategy() {
		confirms = new ConcurrentHashSet<>();
		_confirms = new LinkedTransferQueue<>();
	}

	@Override
	public RabbitMQTemplate getSender() {
		return template;
	}

	@Override
	public void add(PendingConfirm pendingConfirm) {
		if (confirms.addIfAbsent(pendingConfirm.getId())) {
			_confirms.add(pendingConfirm);
		}
	}

	@Override
	public void del(String id) {
		confirms.remove(id);
	}

	/**
	 * 获取头部数据，只获取，不删除
	 * 
	 * @return
	 */
	protected PendingConfirm headGet() {
		return _confirms.peek();
	}

	/**
	 * 是否已经应答
	 * 
	 * @param pendingConfirm
	 * @return
	 */
	protected boolean hasAcked(PendingConfirm pendingConfirm) {
		return !confirms.contains(pendingConfirm.getId());
	}

	/**
	 * 获取头部数据，只获取，不删除
	 * 
	 * @return
	 */
	protected void headRemove() {
		PendingConfirm confirm = _confirms.poll();
		if (confirm != null) {
			this.del(confirm.getId());
		}
	}
}