package com.swak.flux.security.context;

/**
 * 定义一组常用变量
 * @author lifeng
 */
public interface AuthenticationToken {

	/**
	 * 用户名
	 */
	String username = "username";
	
	/**
	 * 密码
	 */
	String password = "password";
	
	/**
	 * 验证码
	 */
	String captcha  = "captcha";
	
	/**
	 * 记住我
	 */
	String rememberMe = "rememberMe";
	
	/**
	 * 回调地址
	 */
	String returnUrl = "returnUrl";
}