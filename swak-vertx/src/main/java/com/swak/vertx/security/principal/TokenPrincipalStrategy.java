package com.swak.vertx.security.principal;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.Constants;
import com.swak.security.JwtAuthProvider;
import com.swak.security.jwt.JWTPayload;
import com.swak.utils.StringUtils;
import com.swak.vertx.security.SecuritySubject;
import com.swak.vertx.transport.Subject;
import com.swak.vertx.transport.Token;

import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;

/**
 * 基于 TOKEN 的身份管理方式, 可以在此做一个服务器端的管理方式，控制账户登录情况
 * 
 * @author lifeng
 */
public class TokenPrincipalStrategy implements PrincipalStrategy {

	private final JwtAuthProvider jwtAuthProvider;

	public TokenPrincipalStrategy(JwtAuthProvider jwt) {
		this.jwtAuthProvider = jwt;
	}

	/**
	 * 创建身份信息
	 */
	@Override
	public CompletionStage<Subject> createSubject(RoutingContext context) {
		Subject subject = null;

		try {

			// 从header 中获取 token
			String token = context.request().getHeader(jwtAuthProvider.getTokenName());

			// 从cookie 中获取 token
			Cookie cookie = null;
			if (StringUtils.isBlank(token) && (cookie = context.getCookie(jwtAuthProvider.getTokenName())) != null
					&& !Constants.DELETED_COOKIE_VALUE.equals(cookie.getValue())) {
				token = cookie.getValue();
			}

			// 获取 JWTPayload
			JWTPayload payload = jwtAuthProvider.verifyToken(token);

			// 创建主体
			subject = new SecuritySubject(payload);

		} catch (Exception e) {
		}

		// 空实现
		if (subject == null) {
			subject = new SecuritySubject();
		}

		// 绑定当前请求
		context.put(Constants.SUBJECT_NAME, subject);

		return CompletableFuture.completedFuture(subject);
	}

	/**
	 * 创建 token
	 */
	@Override
	public CompletionStage<Token> generateToken(Subject subject) {
		Token token = new Token();
		token.setName(jwtAuthProvider.getTokenName());
		token.setToken(jwtAuthProvider.generateToken(subject.toPayload()));
		return CompletableFuture.completedFuture(token);
	}
}
