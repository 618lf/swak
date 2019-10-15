package com.swak.config.vertx;

import java.util.Map;

import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.vertx.security.handler.Handler;
import com.swak.vertx.security.realm.Realm;
import com.swak.vertx.security.realm.SimpleRealm;

/**
 * 安全的配置项目
 * 
 * @author lifeng
 */
public class SecurityConfigurationSupport {

	private Map<String, Handler> handlers;
	private Realm realm = new SimpleRealm();
	private Map<String, String> definitions;

	public Map<String, Handler> getHandlers() {
		return handlers;
	}

	public Realm getRealm() {
		return realm;
	}

	public Map<String, String> getDefinitions() {
		return definitions;
	}

	/**
	 * 设置域对象
	 * 
	 * @param realm
	 * @return
	 */
	public SecurityConfigurationSupport setRealm(Realm realm) {
		this.realm = realm;
		return this;
	}

	/**
	 * 添加 filter
	 * 
	 * @param name
	 * @param filter
	 * @return
	 */
	public SecurityConfigurationSupport addHandler(String name, Handler handler) {
		if (handlers == null) {
			handlers = Maps.newHashMap();
		}
		handlers.put(name, handler);
		return this;
	}

	/**
	 * 配置 FilterChain
	 * 
	 * @param line
	 * @return
	 */
	public SecurityConfigurationSupport definition(String line) {
		if (!StringUtils.hasText(line)) {
			return this;
		}
		String[] parts = StringUtils.split(line, '=');
		if (!(parts != null && parts.length == 2)) {
			return this;
		}
		String path = StringUtils.clean(parts[0]);
		String filter = StringUtils.clean(parts[1]);
		if (!(StringUtils.hasText(path) && StringUtils.hasText(filter))) {
			return this;
		}
		if (definitions == null) {
			definitions = Maps.newOrderMap();
		}
		definitions.put(path, filter);
		return this;
	}
}