package com.swak.vertx.security;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.InitializingBean;

import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.vertx.security.filter.Filter;
import com.swak.vertx.security.handler.Handler;
import com.swak.vertx.security.handler.HandlerChain;
import com.swak.vertx.security.handler.PathDefinition;
import com.swak.vertx.security.handler.SimpleHandlerChain;
import com.swak.vertx.security.handler.impls.AnnoHandler;
import com.swak.vertx.security.handler.impls.RoleHandler;
import com.swak.vertx.security.handler.impls.UserHandler;

import io.vertx.ext.web.RoutingContext;

/**
 * 安全管理的 filter
 * 
 * @author lifeng
 */
public class SecurityFilter implements Filter, InitializingBean {

	private Map<String, Handler> handlers = Maps.newOrderMap();
	private Map<String, List<Handler>> chains = Maps.newOrderMap();
	private Map<String, String> configs = Maps.newOrderMap();

	public SecurityFilter() {
		// 权限验证器
		handlers.put("anno", new AnnoHandler());
		handlers.put("user", new UserHandler());
		handlers.put("role", new RoleHandler());
	}

	@Override
	public CompletableFuture<Boolean> doFilter(RoutingContext context, Subject subject) {

		// 请求的地址
		String url = context.request().uri();
		
		// 找到一个最匹配的
		List<Handler> filters = chains.keySet().stream().filter(s -> {
			return StringUtils.startsWith(url, s);
		}).findFirst().map(s -> {
			context.put(Handler.CHAIN_RESOLVE_PATH, s);
			return chains.get(s);
		}).get();

		// 构建执行链
		HandlerChain executeChain = new SimpleHandlerChain(filters);

		// 执行执行链
		boolean continued = executeChain.doHandler(context, subject);

		// 继续后续的执行
		if (continued) {
			return CompletableFuture.completedFuture(true);
		}

		// 不需要执行后续的代码
		return CompletableFuture.completedFuture(false);
	}

	/**
	 * 定义配置
	 * 
	 * @param line
	 * @return
	 */
	public SecurityFilter definition(String line) {
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
		if (configs == null) {
			configs = Maps.newOrderMap();
		}
		configs.put(path, filter);
		return this;
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
				
				String name = f;
				String param = null;

				// 权限判断
				if (f.contains("[")) {
					name = StringUtils.substringBefore(f, "[");
					param = StringUtils.substringBetween(f, "[", "]");
				}

				// 处理器
				Handler handler = handlers.get(name);
				if (handler == null) {
					continue;
				}
				
				// 参数设置
				if (param != null && handler instanceof PathDefinition) {
					PathDefinition phandler = (PathDefinition)handler;
					phandler.pathConfig(url, param);
				}
				chains.add(handler);
			}
			
			// 设置 url 对应的 chains
			this.chains.put(url, chains);
		}
	}
}
