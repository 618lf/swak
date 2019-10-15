package com.swak.vertx.security;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.InitializingBean;

import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.vertx.security.handler.Handler;
import com.swak.vertx.security.handler.HandlerChain;
import com.swak.vertx.security.handler.PathDefinition;
import com.swak.vertx.security.handler.SimpleHandlerChain;
import com.swak.vertx.security.handler.impls.AnnoHandler;
import com.swak.vertx.security.handler.impls.PermissionHandler;
import com.swak.vertx.security.handler.impls.RoleHandler;
import com.swak.vertx.security.handler.impls.UserHandler;
import com.swak.vertx.transport.Subject;

import io.vertx.ext.web.RoutingContext;

/**
 * 安全管理的 filter
 * 
 * @author lifeng
 */
public class SecurityHandler implements io.vertx.core.Handler<RoutingContext>, InitializingBean {

	private final SecurityManager securityManager;
	private final Map<String, Handler> handlers = Maps.newOrderMap();
	private final Map<String, List<Handler>> chains = Maps.newOrderMap();
	private final Map<String, String> configs = Maps.newOrderMap();

	public SecurityHandler(SecurityManager securityManager) {
		this.securityManager = securityManager;
		// 权限验证器
		handlers.put("anno", new AnnoHandler());
		handlers.put("user", new UserHandler());
		handlers.put("role", new RoleHandler());
		handlers.put("permission", new PermissionHandler());
	}

	/**
	 * 添加自定义 - 处理
	 * 
	 * @param name
	 * @param handler
	 * @return
	 */
	public SecurityHandler addHandler(String name, Handler handler) {
		this.handlers.put(name, handler);
		return this;
	}

	/**
	 * 添加自定义 - 定义
	 * 
	 * @param name
	 * @param definition
	 * @return
	 */
	public SecurityHandler addDefinition(String name, String definition) {
		this.configs.put(name, definition);
		return this;
	}

	/**
	 * 处理请求
	 */
	@Override
	public void handle(RoutingContext context) {
		this.securityManager.createSubject(context).thenCompose(subject -> {
			return this.doFilter(context, subject);
		}).whenComplete((v, e) -> {
			if (v) {
				context.next();
			}
		});
	}

	/**
	 * 执行
	 */
	private CompletionStage<Boolean> doFilter(RoutingContext context, Subject subject) {

		// 请求的地址
		String url = context.request().uri();

		// 找到一个最匹配的
		Optional<List<Handler>> optional = chains.keySet().stream().filter(s -> {
			return StringUtils.startsWith(url, s);
		}).findFirst().map(s -> {
			context.put(Handler.CHAIN_RESOLVE_PATH, s);
			return chains.get(s);
		});
		List<Handler> filters = optional.isPresent() ? optional.get() : null;

		// 构建执行链
		HandlerChain executeChain = new SimpleHandlerChain(filters);

		// 执行执行链
		return executeChain.doHandler(context, subject);
	}

	/**
	 * 初始化 配置
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Set<String> urls = configs.keySet();
		for (String url : urls) {

			String filter = configs.get(url);

			// url 对应的 chains
			List<Handler> chains = Lists.newArrayList();

			// 多个filter 配置用，分隔
			String[] filters = filter.split(",");
			for (String f : filters) {

				String name = StringUtils.clean(f);
				String param = null;

				// 权限判断
				if (name.contains("[")) {
					param = StringUtils.clean(StringUtils.substringBetween(name, "[", "]"));
					name = StringUtils.clean(StringUtils.substringBefore(name, "["));
				}

				// 处理器
				Handler handler = handlers.get(name);
				if (handler == null) {
					continue;
				}

				// 参数设置
				if (param != null && handler instanceof PathDefinition) {
					PathDefinition phandler = (PathDefinition) handler;
					phandler.pathConfig(url, param);
				}
				chains.add(handler);
			}

			// 设置 url 对应的 chains
			this.chains.put(url, chains);
		}
	}
}
