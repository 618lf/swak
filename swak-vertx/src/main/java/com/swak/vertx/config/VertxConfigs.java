package com.swak.vertx.config;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.swak.utils.Ints;
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
	private final Map<Integer, BeansConfig> routers = Maps.newOrderMap();
	private final Map<Integer, BeansConfig> webSockets = Maps.newOrderMap();
	private final Set<RouterConfig> routerConfigs = Sets.newOrderSet();
	private final Set<ImConfig> imConfigs = Sets.newOrderSet();

	private VertxConfigs() {
	}

	public Set<ServiceBean> getServices() {
		return services;
	}

	public Map<Integer, BeansConfig> getRouters() {
		return routers;
	}

	public Set<RouterConfig> getRouterConfigs() {
		return routerConfigs;
	}

	public Map<Integer, BeansConfig> getWebSockets() {
		return webSockets;
	}

	public Set<ImConfig> getImConfigs() {
		return imConfigs;
	}

	public VertxConfigs add(AbstractConfig bean) {
		if (bean instanceof ServiceBean) {
			this.add((ServiceBean) bean);
		} else if (bean instanceof RouterBean) {
			this.add((RouterBean) bean);
		} else if (bean instanceof ImBean) {
			this.add((ImBean) bean);
		} else if (bean instanceof RouterConfig) {
			this.add((RouterConfig) bean);
		} else if (bean instanceof ImConfig) {
			this.add((ImConfig) bean);
		}
		return this;
	}

	public VertxConfigs add(ServiceBean bean) {
		services.add(bean);
		return this;
	}

	public VertxConfigs add(RouterBean bean) {
		BeansConfig beanConfig = routers.computeIfAbsent(bean.getPort(), (port) -> {
			return new BeansConfig();
		});
		beanConfig.add(bean);
		return this;
	}

	public VertxConfigs add(ImBean bean) {
		BeansConfig beanConfig = webSockets.computeIfAbsent(bean.getPort(), (port) -> {
			return new BeansConfig();
		});
		beanConfig.add(bean);
		return this;
	}

	public VertxConfigs add(RouterConfig bean) {
		routerConfigs.add(bean);
		return this;
	}

	public VertxConfigs add(ImConfig bean) {
		imConfigs.add(bean);
		return this;
	}

	/**
	 * Bean定义
	 */
	public static class BeansConfig {
		private int instances = -1;
		private List<AbstractBean> beans = new LinkedList<>();

		public int getInstances() {
			return instances;
		}

		public void setInstances(int instances) {
			this.instances = instances;
		}

		@SuppressWarnings("unchecked")
		public <T> List<T> getBeans() {
			return (List<T>) beans;
		}

		public void setBeans(List<AbstractBean> beans) {
			this.beans = beans;
		}

		public BeansConfig add(AbstractBean bean) {
			this.beans.add(bean);
			this.instances = Ints.max(this.instances, bean.getInstances());
			return this;
		}
	}
}
