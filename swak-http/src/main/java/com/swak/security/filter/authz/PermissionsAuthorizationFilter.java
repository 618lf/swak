package com.swak.security.filter.authz;

import com.swak.reactivex.Subject;
import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;
import com.swak.security.utils.SecurityUtils;

/**
 * 权限信息判断
 * @author lifeng
 */
public class PermissionsAuthorizationFilter extends AuthorizationFilter {

	/**
	 * 验证权限,必须有用户信息
	 */
	@Override
	protected boolean isAccessAllowed(HttpServerRequest request, HttpServerResponse response, Object mappedValue) {
		Subject subject = SecurityUtils.getSubject(request);
		if (subject.getPrincipal() == null) {
			return false;
		}
		String[] perms = (String[]) mappedValue;
		boolean isPermitted = true;
        if (perms != null && perms.length > 0) {
            if (perms.length == 1) {
                if (!subject.isPermitted(perms[0])) {
                    isPermitted = false;
                }
            } else {
                if (!subject.isPermittedAll(perms)) {
                    isPermitted = false;
                }
            }
        }
		return isPermitted;
	}
}