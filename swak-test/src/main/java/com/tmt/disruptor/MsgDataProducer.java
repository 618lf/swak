package com.tmt.disruptor;

import com.lmax.disruptor.RingBuffer;

public class MsgDataProducer {
	private final RingBuffer<MsgData> ringBuffer;

	public MsgDataProducer(RingBuffer<MsgData> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	public void pushData(long msg) {
		// 可以把ringBuffer看做一个事件队列，那么next就是得到下面一个事件槽
		long seq = ringBuffer.next();
		try {
			// 获取可用位置
			MsgData event = ringBuffer.get(seq);
			// 填充可用位置
			event.setMsg(msg);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 发布事件， 通知消费者
			ringBuffer.publish(seq);
		}
	}
}
