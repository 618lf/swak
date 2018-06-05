package com.swak.reactivex.transport.http.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;
import com.swak.reactivex.transport.TransportMode;

/**
 * 服务器的默认配置
 * 
 * @author lifeng
 */
@ConfigurationProperties(prefix = Constants.HTTP_CLIENT_PREFIX)
public class HttpClientProperties {

	private String name = "SWAK-REACTIVE-SERVER";
	private TransportMode mode = TransportMode.NIO;
	
	// 线程数量
	private int serverWorker = -1; //  自动计算
	
	public int getServerWorker() {
		return serverWorker;
	}
	public void setServerWorker(int serverWorker) {
		this.serverWorker = serverWorker;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public TransportMode getMode() {
		return mode;
	}
	public void setMode(TransportMode mode) {
		this.mode = mode;
	}
}
