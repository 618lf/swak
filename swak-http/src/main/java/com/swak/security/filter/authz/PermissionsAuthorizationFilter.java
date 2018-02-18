package com.swak.security.filter.authz;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.security.subjct.Subject;
import com.swak.security.utils.SecurityUtils;

public class PermissionsAuthorizationFilter extends AuthorizationFilter {

	/**
	 * 验证权限,必须有用户信息
	 */
	@Override
	protected boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response, Object mappedValue) throws Exception {
		Subject subject = SecurityUtils.getSubject();
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