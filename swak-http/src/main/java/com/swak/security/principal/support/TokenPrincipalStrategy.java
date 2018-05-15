package com.swak.security.principal.support;

import com.swak.common.utils.Digests;
import com.swak.common.utils.StringUtils;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.reactivex.Principal;
import com.swak.reactivex.Subject;
import com.swak.security.principal.PrincipalStrategy;
import com.swak.security.utils.TokenUtils;

/**
 * 基于 jwt 的 身份管理方式
 * @author lifeng
 */
public class TokenPrincipalStrategy implements PrincipalStrategy {

	private String tokenName = "X-Token";
	private Integer timeOut = 24 * 60 * 60 * 7; // 一个星期
	private String key = "SIMPLE-TOKEN-SERVER";
	
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
	public void createPrincipal(Subject subject, HttpServerRequest request,
			HttpServerResponse response) {
		
		// 也可以存储到 cookie 中
		String token = TokenUtils.getToken(subject.getPrincipal(), key);
		response.header(this.getTokenName(), token);
		
		// 生成 SessionId
		String sessionId = this.getKey(token);
		subject.setSessionId(sessionId);
	}

	/**
	 * 将身份信息无效
	 */
	@Override
	public void invalidatePrincipal(Subject subject,
			HttpServerRequest request, HttpServerResponse response) {
		response.header(this.getTokenName(), "");
	}

	/**
	 * 获取身份信息
	 */
	@Override
	public void resolvePrincipal(Subject subject, HttpServerRequest request,
			HttpServerResponse response) {
		
		// 获取token
		String token = request.getRequestHeader(this.getTokenName());
		if (!StringUtils.hasText(token)) {
			return;
		}
		
		// 获取加密的key 根据token 获取
		String sessionId = this.getKey(token);
		subject.setSessionId(sessionId);
		
		// 获取身份
		Principal principal = TokenUtils.getSubject(token, key);
		if (principal != null) {
			subject.setPrincipal(principal);
		}
	}
	
	/**
	 * 将此sessionId 无效
	 */
	@Override
	public void invalidatePrincipal(String sessionId) {}

	/**
	 * 获得缓存的key值
	 * @param token
	 * @return
	 */
	protected String getKey(String token) {
		return Digests.md5(token);
	}
}
