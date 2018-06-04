package com.swak.security.principal;

import java.util.Set;
import java.util.Stack;

import com.swak.reactivex.transport.http.Principal;
import com.swak.reactivex.transport.http.Session;

/**
 * 无Session 
 * @author lifeng
 */
public class NoneSession implements Session {
	
	/**
	 * 只有这个唯一的对象
	 */
	public static final NoneSession NONE = new NoneSession();
	
	private NoneSession() {}
	@Override
	public String getId() {
		return null;
	}

	@Override
	public Principal getPrincipal() {
		return null;
	}

	@Override
	public void setPrincipal(Principal principal) {
		
	}

	@Override
	public boolean isAuthenticated() {
		return false;
	}

	@Override
	public long getCreationTime() {
		return 0;
	}

	@Override
	public long getLastAccessedTime() {
		return 0;
	}

	@Override
	public long getMaxInactiveInterval() {
		return 0;
	}

	@Override
	public Stack<Principal> getRunAsPrincipals() {
		return null;
	}

	@Override
	public void setRunAsPrincipals(Stack<Principal> runAsPrincipals) {
		
	}

	@Override
	public void setAuthenticated(boolean authenticated) {
		
	}

	@Override
	public <T> T getAttribute(String attributeName) {
		return null;
	}

	@Override
	public Set<String> getAttributeNames() {
		return null;
	}

	@Override
	public <T> void setAttribute(String attributeName, T attributeValue) {
		
	}

	@Override
	public void removeAttribute(String attributeName) {
		
	}

	@Override
	public void destory() {
		// TODO Auto-generated method stub
		
	}
}
