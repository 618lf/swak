package com.swak.security.filter.authz;

import com.swak.reactivex.transport.http.Subject;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.security.SecurityUtils;

import reactor.core.publisher.Mono;

/**
 * 权限信息判断
 * 
 * @author lifeng
 */
public class PermissionsAuthorizationFilter extends AuthorizationFilter {

	/**
	 * 验证权限,必须有用户信息
	 */
	@Override
	protected Mono<Boolean> isAccessAllowed(HttpServerRequest request, HttpServerResponse response,
			Object mappedValue) {
		Subject subject = SecurityUtils.getSubject(request);
		if (subject.getPrincipal() == null) {
			return Mono.just(false);
		}
		String[] perms = (String[]) mappedValue;
		if (perms != null && perms.length > 0) {
			return Mono.fromCompletionStage(
					perms.length == 1 ? subject.isPermitted(perms[0]) : subject.isPermittedAll(perms));
		}
		return Mono.just(true);
	}
}