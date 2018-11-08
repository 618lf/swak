package com.tmt;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.security.JwtAuthProvider;
import com.swak.utils.Sets;
import com.swak.vertx.config.IRouterConfig;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.security.JwtAuthHandler;
import com.swak.vertx.security.SecurityFilter;
import com.swak.vertx.security.filter.Filter;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

/**
 * 项目配置
 * 
 * @author lifeng
 */
@Configuration
public class AppConfiguration {

	/**
	 * 授权提供
	 * 
	 * @param properties
	 * @return
	 */
	@Bean
	public JwtAuthProvider jwtAuth(VertxProperties properties) {
		JwtAuthProvider JwtAuth = new JwtAuthProvider(properties.getKeyStorePath(), properties.getKeyStorePass(), properties.getJwtTokenName());
		return JwtAuth;	}

	/**
	 * 安全过滤
	 * 
	 * @return
	 */
	@Bean
	public Filter securityFilter() {
		// 权限配置
		SecurityFilter filter = new SecurityFilter();
		filter.definition("/api/login=anno").definition("/api/logout=anno").definition("/api/reqister=anno")
				.definition("/api/user/=user").definition("/api/manage/=user, role[admin]").definition("/=user");
		return filter;
	}

	/**
	 * 网站自定义的路由配置， 可以配置多个
	 * 
	 * @return
	 */
	@Bean
	public IRouterConfig routerConfig(JwtAuthProvider jwtAuth, Filter securityFilter, VertxProperties properties) {
		return new IRouterConfig() {
			@Override
			public void apply(Router router) {
				Set<String> headers = Sets.newHashSet();
				headers.add("X-Requested-With");
				headers.add(jwtAuth.getTokenName());
				router.route().handler(CorsHandler.create("*").allowedHeaders(headers));
				router.route().handler(BodyHandler.create(properties.getUploadDirectory())
							  .setBodyLimit(properties.getBodyLimit())
							  .setDeleteUploadedFilesOnEnd(properties.isDeleteUploadedFilesOnEnd()));
				router.route().handler(JwtAuthHandler.create(jwtAuth, securityFilter));
			}
		};
	}
}