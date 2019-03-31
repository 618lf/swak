package com.swak.flux.security.filter.authz;

import com.swak.flux.transport.http.Subject;
import com.swak.flux.transport.http.server.HttpServerRequest;
import com.swak.flux.transport.http.server.HttpServerResponse;

import reactor.core.publisher.Mono;

/**
 * 角色验证
 * 
 * @author lifeng
 */
public class RolesAuthorizationFilter extends AuthorizationFilter {

	@Override
	protected Mono<Boolean> isAccessAllowed(HttpServerRequest request, HttpServerResponse response,
			Object mappedValue) {
		Subject subject = request.getSubject();
		if (subject.getPrincipal() == null) {
			return Mono.just(false);
		}

		String[] rolesArray = (String[]) mappedValue;
		if (rolesArray == null || rolesArray.length == 0) {
			return Mono.just(true);
		}
		return Mono.fromCompletionStage(subject.hasAllRoles(rolesArray));
	}
}