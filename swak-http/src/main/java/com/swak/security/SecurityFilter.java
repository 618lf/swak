package com.swak.security;

import com.swak.reactivex.handler.WebFilter;
import com.swak.reactivex.handler.WebFilterChain;
import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;
import com.swak.security.mgt.FilterChainResolver;
import com.swak.security.mgt.SecurityManager;
import com.swak.security.subject.Subject;

import io.reactivex.Observable;

/**
 * 构建 API 安全处理，以及用户处理
 * 
 * @author lifeng
 */
public class SecurityFilter implements WebFilter {

	private SecurityManager securityManager;
	private FilterChainResolver filterChainManager;

	/**
	 * 执行权限过滤 filter
	 */
	@Override
	public Observable<Void> filter(HttpServerRequest request, HttpServerResponse response, WebFilterChain origChain) {

		// 获取当前的用户
		Subject subject = securityManager.createSubject(request, response);
		
		// 存储在 request 中
		request.setSubject(subject);

		// 执行filter链
		return this.getExecutionChain(request, response, origChain).filter(request, response);
	}

	/**
	 * 获得执行的 Filter Chain
	 * 
	 * @param request
	 * @param response
	 * @param origChain
	 * @return
	 */
	protected WebFilterChain getExecutionChain(HttpServerRequest request, HttpServerResponse response,
			WebFilterChain origChain) {
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
}