package com.swak.security.filter.authz;

import com.swak.reactivex.transport.http.Subject;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.security.utils.SecurityUtils;

import reactor.core.publisher.Mono;

/**
 * 角色验证
 * @author lifeng
 */
public class RolesAuthorizationFilter extends AuthorizationFilter {

	@Override
	protected Mono<Boolean> isAccessAllowed(HttpServerRequest request,
			HttpServerResponse response, Object mappedValue) {
		Subject subject = SecurityUtils.getSubject(request);
		if (subject.getPrincipal() == null) {
			return Mono.just(false);
		}
		
		String[] rolesArray = (String[]) mappedValue;
		if (rolesArray == null || rolesArray.length == 0) {
			return Mono.just(true);
        }
		return subject.hasAllRoles(rolesArray);
	}
}