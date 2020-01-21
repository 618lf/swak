package com.swak.rxtx;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.threads.ScheduledContext;
import com.swak.rxtx.channel.Channel;
import com.swak.rxtx.channel.EventLoopGroup;
import com.swak.rxtx.utils.SerialUtils;
import com.swak.utils.Maps;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 设备管理器
 * 
 * @author lifeng
 */
@Getter
@Setter
@Accessors(chain = true)
public class Channels {

	/**
	 * 所有设备
	 */
	private Map<String, Channel> channels;

	/**
	 * 设备心跳
	 */
	private Heartbeat heartbeat;

	/**
	 * 输入输出线程池组
	 */
	private EventLoopGroup eventLoops;

	/**
	 * 初始化channel处理器
	 */
	private Consumer<Channel> channelInit;

	/**
	 * 创建一个设备管理器
	 * 
	 * @param works
	 * @param heartbeatSeconds
	 * @param channelInit
	 */
	public Channels(int works, int heartbeatSeconds, Consumer<Channel> channelInit) {
		this.channels = Maps.newHashMap(works * 2);
		this.heartbeat = new Heartbeat(heartbeatSeconds);
		this.eventLoops = new EventLoopGroup(works);
	}

	/**
	 * 尝试加载所有的设备
	 */
	private void scanChannels() {
		List<String> comms = SerialUtils.getCommNames();
		for (String comm : comms) {
			channels.computeIfAbsent(comm, (v) -> this.scanChannel(comm));
		}
	}

	/**
	 * 扫描设备
	 * 
	 * @return
	 */
	private Channel scanChannel(String comm) {
		Channel channel = new Channel(comm).register(eventLoops.next());
		if (this.channelInit != null) {
			this.channelInit.accept(channel);
		}
		return channel;
	}

	/**
	 * 刷新设备
	 */
	public void connect() {

		/**
		 * 查找所有设备
		 */
		this.scanChannels();

		/**
		 * 刷新所有设备
		 */
		channels.forEach((comm, device) -> {
			device.connect();
		});
	}

	/**
	 * 主动读取数据 -- 通过channelHandler 来处理
	 */
	public void read() {
		channels.forEach((comm, device) -> {
			device.read();
		});
	}

	/**
	 * 发送命令
	 */
	public void write(Object data) {
		channels.forEach((comm, device) -> {
			device.write(data);
		});
	}

	/**
	 * 重写 heartbeat逻辑，让其具备发现新设备的能力
	 * 
	 */
	public void heartbeat() {

		// 尝试发现新设备
		this.scanChannels();

		// 处理设备的心跳
		channels.forEach((comm, device) -> {
			device.heartbeat();
		});
	}

	/**
	 * 心跳
	 */
	protected class Heartbeat implements Runnable {

		/**
		 * 数据处理线程
		 */
		private ScheduledContext heartbeat;

		public Heartbeat(int heartbeatSeconds) {
			heartbeat = Contexts.createScheduledContext("Channels.Heartbeat-", 1, true, 30 * 2, TimeUnit.SECONDS);
			heartbeat.scheduleAtFixedRate(this, 0, heartbeatSeconds, TimeUnit.SECONDS);
		}

		@Override
		public void run() {
			Channels.this.heartbeat();
		}
	}

	/**
	 * 构建起器
	 * 
	 * @author lifeng
	 */
	@Getter
	@Setter
	@Accessors(chain = true)
	public static class Builder {
		private int works;
		private int heartbeatSeconds;
		private Consumer<Channel> channelInit;

		public Channels build() {
			return new Channels(works, heartbeatSeconds, channelInit);
		}
	}

	/**
	 * 创建构造器
	 * 
	 * @return
	 */
	public static Builder builder() {
		return new Builder();
	}
}
