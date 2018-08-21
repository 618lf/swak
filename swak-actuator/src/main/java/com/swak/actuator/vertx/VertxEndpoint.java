package com.swak.actuator.vertx;

import java.util.Set;

import com.swak.actuator.endpoint.annotation.Endpoint;
import com.swak.actuator.endpoint.annotation.Operation;
import com.swak.actuator.endpoint.annotation.Selector;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.dropwizard.MetricsService;

/**
 * 直接获取 vertx 的内容
 * @author lifeng
 */
@Endpoint(id = "vertx")
public class VertxEndpoint {
	
	private MetricsService metricsService;
	
	public VertxEndpoint(Vertx vertx) {
		metricsService = MetricsService.create(vertx);
	}
	
	/**
	 * 获得所有可监控的指标
	 * @return
	 */
	@Operation
	public Set<String> metricsNames() {
		return metricsService.metricsNames();
	}
	
	/**
	 * 获得所有可监控的指标
	 * @return
	 */
	@Operation
	public JsonObject metricsNames(@Selector String name) {
		System.out.println("参数：" + name);
		return metricsService.getMetricsSnapshot(name);
	}
}