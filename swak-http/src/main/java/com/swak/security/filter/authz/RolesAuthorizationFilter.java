package com.swak.security.filter.authz;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;
import com.swak.security.subject.Subject;
import com.swak.security.utils.SecurityUtils;

/**
 * 角色验证
 * @author lifeng
 */
public class RolesAuthorizationFilter extends AuthorizationFilter {

	@Override
	protected boolean isAccessAllowed(HttpServerRequest request,
			HttpServerResponse response, Object mappedValue) {
		Subject subject = SecurityUtils.getSubject(request);
		if (subject.getPrincipal() == null) {
			return false;
		}
		
		String[] rolesArray = (String[]) mappedValue;
		if (rolesArray == null || rolesArray.length == 0) {
            return true;
        }
		return subject.hasAllRoles(rolesArray);
	}
}