package com.swak.flux.security;

import org.springframework.core.Ordered;

import com.swak.flux.handler.WebFilter;
import com.swak.flux.handler.WebFilterChain;
import com.swak.flux.security.mgt.FilterChainManager;
import com.swak.flux.security.mgt.SecurityManager;
import com.swak.flux.transport.http.Subject;
import com.swak.flux.transport.http.server.HttpServerRequest;
import com.swak.flux.transport.http.server.HttpServerResponse;

import reactor.core.publisher.Mono;

/**
 * 构建 API 安全处理，以及用户处理
 * 
 * @author lifeng
 */
public class SecurityFilter implements WebFilter, Ordered {

	private final SecurityManager securityManager;
	private final FilterChainManager filterChainManager;

	public SecurityFilter(SecurityManager securityManager, FilterChainManager filterChainManager) {
		this.securityManager = securityManager;
		this.filterChainManager = filterChainManager;
	}

	/**
	 * 执行权限过滤 filter
	 */
	@Override
	public Mono<Void> filter(HttpServerRequest request, HttpServerResponse response, WebFilterChain origChain) {
		return securityManager.createSubject(request, response)
			   .map(subject -> this.getExecutionChain(subject, request, response, origChain))
			   .flatMap(chain -> chain.filter(request, response));
	}

	/**
	 * 获得执行的 Filter Chain
	 * 
	 * @param request
	 * @param response
	 * @param origChain
	 * @return
	 */
	protected WebFilterChain getExecutionChain(Subject subject, HttpServerRequest request, HttpServerResponse response,
			WebFilterChain origChain) {
		
		// 构建请求
		WebFilterChain chain = origChain;
		if (this.filterChainManager == null || !this.filterChainManager.hasChains()) {
			return origChain;
		}

		WebFilterChain resolved = this.filterChainManager.proxy(request, origChain);
		if (resolved != null) {
			chain = resolved;
		}
		return chain;
	}

	/**
	 * 顺序
	 */
	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 100;
	}
}