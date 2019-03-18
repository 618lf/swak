package com.swak.actuator.vertx;

import java.util.List;
import java.util.Set;

import com.swak.actuator.endpoint.annotation.Endpoint;
import com.swak.actuator.endpoint.annotation.Operation;
import com.swak.actuator.endpoint.annotation.Selector;
import com.swak.utils.Sets;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.micrometer.MetricsService;

/**
 * 直接获取 vertx 的内容
 * @author lifeng
 */
@Endpoint(id = "vertx")
public class VertxEndpoint {
	
	private MetricsService metricsService;
	private Router mainRouter;
	
	public VertxEndpoint(Vertx vertx, Router mainRouter) {
		metricsService = MetricsService.create(vertx);
		this.mainRouter = mainRouter;
	}
	
	/**
	 * 获得所有可监控的指标
	 * @return
	 */
	@Operation
	public Set<String> routes() {
		Set<String> routeDescs = Sets.newHashSet();
		List<Route> routes = mainRouter.getRoutes();
		for(Route route: routes) {
			routeDescs.add(route.getPath());
		}
		return routeDescs;
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
		return metricsService.getMetricsSnapshot(name);
	}
}