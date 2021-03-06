package com.swak.rxtx.channel;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.rxtx.Channels;
import com.swak.rxtx.enums.Status;
import com.swak.rxtx.utils.SerialUtils;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 数据通讯 管道 -- 只能单线程访问
 * 
 * @author lifeng
 */
public class Channel {

	/**
	 * 用于消息调试
	 */
	private static Logger logger = LoggerFactory.getLogger(Channel.class);

	private final String comm;
	private Property property;
	private EventLoop eventLoop;
	private SerialPort sport;
	private ChannelPipeline pipeline;
	private volatile Status status = Status.断开;

	// 优化的可复用的緩存區
	private ByteBufAllocator alloc;

	// ######### 外部可设置参数 ###################
	public Channel property(Property property) {
		this.property = property;
		this.alloc = this.property.alloc;
		return this;
	}

	public ChannelPipeline pipeline() {
		return this.pipeline;
	}

	public ByteBufAllocator alloc() {
		return this.alloc;
	}

	public String comm() {
		return comm;
	}

	public boolean isActive() {
		return status == Status.连接;
	}
	// ########################################

	/**
	 * 基本的数据
	 * 
	 * @param comm
	 * @param property
	 */
	public Channel(String comm) {
		this.comm = comm;
		this.pipeline = new ChannelPipeline(this);
		this.property(new Property());
	}

	/**
	 * 注册到指定的eventLoop上去
	 */
	public Channel register(EventLoop eventLoop) {
		this.eventLoop = eventLoop;
		return this;
	}

	/**
	 * 连接
	 */
	public void connect() {
		if (!inEventLoop()) {
			this.eventLoop.execute(() -> {
				this.connect();
			});
		} else {

			// 先关闭
			if (this.sport != null) {
				this.close();
			}

			// 重新连接
			this.doConnect();

			// 连接事件
			if (this.isActive()) {
				this.pipeline.fireConnectEvent(this);
			}
			// 关闭连接事件
			else {
				Channels.me().remove(this);
				this.pipeline.fireCloseEvent(this);
			}
		}
	}

	/**
	 * 执行连接
	 */
	private void doConnect() {
		// 连接设备
		try {

			// 关键属性校验
			if (this.property == null) {
				logger.error("请初始化设备连接属性：[{}]", comm);
				return;
			}

			// 连接设备
			this.sport = SerialUtils.connect(this.comm, property.baudRate, property.dataBit, property.stopBit,
					property.parityBit);
			this.sport.setInputBufferSize(property.readBufferSize);
			this.sport.setOutputBufferSize(property.writeBufferSize);
			this.sport.addEventListener((event) -> {
				this.autoRead(event);
			});
			this.sport.notifyOnDataAvailable(true);
			this.sport.notifyOnCTS(true);
			this.sport.notifyOnDSR(true);
			this.sport.notifyOnRingIndicator(true);
			this.sport.notifyOnCarrierDetect(true);
			this.sport.notifyOnOverrunError(true);
			this.sport.notifyOnParityError(true);
			this.sport.notifyOnFramingError(true);
			this.sport.notifyOnBreakInterrupt(true);
			this.status = Status.连接;

			if (logger.isDebugEnabled()) {
				logger.debug("设备上线：[{}]", this.comm);
			}
		} catch (PortInUseException e) {
			this.status = Status.断开;
		} catch (NoSuchPortException e) {
			this.status = Status.断开;
		} catch (Exception e) {
			logger.error("连接串口设备异常：[{}]", comm, e);
			this.status = Status.断开;
		}
	}

	/**
	 * 自动读取
	 * 
	 * @param event
	 */
	private void autoRead(SerialPortEvent event) {
		// 记录设备事件处理
		if (logger.isDebugEnabled()) {
			logger.debug("收到设备事件：[{}]，事件:[{}]", this.comm, event.getEventType());
		}

		// 只需要处理读取通知
		if (SerialPortEvent.DATA_AVAILABLE != event.getEventType()) {
			return;
		}

		// 转为主动读取
		this.read();
	}

	/**
	 * 读取到数据
	 * 
	 * @param data
	 */
	private void read() {
		if (!inEventLoop()) {
			this.eventLoop.execute(() -> {
				this.read();
			});
		} else {
			this.doRead();
		}
	}

