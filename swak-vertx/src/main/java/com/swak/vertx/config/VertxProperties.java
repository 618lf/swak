package com.swak.vertx.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.reactivex.transport.TransportMode;
import com.swak.utils.Maps;

import io.vertx.core.impl.cpu.CpuCoreSensor;

/**
 * Vertx 的属性配置
 * 
 * @author lifeng
 */
@ConfigurationProperties(prefix = "spring.vertx")
public class VertxProperties {

	private int port = 8080;
	private TransportMode mode = TransportMode.NIO;
	private int eventLoopPoolSize = 2 * CpuCoreSensor.availableProcessors();
	private int workerThreads = 20;
	private Map<String, Integer> workers = Maps.newHashMap();

	public Map<String, Integer> getWorkers() {
		return workers;
	}

	public void setWorkers(Map<String, Integer> workers) {
		this.workers = workers;
	}

	public int getEventLoopPoolSize() {
		return eventLoopPoolSize;
	}

	public void setEventLoopPoolSize(int eventLoopPoolSize) {
		this.eventLoopPoolSize = eventLoopPoolSize;
	}

	public TransportMode getMode() {
		return mode;
	}

	public void setMode(TransportMode mode) {
		this.mode = mode;
	}

	public int getWorkerThreads() {
		return workerThreads;
	}

	public void setWorkerThreads(int workerThreads) {
		this.workerThreads = workerThreads;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}