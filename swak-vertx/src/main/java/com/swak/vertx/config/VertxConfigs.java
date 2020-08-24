package com.swak.vertx.config;

import java.util.Set;

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
	private final Set<RouterBean> routers = Sets.newOrderSet();
	private final Set<IRouterSupplier> routerSuppliers = Sets.newOrderSet();
	private final Set<IRouterConfig> routerConfigs = Sets.newOrderSet();

	private VertxConfigs() {
	}

	public Set<ServiceBean> getServices() {
		return services;
	}

	public Set<RouterBean> getRouters() {
		return routers;
	}

	public Set<IRouterSupplier> getRouterSuppliers() {
		return routerSuppliers;
	}

	public Set<IRouterConfig> getRouterConfigs() {
		return routerConfigs;
	}

	public VertxConfigs add(AbstractConfig bean) {
		if (bean instanceof ServiceBean) {
			this.add((ServiceBean) bean);
		} else if (bean instanceof RouterBean) {
			this.add((RouterBean) bean);
		} else if (bean instanceof IRouterSupplier) {
			this.add((IRouterSupplier) bean);
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
		routers.add(bean);
		return this;
	}

	public VertxConfigs add(IRouterSupplier bean) {
		routerSuppliers.add(bean);
		return this;
	}

	public VertxConfigs add(IRouterConfig bean) {
		routerConfigs.add(bean);
		return this;
	}
}