	/**
	 * 读取串口返回信息
	 * 
	 * @throws IOException
	 */
	private void doRead() {
		try {
			// 设置为不主动推动消息
			this.sport.notifyOnDataAvailable(false);

			// 读取数据并触发消息处理
			while (true) {

				// 分配一块空间
				ByteBuf byteBuf = this.alloc.buffer(this.property.readSizeOnce);

				// 读取数据
				int len = byteBuf.writeBytes(sport.getInputStream(), byteBuf.capacity());

				// 打印提示
				if (logger.isDebugEnabled()) {
					logger.debug("收到设备反馈：[{}]，读取数据长度:[{}]", this.comm, len);
				}

				// 触发读取操作
				this.pipeline.fireReadEvent(this, byteBuf);

				// 如果没有数据则不需要处理
				if (len <= 0) {
					break;
				}
			}
		} catch (Exception e) {
			logger.error("读取串口信息时发生异常", e);
			this.status = Status.异常;
		} finally {
			// 设置为主动推动消息
			this.sport.notifyOnDataAvailable(true);
		}
	}

	/**
	 * 写数据
	 * 
	 * @param data
	 */
	public void write(Object data) {
		if (!inEventLoop()) {
			this.eventLoop.execute(() -> {
				this.write(data);
			});
		} else {
			this.pipeline.fireWriteEvent(this, data);
		}
	}

	/**
	 * 处理心跳
	 */
	public void heartbeat() {
		if (!inEventLoop()) {
			this.eventLoop.execute(() -> {
				this.heartbeat();
			});
		} else {

			// 如果是断开的
			if (this.status == Status.断开) {
				this.connect();
				return;
			}

			// 否则需触发心跳处理
			this.pipeline.fireHeartbeatEvent(this);
		}
	}

	/**
	 * 最终的输出数据
	 * 
	 * @param data
	 */
	void writeAndFlush(Object data) {
		if (!inEventLoop()) {
			this.eventLoop.execute(() -> {
				this.writeAndFlush(data);
			});
		} else {
			try {

				// 断开状态不能写数据
				if (this.status == Status.断开) {
					logger.error("设备已经断开");
					return;
				}

				// 只能写入字节数组
				byte[] command = null;
				if (data instanceof byte[]) {
					command = (byte[]) data;
				}

				// 仅仅提示
				if (command == null) {
					logger.error("命令格式不正确，请转为字节数组");
					return;
				}

				OutputStream outputStream = sport.getOutputStream();
				outputStream.write(command);
				outputStream.flush();

				if (logger.isDebugEnabled()) {
					logger.debug("发送命令给设备：[{}], 命令长度:[{}]", this.comm, command.length);
				}
			} catch (IOException e) {
				logger.error("发送信息到串口时发生异常", e);
				this.status = Status.断开;
			} catch (Exception e) {
				logger.error("发送信息到串口时发生异常", e);
				this.status = Status.异常;
			}
		}
	}

	/**
	 * 关闭
	 */
	public void close() {
		if (!inEventLoop()) {
			this.eventLoop.execute(() -> {
				this.close();
			});
		} else {
			try {
				if (sport != null && this.status != Status.断开) {
					sport.notifyOnDataAvailable(false);
					sport.removeEventListener();
					sport.close();
					sport = null;
					this.status = Status.断开;
					this.pipeline.fireCloseEvent(this);
				}
				sport = null;
				this.status = Status.断开;

				if (logger.isDebugEnabled()) {
					logger.debug("设备关闭：[{}]", this.comm);
				}
			} catch (Exception e) {
				logger.error("发送信息到串口时发生异常", e);
			}
		}
	}

	/**
	 * 当前是否运行在EventLoop下
	 * 
	 * @return
	 */
	public boolean inEventLoop() {
		return this.eventLoop.inEventLoop();
	}

	/**
	 * 在EventLoop中运行
	 * 
	 * @param task
	 */
	public void execute(Runnable task) {
		this.eventLoop.execute(task);
	}

	/**
	 * 设备属性
	 * 
	 * @author lifeng
	 */
	@Getter
	@Setter
	@Accessors(chain = true)
	public static class Property {
		private int baudRate = 9600; // 9600波特率
		private int dataBit = SerialPort.DATABITS_8; // 8 数据位
		private int stopBit = SerialPort.STOPBITS_1; // 1 停止位
		private int parityBit = SerialPort.PARITY_NONE; // 0 校验位
		private int readBufferSize = 8192; // 缓冲区大小
		private int writeBufferSize = 1024; // 缓冲区大小
		private int readSizeOnce = 10; // 每次读取的大小
		private ByteBufAllocator alloc = PooledByteBufAllocator.DEFAULT; // 缓存分配策略
	}
}
