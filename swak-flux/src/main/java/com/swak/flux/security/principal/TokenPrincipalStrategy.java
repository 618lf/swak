package com.swak.flux.security.principal;

import java.io.Serializable;

import com.swak.flux.transport.Principal;
import com.swak.flux.transport.Subject;
import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.transport.server.HttpServerResponse;
import com.swak.security.JwtAuthProvider;
import com.swak.security.jwt.JWTPayload;
import com.swak.utils.StringUtils;

import reactor.core.publisher.Mono;

/**
 * 基于 TOKEN 的身份管理方式 一个星期不用就失效
 * 
 * @author lifeng
 */
public class TokenPrincipalStrategy implements PrincipalStrategy {

	private String tokenName = "X-Token";
	private Integer timeOut = 24 * 60 * 60 * 7; // 一个星期
	private final JwtAuthProvider jwt;

	public TokenPrincipalStrategy(JwtAuthProvider jwt) {
		this.jwt = jwt;
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
		JWTPayload payload = new JWTPayload();
		payload.put("id", subject.getPrincipal().getId());
		payload.put("account", subject.getPrincipal().getAccount());
		String token = jwt.generateToken(payload);
		response.header(this.getTokenName(), token);
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

		// 获取身份,解析错误会抛出异常
		JWTPayload payload = jwt.verifyToken(token);
		Serializable id = payload.getValue("id");
		String account = payload.getValue("account");
		Principal principal = new Principal();
		principal.setAccount(account);
		principal.setId(id);
		subject.setPrincipal(principal);
		return Mono.just(subject);
	}
}
