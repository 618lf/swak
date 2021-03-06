package com.swak.vertx.security.principal;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.Constants;
import com.swak.security.JwtAuthProvider;
import com.swak.security.Subject;
import com.swak.security.Token;
import com.swak.security.jwt.JWTPayload;
import com.swak.utils.StringUtils;
import com.swak.vertx.security.Context;
import com.swak.vertx.security.SecuritySubject;

import io.vertx.core.http.Cookie;

/**
 * 基于 TOKEN 的身份管理方式, 可以在此做一个服务器端的管理方式，控制账户登录情况
 *
 * @author: lifeng
 * @date: 2020/3/29 20:43
 */
public class TokenPrincipalStrategy implements PrincipalStrategy {

	private final JwtAuthProvider jwtAuth;
	private final String tokenName;

	public TokenPrincipalStrategy(JwtAuthProvider jwtAuth, String tokenName) {
		this.jwtAuth = jwtAuth;
		this.tokenName = tokenName;
	}

	/**
	 * 创建身份信息
	 */
	@Override
	public CompletionStage<Subject> createSubject(Context context) {
		Subject subject = null;

		try {

			// 从header 中获取 token
			String token = context.header(this.tokenName);

			// 从cookie 中获取 token
			Cookie cookie;
			if (StringUtils.isBlank(token) && (cookie = context.cookie(this.tokenName)) != null
					&& !Constants.DELETED_COOKIE_VALUE.equals(cookie.getValue())) {
				token = cookie.getValue();
			}

			// 获取 JWTPayload
			JWTPayload payload = jwtAuth.verifyToken(token);

			// 创建主体
			subject = new SecuritySubject(payload);

		} catch (Exception ignored) {
		}

		// 空实现
		if (subject == null) {
			subject = new SecuritySubject();
		}

		// 绑定当前请求
		context.put(Constants.SUBJECT_NAME, subject);

		// 异步结果
		return CompletableFuture.completedFuture(subject);
	}

	/**
	 * 创建 token
	 */
	@Override
	public CompletionStage<Token> generateToken(Subject subject) {
		Token token = new Token();
		token.setName(this.tokenName);
		token.setToken(jwtAuth.generateToken(subject.toPayload()));
		return CompletableFuture.completedFuture(token);
	}
}
