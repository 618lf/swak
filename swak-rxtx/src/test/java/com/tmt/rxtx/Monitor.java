package com.tmt.rxtx;

import com.tmt.rxtx.Device;

/**
 * 命令监听
 * 
 * @author lifeng
 */
public interface Monitor {

	/**
	 * 收到数据
	 * 
	 * @param device
	 * @param data
	 */
	void receiveDataListener(Device device, byte[] data);
}
