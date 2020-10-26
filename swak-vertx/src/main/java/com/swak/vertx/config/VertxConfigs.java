package com.swak.vertx.config;

import java.util.Iterator;
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
	private Map<Integer, BeansConfig> unifys = Maps.newOrderMap();
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

	public Map<Integer, BeansConfig> getUnifys() {
		return unifys;
	}

	public void setUnifys(Map<Integer, BeansConfig> unifys) {
		this.unifys = unifys;
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
	 * 合并默认的端口
	 * 
	 * @param httpPort  http 默认端口
	 * @param imPort    im 默认端口
	 * @param instances 默认实例
	 * @return 合并之后的配置项
	 */
	public VertxConfigs mergeDefaults(int httpPort, int imPort, int instances) {

		// 1. http 端口处理 合并所有的默认配置项
		BeansConfig defaultConfig = new BeansConfig();
		Iterator<Integer> ports = routers.keySet().iterator();
		while (ports.hasNext()) {
			Integer port = ports.next();
			routers.get(port).merge(instances);
			if (port <= 0) {
				defaultConfig.merge(routers.remove(port));
			}
		}
		if (routers.containsKey(httpPort)) {
			defaultConfig.merge(routers.remove(httpPort));
		}
		if (!defaultConfig.getBeans().isEmpty()) {
			routers.putIfAbsent(httpPort, defaultConfig);
		}

		// 2. Im 端口处理 合并所有的默认配置项
		defaultConfig = new BeansConfig();
		ports = webSockets.keySet().iterator();
		while (ports.hasNext()) {
			Integer port = ports.next();
			webSockets.get(port).merge(instances);
			if (port <= 0) {
				defaultConfig.merge(webSockets.remove(port));
			}
		}
		if (webSockets.containsKey(imPort)) {
			defaultConfig.merge(webSockets.remove(imPort));
		}
		if (!defaultConfig.getBeans().isEmpty()) {
			webSockets.putIfAbsent(imPort, defaultConfig);
		}
		// 3. 统一处理器
		routers.keySet().forEach(port -> {
			if (webSockets.containsKey(port)) {
				BeansConfig unifyConfig = unifys.computeIfAbsent(port, (key) -> {
					return new BeansConfig();
				});
				unifyConfig.instances = Ints.max(routers.get(port).instances, webSockets.get(port).instances);
			}
		});

		// 当前配置
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

		public BeansConfig merge(BeansConfig config) {
			this.beans.addAll(config.beans);
			this.instances = Ints.max(this.instances, config.instances);
			return this;
		}

		public BeansConfig merge(int instances) {
			this.instances = this.instances <= 0 ? instances : this.instances;
			return this;
		}
	}
}
