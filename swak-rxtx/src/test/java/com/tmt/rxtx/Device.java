package com.tmt.rxtx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.rxtx.SerialUtils;
import com.swak.utils.IOUtils;
import com.swak.utils.StringUtils;
import com.tmt.rxtx.config.Config;
import com.tmt.rxtx.enums.Status;
import com.tmt.rxtx.message.BaseMsg;
import com.tmt.rxtx.message.ReqMsg_GetMac;
import com.tmt.rxtx.message.RespMsg_Cmd;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 设备
 * 
 * @author lifeng
 */
@Getter
@Setter
@Accessors(chain = true)
public class Device implements SerialPortEventListener, Encodes {

	private static Logger logger = LoggerFactory.getLogger(Device.class);
	private Status status = Status.断开;
	private String name;
	private SerialPort sport;
	private Monitor monitor;

	// 缓存数据（用于缓存上传中的数据，长度不够处理的）
	private String cached;

	/**
	 * 连接到设备
	 */
	public void connect() {

		// 连接设备
		try {

			// 先关闭
			if (this.sport != null) {
				this.close();
			}

			// 连接设备
			this.sport = SerialUtils.connect(this.name, Config.baudRate, Config.dataBit, Config.stopBit,
					Config.parityBit);
			this.sport.setInputBufferSize(Config.readBufferSize);
			this.sport.setOutputBufferSize(Config.writeBufferSize);
			this.sport.addEventListener(this);
			this.sport.notifyOnDataAvailable(true);
			this.status = Status.连接;

			if (logger.isDebugEnabled()) {
				logger.debug("设备上线：[{}]", this.name);
			}
		} catch (Exception e) {
			logger.error("连接串口设备异常：[{}]", name, e);
			this.status = Status.断开;
		}

		// 发送初始消息
		if (this.status == Status.连接) {
			this.sendCommand(ReqMsg_GetMac.of().encode());
		}
	}

	/**
	 * 设备事件处理
	 */
	@Override
	public void serialEvent(SerialPortEvent event) {

		// 记录设备事件处理
		if (logger.isDebugEnabled()) {
			logger.debug("收到设备事件：[{}]，事件:[{}]", this.name, event.getEventType());
		}

		// 设备事件处理
		switch (event.getEventType()) {
		case SerialPortEvent.BI: // 通讯中断
		case SerialPortEvent.OE: // 溢位错误
		case SerialPortEvent.FE: // 帧错误
		case SerialPortEvent.PE: // 奇偶校验错误
			this.status = Status.异常;
			break;
		case SerialPortEvent.CD: // 载波检测
		case SerialPortEvent.CTS: // 清除发送
		case SerialPortEvent.DSR: // 数据设备准备好
		case SerialPortEvent.RI: // 响铃侦测
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 输出缓冲区已清空
			break;
		case SerialPortEvent.DATA_AVAILABLE: // 有数据到达
			// 调用读取数据的方法
			readComm();
			break;
		default:
			break;
		}
	}

	/**
	 * 读取串口返回信息
	 */
	private void readComm() {
		InputStream inputStream = null;
		try {
			inputStream = sport.getInputStream();
			byte[] readBuffer = new byte[inputStream.available()];
			int len = 0;
			if ((len = inputStream.read(readBuffer)) != -1) {
				// 这里有个问题，传递的数据应该是连续的，不应该被分断
				byte[] data = ByteBuffer.wrap(readBuffer, 0, len).array();
				monitor.receiveDataListener(this, data);

				if (logger.isDebugEnabled()) {
					logger.debug("收到设备反馈：[{}]，数据长度:[{}]", this.name, len);
				}
			}
		} catch (IOException e) {
			logger.error("发送信息到串口时发生异常", e);
			this.status = Status.异常;
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	/**
	 * 发送信息到串口
	 */
	public void sendCommand(byte[] command) {
		OutputStream outputStream = null;
		try {
			outputStream = sport.getOutputStream();
			outputStream.write(command);
			outputStream.flush();

			if (logger.isDebugEnabled()) {
				logger.debug("发送命令给设备：[{}]，命令[{}]", this.name, this.encodeHex(command));
			}
		} catch (Exception e) {
			logger.error("发送信息到串口时发生异常", e);
			this.status = Status.异常;
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
	}

	/**
	 * 关闭
	 */
	public void close() {
		try {
			if (sport != null) {
				sport.notifyOnDataAvailable(false);
				sport.removeEventListener();
				sport.close();
			}
			sport = null;
			this.status = Status.断开;

			if (logger.isDebugEnabled()) {
				logger.debug("设备关闭：[{}]", this.name);
			}
		} catch (Exception e) {
			logger.error("发送信息到串口时发生异常", e);
		}
	}

	/**
	 * 消息
	 * 
	 * @param message
	 * @return
	 */
	public BaseMsg parseWithCache(String message) {

		// 尝试处理新数据
		BaseMsg baseMsg = BaseMsg.parse(message);

		// 带有命令的数据
		if (baseMsg instanceof RespMsg_Cmd) {
			this.cached = null;
			return baseMsg;
		}

		// 尝试处理合并数据
		String total = StringUtils.isNotBlank(this.cached) ? new StringBuilder(this.cached).append(message).toString()
				: message;
		this.cached = null;

		// 没有命令的数据，缓存起来
		this.cached = total;
		return null;
	}

	/**
	 * 创建设备对象
	 * 
	 * @param comm
	 * @return
	 */
	public static Device of(String comm) {
		Device device = new Device();
		device.name = comm;
		return device;
	}
}
