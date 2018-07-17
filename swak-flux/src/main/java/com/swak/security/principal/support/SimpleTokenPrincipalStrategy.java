package com.swak.security.principal.support;

import com.swak.codec.Digests;
import com.swak.reactivex.transport.http.Principal;
import com.swak.reactivex.transport.http.Subject;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.reactor.publisher.MonoTrue;
import com.swak.security.TokenUtils;
import com.swak.security.principal.PrincipalStrategy;
import com.swak.utils.StringUtils;

import reactor.core.publisher.Mono;

/**
 * 基于 TOKEN 的身份管理方式 一个星期不用就失效
 * 
 * @author lifeng
 */
public class SimpleTokenPrincipalStrategy implements PrincipalStrategy {

	private String tokenName = "X-Token";
	private Integer timeOut = 24 * 60 * 60 * 7; // 一个星期
	private final String KEY;

	public SimpleTokenPrincipalStrategy(String KEY) {
		this.KEY = KEY;
	}

	public Integer getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(Integer timeOut) {
		this.timeOut = timeOut;
	}

	public String getTokenName() {
		return tokenName;
	}

	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

	/**
	 * 创建身份信息 基于 Jwts
	 */
	@Override
	public Mono<Subject> createPrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		String token = TokenUtils.getToken(subject.getPrincipal(), KEY);
		response.header(this.getTokenName(), token);
		String sessionId = this.getKey(token);
		subject.setSessionId(sessionId);
		return Mono.just(subject);
	}

	/**
	 * 将身份信息无效
	 */
	@Override
	public Mono<Boolean> invalidatePrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response) {
		return Mono.fromCallable(() -> {
			response.header(this.getTokenName(), StringUtils.EMPTY);
			return true;
		});
	}

	/**
	 * 获取身份信息
	 */
	@Override
	public Mono<Subject> resolvePrincipal(Subject subject, HttpServerRequest request, HttpServerResponse response) {

		// 获取token
		String token = request.getRequestHeader(this.getTokenName());
		if (!StringUtils.hasText(token)) {
			return Mono.just(subject);
		}

		// 获取加密的key 根据token 获取
		String sessionId = this.getKey(token);
		
		// 获取身份
		Principal principal = TokenUtils.getSubject(token, KEY);
		if (principal != null) {
			subject.setPrincipal(principal);
		}
		subject.setSessionId(sessionId);
		return Mono.just(subject);
	}

	/**
	 * 将此sessionId 无效
	 */
	@Override
	public Mono<Boolean> invalidatePrincipal(String sessionId) {
		return MonoTrue.instance();
	}

	/**
	 * 获得缓存的key值
	 * 
	 * @param token
	 * @return
	 */
	protected String getKey(String token) {
		return Digests.md5(token);
	}
}
