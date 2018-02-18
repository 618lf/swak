package com.swak.security.principal.support;

import java.util.UUID;

import com.swak.common.utils.Digests;
import com.swak.common.utils.StringUtils;
import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.security.principal.Principal;
import com.swak.security.principal.PrincipalStrategy;
import com.swak.security.subjct.Subject;
import com.swak.security.utils.TokenUtils;

/**
 * 基于 token 的身份解决方案
 * 对于key 的管理可以更加简单， 看情况而定
 * @author lifeng
 */
public class TokenPrincipalStrategy implements PrincipalStrategy {

	private String tokenName = "X-Token";
	private Integer timeOut = 24 * 60 * 60 * 30; // 一个月
	private String key = UUID.randomUUID().toString();
	
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
	public void createPrincipal(Subject subject, HttpServletRequest request,
			HttpServletResponse response) {
		// 让前端可已获取响应头
		response.header("Access-Control-Expose-Headers", this.getTokenName());
		String token = TokenUtils.getToken(subject.getPrincipal(), key);
		response.header(this.getTokenName(), token);
		
		// session id
		String sessionId = this.getKey(token);
		subject.setSessionId(sessionId);
	}

	/**
	 * 将身份信息无效
	 */
	@Override
	public void invalidatePrincipal(Subject subject,
			HttpServletRequest request, HttpServletResponse response) {
		response.header(this.getTokenName(), "");
	}

	/**
	 * 获取身份信息
	 */
	@Override
	public void resolvePrincipal(Subject subject, HttpServletRequest request,
			HttpServletResponse response) {
		
		// 获取token
		String token = request.getHeader(this.getTokenName());
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
	public void invalidatePrincipal(String sessionId) {
		
	}

	/**
	 * 获得缓存的key值
	 * @param token
	 * @return
	 */
	protected String getKey(String token) {
		return Digests.md5(token);
	}
}