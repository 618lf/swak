package com.tmt.consumer;

import com.swak.common.Constants;
import com.swak.common.eventbus.Event;
import com.swak.common.eventbus.EventConsumer;

/**
 * 更新事件的消费者
 * @author lifeng
 */
public class UpdateEventConsumer implements EventConsumer{

	@Override
	public String getChannel() {
		return Constants.UPDATE_EVENT_TOPIC;
	}

	@Override
	public void onMessge(Event event) {
		// System.out.println("收到消息");
	}
}
