package com.swak.rxtx.channel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.rxtx.enums.Status;
import com.swak.rxtx.utils.SerialUtils;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 数据通讯管道
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
	private byte[] readBuffer;
	private EventLoop eventLoop;
	private SerialPort sport;
	private ChannelPipeline pipeline = new ChannelPipeline();
	private volatile Status status = Status.断开;

	// ######### 外部可设置参数 ###################
	public Channel property(Property property) {
		this.property = property;
		this.readBuffer = new byte[this.property.readSizeOnce];
		return this;
	}

	public ChannelPipeline pipeline() {
		return this.pipeline;
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
		this.property = new Property();
		this.readBuffer = new byte[this.property.readSizeOnce];
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

			// 发送事件
			this.pipeline.fireConnectEvent(this);
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
	public void read() {
		if (!inEventLoop()) {
			this.eventLoop.execute(() -> {
				this.read();
			});
		} else {

			// 设置为不主动推动消息
			this.sport.notifyOnDataAvailable(false);

			// 读取数据并触发消息处理
			this.doRead();

			// 设置为不主动推动消息
			this.sport.notifyOnDataAvailable(true);
		}
	}

	/**
	 * 读取串口返回信息
	 */
	private void doRead() {
		try {
			InputStream inputStream = sport.getInputStream();
			int len = 0;
			while ((len = inputStream.read(readBuffer)) > 0) {

				// 获取读取到的数据
				byte[] data = Arrays.copyOfRange(readBuffer, 0, len);

				// 触发读取操作
				this.pipeline.fireReadEvent(this, data);

				if (logger.isDebugEnabled()) {
					logger.debug("收到设备反馈：[{}]，数据长度:[{}]", this.comm, len);
				}
			}
		} catch (IOException e) {
			logger.error("读取串口信息时发生异常", e);
			this.status = Status.异常;
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

				// 只能写入字节数组
				byte[] command = null;
				if (command instanceof byte[]) {
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
					logger.debug("发送命令给设备：[{}]", this.comm);
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
				if (sport != null) {
					sport.notifyOnDataAvailable(false);
					sport.removeEventListener();
					sport.close();
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
	private boolean inEventLoop() {
		return this.eventLoop.inEventLoop();
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
	}
}
