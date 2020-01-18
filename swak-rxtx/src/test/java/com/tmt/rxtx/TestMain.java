package com.tmt.rxtx;

/**
 * 键单的测试
 * 
 * @ClassName: TestMain
 * @Description:TODO(描述这个类的作用)
 * @author: lifeng
 * @date: Jan 3, 2020 10:28:13 AM
 */
public class TestMain {

	public static void main(String[] args) throws Exception {

		// 设备管理器
		Devices devices = new Devices();

		// 刷新所有设备
		devices.refresh();

		// 等待10s
		Thread.sleep(10000);

		// 发送采集
		devices.startCollection();

		// 等待10s
		Thread.sleep(10000);

		// 停止采集
		devices.stopCollection();

		// 等待10s
		Thread.sleep(10000);

		// 开始上传
		devices.startUpload();
	}
}
