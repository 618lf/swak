package com.swak.vertx.security;

import com.swak.vertx.Constants;
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

	public JwtAuthHandler(JwtAuthProvider jwtAuthProvider, Filter filter) {
		this.jwtAuthProvider = jwtAuthProvider;
		this.filter = filter;
	}

	/**
	 * 提交授权服务
	 */
	@Override
	public void handle(RoutingContext context) {

		// 主体信息
		Subject subject = null;
		
		try {
			
			// 获取 token 名称
			String token = context.request().getHeader(jwtAuthProvider.getTokenName());

			// 获取 JWTPayload
			JWTPayload payload = jwtAuthProvider.verifyToken(token);

			// 创建主体
			subject = new Subject(payload);
			
		} catch (Exception e) {}
		
		// 空实现
		if (subject == null) {
			subject = new Subject();
		}
		
		// 绑定当前请求
		context.put(Constants.SUBJECT_NAME, subject);

		// filter 中判断是否需要后续的处理
		filter.doFilter(context, subject).whenComplete((v, e) -> {
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