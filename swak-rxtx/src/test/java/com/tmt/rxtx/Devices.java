package com.tmt.rxtx;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.swak.reactivex.threads.Contexts;
import com.swak.reactivex.threads.WorkerContext;
import com.swak.rxtx.SerialUtils;
import com.tmt.rxtx.enums.Status;
import com.tmt.rxtx.message.ReqMsg_StartCollection;
import com.tmt.rxtx.message.ReqMsg_StartUpload;
import com.tmt.rxtx.message.ReqMsg_StopCollection;
import com.tmt.rxtx.message.ReqMsg_StopUpload;
import com.tmt.rxtx.message.RespMsg_Cmd;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 设备管理器 -- 所有命令只能通过命令来访问
 * 
 * @author lifeng
 */
@Getter
@Setter
@Accessors(chain = true)
public class Devices implements Monitor {

	/**
	 * 所有设备
	 */
	private Map<String, Device> devices;

	/**
	 * 数据缓存
	 */
	private BlockingQueue<Data> datas;

	/**
	 * 数据处理线程
	 */
	private WorkerContext context;

	/**
	 * 数据处理状态
	 */
	private volatile AtomicBoolean sending = new AtomicBoolean(false);

	/**
	 * 设备管理器
	 */
	public Devices() {
		devices = new ConcurrentHashMap<>();
		datas = new LinkedBlockingDeque<>();
		context = Contexts.createWorkerContext("Devices-", 1, true, 30 * 2, TimeUnit.SECONDS);
	}

	/**
	 * 刷新设备
	 */
	public void refresh() {
		List<String> comms = SerialUtils.getCommNames();
		for (String comm : comms) {
			Device device = devices.computeIfAbsent(comm, (v) -> Device.of(comm).setMonitor(this));
			if (device.getStatus() == Status.断开 || device.getStatus() == Status.异常) {
				device.connect();
			}
		}
	}

	/**
	 * 所有设备开始采集
	 */
	public void startCollection() {
		devices.forEach((comm, device) -> {
			device.sendCommand(ReqMsg_StartCollection.of().encode());
		});
	}

	/**
	 * 所有设备开始采集
	 */
	public void stopCollection() {
		devices.forEach((comm, device) -> {
			device.sendCommand(ReqMsg_StopCollection.of().encode());
		});
	}

	/**
	 * 所有设备开始采集
	 */
	public void startUpload() {
		devices.forEach((comm, device) -> {
			device.sendCommand(ReqMsg_StartUpload.of().encode());
		});
	}

	/**
	 * 所有设备开始采集
	 */
	public void stopUpload() {
		devices.forEach((comm, device) -> {
			device.sendCommand(ReqMsg_StopUpload.of().encode());
		});
	}

	/**
	 * 收到数据
	 */
	@Override
	public void receiveDataListener(Device device, byte[] data) {

		// 加入缓存
		datas.add(new Data().setData(data).setDevice(device));

		// 开启任务
		this.prepareTask();
	}

	/**
	 * 添加一次执行任务
	 */
	private void prepareTask() {
		if (sending.compareAndSet(false, true)) {
			context.execute(new DataHandlers());
		}
	}

	/**
	 * Helper class to actually send LoggingEvents asynchronously.
	 */
	protected class DataHandlers implements Runnable {

		@Override
		public void run() {
			try {
				while (true) {

					// 会去需要发送的数据
					final Data event = datas.poll();
					if (event == null) {
						break;
					}

					// 发送
					this.doHandler(event);
				}
			} finally {
				this.prepareNextTask();
			}
		}

		/**
		 * 处理数据
		 * 
		 * @param event
		 */
		private void doHandler(final Data event) {
			event.parse();
		}

		/**
		 * 准备下一次的执行
		 */
		private void prepareNextTask() {
			if (sending.compareAndSet(true, false) && datas.peek() != null) {
				prepareTask();
			}
		}
	}

	/**
	 * 数据
	 * 
	 * @author lifeng
	 */
	@Getter
	@Setter
	@Accessors(chain = true)
	protected static class Data implements Encodes {
		Device device;
		byte[] data;

		/**
		 * 解析请求:解析请求与设备状态有关系，如果设备当前是上传中状态且没有结束，则需要继续上上传处理
		 * 
		 * @param message
		 * @return
		 */
		public RespMsg_Cmd parse() {
			// 格式化数据
			String message = this.encodeHex(data);
			System.out.println("收集到的数据:-------------------开始");
			System.out.println(message);
			System.out.println("收集到的数据:-------------------结束");
			return null;
		}
	}
}