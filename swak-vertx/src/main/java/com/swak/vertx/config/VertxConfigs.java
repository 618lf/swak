package com.swak.vertx.config;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.swak.utils.Maps;
import com.swak.utils.Sets;

/**
 * 配置项
 * 
 * @author lifeng
 * @date 2020年8月23日 下午11:52:49
 */
public class VertxConfigs {

	public static VertxConfigs ME = new VertxConfigs();

	public static VertxConfigs me() {
		return ME;
	}

	private final Set<ServiceBean> services = Sets.newOrderSet();
	private final Map<Integer, List<RouterBean>> routers = Maps.newOrderMap();
	private final Map<Integer, List<WebSocketBean>> webSockets = Maps.newOrderMap();
	private final Set<IRouterConfig> routerConfigs = Sets.newOrderSet();

	private VertxConfigs() {
	}

	public Set<ServiceBean> getServices() {
		return services;
	}

	public Map<Integer, List<RouterBean>> getRouters() {
		return routers;
	}

	public Set<IRouterConfig> getRouterConfigs() {
		return routerConfigs;
	}

	public Map<Integer, List<WebSocketBean>> getWebSockets() {
		return webSockets;
	}

	public VertxConfigs add(AbstractConfig bean) {
		if (bean instanceof ServiceBean) {
			this.add((ServiceBean) bean);
		} else if (bean instanceof RouterBean) {
			this.add((RouterBean) bean);
		} else if (bean instanceof WebSocketBean) {
			this.add((WebSocketBean) bean);
		} else if (bean instanceof IRouterConfig) {
			this.add((IRouterConfig) bean);
		}
		return this;
	}

	public VertxConfigs add(ServiceBean bean) {
		services.add(bean);
		return this;
	}

	public VertxConfigs add(RouterBean bean) {
		List<RouterBean> beans = routers.get(bean.getPort());
		if (beans == null) {
			beans = new LinkedList<>();
			routers.put(bean.getPort(), beans);
		}
		beans.add(bean);
		return this;
	}

	public VertxConfigs add(WebSocketBean bean) {
		List<WebSocketBean> beans = webSockets.get(bean.getPort());
		if (beans == null) {
			beans = new LinkedList<>();
			webSockets.put(bean.getPort(), beans);
		}
		beans.add(bean);
		return this;
	}

	public VertxConfigs add(IRouterConfig bean) {
		routerConfigs.add(bean);
		return this;
	}
}
