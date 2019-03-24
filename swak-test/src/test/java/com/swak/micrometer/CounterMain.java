package com.swak.micrometer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.search.Search;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

public class CounterMain {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	static {
		Metrics.addRegistry(new SimpleMeterRegistry());
	}

	public static void main(String[] args) {
		Order order1 = new Order();
		order1.setOrderId("ORDER_ID_1");
		order1.setAmount(100);
		order1.setChannel("CHANNEL_A");
		order1.setCreateTime(LocalDateTime.now());
		createOrder(order1);
		Order order2 = new Order();
		order2.setOrderId("ORDER_ID_2");
		order2.setAmount(200);
		order2.setChannel("CHANNEL_B");
		order2.setCreateTime(LocalDateTime.now());
		createOrder(order2);
		Search.in(Metrics.globalRegistry).meters().forEach(each -> {
			StringBuilder builder = new StringBuilder();
			builder.append("name:").append(each.getId().getName()).append(",tags:").append(each.getId().getTags())
					.append(",type:").append(each.getId().getType()).append(",value:").append(each.measure());
			System.out.println(builder.toString());
		});
	}

	private static void createOrder(Order order) {
		Metrics.counter("order.create", "channel", order.getChannel(), "createTime",
				FORMATTER.format(order.getCreateTime())).increment();
	}
}

class Order {
	private String orderId;
	private Integer amount;
	private String channel;
	private LocalDateTime createTime;

	public String getOrderId() {
		return orderId;
	}

	public Integer getAmount() {
		return amount;
	}

	public String getChannel() {
		return channel;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}
}
