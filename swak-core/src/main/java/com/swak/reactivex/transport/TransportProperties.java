package com.swak.reactivex.transport;

import com.swak.OS;

/**
 * 协议的通用配置
 * 
 * @author lifeng
 */
public class TransportProperties {

	private TransportMode mode = TransportMode.OS;

	public TransportMode getMode() {
		if (TransportMode.OS == mode) {
			return this.getModeByOS();
		}
		return mode;
	}

	public TransportMode getModeByOS() {
		OS os = OS.me();
		if (os == OS.linux) {
			return TransportMode.EPOLL;
		}
		return TransportMode.NIO;
	}

	public void setMode(TransportMode mode) {
		this.mode = mode;
	}
}