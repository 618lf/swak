package com.swak.vertx.security;

import com.swak.entity.Result;
import com.swak.exception.ErrorCode;
import com.swak.vertx.security.filter.Filter;
import com.swak.vertx.security.jwt.JWTPayload;
import com.swak.vertx.transport.HttpConst;

import io.netty.handler.codec.http.HttpHeaderNames;
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

		try {
			// 获取 token 名称
			String token = context.request().getHeader(tokenName);

			// 获取 JWTPayload
			JWTPayload payload = jwtAuthProvider.verifyToken(token);

			// 放入请求中,就是一个 Map 对象
			if (payload != null) {
				context.request().params().add("authPayload", payload.encode());
			}
		} catch (Exception e) {
			context.response().putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
			context.response().end(Result.error(ErrorCode.TOKEN_ERROR).toJson());
			return;
		}

		// filter 中判断是否需要后续的处理
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