package com.swak.security.mgt;

import com.swak.http.Filter;
import com.swak.security.filter.authc.AnonymousFilter;
import com.swak.security.filter.authc.AuthenticatingFilter;
import com.swak.security.filter.authc.LogoutFilter;
import com.swak.security.filter.authc.UserFilter;
import com.swak.security.filter.authz.PermissionsAuthorizationFilter;
import com.swak.security.filter.authz.RolesAuthorizationFilter;

public enum DefaultFilter {

	anon(AnonymousFilter.class), 
	authc(AuthenticatingFilter.class), 
	logout(LogoutFilter.class), 
	perms(PermissionsAuthorizationFilter.class), 
	roles(RolesAuthorizationFilter.class), 
	user(UserFilter.class);

	private final Class<? extends Filter> filterClass;
	
	private DefaultFilter(Class<? extends Filter> filterClass) {
		this.filterClass = filterClass;
	}

	public Class<? extends Filter> getFilterClass() {
		return filterClass;
	}
	
	public Filter newInstance() throws InstantiationException, IllegalAccessException {
        return (Filter) filterClass.newInstance();
    }
}
