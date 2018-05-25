package com.swak.reactivex;

import java.util.Set;
import java.util.Stack;

/**
 * 简单的session存储, 提供类似Session 的存储
 * @author lifeng
 */
public interface Session {
	
	/**
	 * session id
	 * @return
	 */
	String getId();
	
	/**
	 * 得到身份
	 * @return
	 */
	Principal getPrincipal();
	
	/**
	 * 设置身份
	 * @return
	 */
	void setPrincipal(Principal principal);
	
	/**
	 * 是否授权登录
	 * @return
	 */
	boolean isAuthenticated();
	
	/**
	 * 创建时间
	 * @return
	 */
	long getCreationTime();
	
	/**
	 * 最后一次访问
	 * @return
	 */
	long getLastAccessedTime();
	
	/**
	 * 最大的空闲时间
	 * @return
	 */
	long getMaxInactiveInterval();
	
	/**
	 * 返回 runas
	 * @return
	 */
	Stack<Principal> getRunAsPrincipals();
	
	/**
	 * 设置 runas
	 */
	void setRunAsPrincipals(Stack<Principal> runAsPrincipals);
	
	/**
	 * 设置 已经身份认证
	 * @param authenticated
	 */
	void setAuthenticated(boolean authenticated);
	
	/**
	 * 属性
	 * @param attributeName
	 * @return
	 */
	<T> T getAttribute(String attributeName);
	
	/**
	 * 所有的属性名称
	 * @return
	 */
	Set<String> getAttributeNames();
	
	/**
	 * 设置属性
	 * @param attributeName
	 * @param attributeValue
	 */
	<T> void setAttribute(String attributeName, T attributeValue);
	
	/**
	 * 删除属性
	 * @param attributeName
	 */
	void removeAttribute(String attributeName);
	
	/**
	 * 销毁 for gc 
	 */
	void destory();
}