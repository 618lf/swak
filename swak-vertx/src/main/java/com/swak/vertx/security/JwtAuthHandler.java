package com.swak.vertx.security;

import com.swak.vertx.security.filter.Filter;
import com.swak.vertx.security.jwt.JWTPayload;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * 即与 Jwt 的 授权
 * 
 * @author lifeng
 */
public class JwtAuthHandler implements Handler<RoutingContext> {

	// 授权服务
	private final JwtAuthProvider jwtAuthProvider;
	private final Filter filter;
	private final String tokenName;

	public JwtAuthHandler(JwtAuthProvider jwtAuthProvider, Filter filter) {
		this.jwtAuthProvider = jwtAuthProvider;
		this.filter = filter;
		this.tokenName = jwtAuthProvider.getTokenName();
	}

	/**
	 * 提交授权服务
	 */
	@Override
	public void handle(RoutingContext context) {

		// 获取 token 名称
		String token = context.request().getHeader(tokenName);
		
		// 获取 JWTPayload
		JWTPayload payload = jwtAuthProvider.verifyToken(token);
		
		// 放入请求中,就是一个 Map 对象
		if (payload != null) {
			context.request().params().add("authPayload", payload.encode());
		}

		// 返回false 不需要后面的处理
		// 返回true  需要继续后面的处理
		filter.doFilter(context).whenComplete((v, e) -> {
			if (v) {
				context.next();
			}
		});
	}

	/**
	 * 创建一个处理器
	 * 
	 * @param jwtAuth
	 * @return
	 */
	public static JwtAuthHandler create(JwtAuthProvider jwtAuth, Filter filter) {
		return new JwtAuthHandler(jwtAuth, filter);
	}
}