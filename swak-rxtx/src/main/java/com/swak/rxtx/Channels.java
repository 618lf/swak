package com.swak.rxtx;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.closable.ShutDownHook;
import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.threads.ScheduledContext;
import com.swak.rxtx.channel.Channel;
import com.swak.rxtx.channel.EventLoopGroup;
import com.swak.rxtx.utils.SerialUtils;

/**
 * 设备管理器
 *
 * @author lifeng
 */
public class Channels {

	/**
	 * 全局唯一处理器
	 */
	private static Channels _ME = null;

	public static Channels me() {
		return _ME;
	}

	/**
	 * 用于消息调试
	 */
	protected static Logger logger = LoggerFactory.getLogger(Channel.class);

	/**
	 * 所有设备
	 */
	protected Map<String, Channel> channels;

	/**
	 * 设备心跳
	 */
	protected Heartbeat heartbeat;

	/**
	 * 输入输出线程池组
	 */
	protected EventLoopGroup eventLoops;

	/**
	 * 初始化channel处理器
	 */
	protected Consumer<Channel> channelInit;

	/**
	 * 是否已经关闭
	 */
	private AtomicBoolean closed = new AtomicBoolean(true);

	/**
	 * 创建一个设备管理器
	 *
	 * @param works
	 * @param heartbeatSeconds
	 * @param channelInit
	 */
	public Channels(int works, int heartbeatSeconds, Consumer<Channel> channelInit) {
		this.channels = new ConcurrentHashMap<>(works * 2);
		this.heartbeat = new Heartbeat(heartbeatSeconds);
		this.eventLoops = new EventLoopGroup(works);
		this.channelInit = channelInit;
		_ME = this;

		// 关闭系统时，关闭设备来释放资源
		ShutDownHook.registerShutdownHook(() -> {
			this.close();
			this.heartbeat.shutdown();
			this.eventLoops.shutdown();
		});
	}

	/**
	 * 是否运行中
	 *
	 * @return
	 */
	public boolean isRunning() {
		return !this.closed.get();
	}

	/**
	 * 启动
	 */
	public void start() {
		if (closed.compareAndSet(true, false)) {
			this.heartbeat.start();
		}
	}

	/**
	 * 尝试加载所有的设备
	 */
	private void scanChannels() {
		List<String> comms = SerialUtils.getCommNames();
		if (comms != null && comms.size() > 0) {
			for (String comm : comms) {
				channels.computeIfAbsent(comm, (v) -> this.scanChannel(comm));
			}
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
		if (!closed.get()) {
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
	}

	/**
	 * 刷新设备
	 */
	public void close() {
		if (closed.compareAndSet(false, true)) {
			/**
			 * 刷新所有设备
			 */
			channels.forEach((comm, device) -> {
				device.close();
			});

			/**
			 * 删除设备
			 */
			channels.values().removeIf(device -> {
				return !device.isActive();
			});
		}
	}

	/**
	 * 删除通道
	 * 
	 * @param channel
	 */
	public void remove(Channel channel) {
		channels.remove(channel.comm());
	}

	/**
	 * 设备心跳, 发现新设备
	 */
	private void heartbeat() {
		if (!closed.get()) {
			// 设备心跳
			if (logger.isDebugEnabled()) {
				logger.debug("设备心跳，设备数:[{}]", channels.size());
			}

			// 尝试发现新设备
			this.scanChannels();

			// 处理设备的心跳
			channels.forEach((comm, channel) -> {
				channel.heartbeat();
			});
		}
	}

	/**
	 * 心跳
	 */
	class Heartbeat implements Runnable {

		/**
		 * 数据处理线程
		 */
		private ScheduledContext heartbeat;
		private int heartbeatSeconds;

		public Heartbeat(int heartbeatSeconds) {
			this.heartbeatSeconds = heartbeatSeconds;
		}

		@Override
		public void run() {
			Channels.this.heartbeat();
		}

		/**
		 * 启动
		 */
		private void start() {
			if (heartbeat == null && heartbeatSeconds > 0) {
				heartbeat = Contexts.createScheduledContext("Channels.Heartbeat-", 1, true, 30 * 2, TimeUnit.SECONDS);
				heartbeat.scheduleAtFixedRate(this, 0, heartbeatSeconds, TimeUnit.SECONDS);
			}
		}

		/**
		 * 关闭
		 */
		private void shutdown() {
			if (heartbeat != null) {
				heartbeat.shutdown();
			}
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