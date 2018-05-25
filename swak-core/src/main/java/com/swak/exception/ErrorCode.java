package com.swak.exception;

import java.io.Serializable;

import com.swak.utils.JsonMapper;

/**
 * 系统错误码
 * @author lifeng
 */
public class ErrorCode implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;
	
	//操作成功
	public static ErrorCode OPERATE_FAILURE = new ErrorCode(-1,"操作失败");
	//操作成功
	public static ErrorCode OPERATE_SECCESS = new ErrorCode(1,"操作成功");
	//用户注册成功
	public static ErrorCode REGISTER_SUCCESS = new ErrorCode(2,"用户注册成功");
	//用户找回密码成功
	public static ErrorCode FIND_PASSWORD_SUCCESS = new ErrorCode(3,"用户找回密码成功");
	//登录失败 ---用户名和密码错误
	public static ErrorCode U_P_FAILURE = new ErrorCode(40001,"用户名和密码错误");
	//登录失败 ---验证码错误
	public static ErrorCode CAPTCHA_FAILURE = new ErrorCode(40002,"验证码错误");
	//登录失败 ---账户锁定
	public static ErrorCode ACCOUNT_LOCK_FAILURE = new ErrorCode(40003,"账户锁定"); 
	//访问受限 ---无权限
	public static ErrorCode ACCESS_DENIED = new ErrorCode(40004,"访问受限,请登录或联系管理员授权");
	//访问受限 ---没登录（没登陆和没授权是两个不同的问题）
	public static ErrorCode NO_USER = new ErrorCode(40005,"访问受限,请登录");
	//访问受限 ---没登录（没登陆和没授权是两个不同的问题）
	public static ErrorCode NO_AUTHENTICATED = new ErrorCode(40006,"操作受限,请输入密码认证");
	//访问受限 ---必须是微信用户（没登陆和没授权是两个不同的问题）
	public static ErrorCode NO_BIND_WX = new ErrorCode(40007,"操作受限,必须绑定微信用户");
	//访问受限 ---必须是微信用户（没登陆和没授权是两个不同的问题）
	public static ErrorCode NO_OPEN_WX = new ErrorCode(40008,"操作受限,必须在微信客户端打开");
	//访问受限 ---必须是微信用户（没登陆和没授权是两个不同的问题）
	public static ErrorCode NO_SUBSCRIBE_WX = new ErrorCode(40009,"操作受限,必须是关注微信");
	//访问受限 ---请重新发起登录操作（一般页面收到这个请求后重新发起请求）
	public static ErrorCode REDIRECT_LOGIN = new ErrorCode(40010,"重新发起登录操作");
	
	/**
	 * 错误code
	 */
	private int code;
	
	/**
	 * 错误描述
	 */
	private String msg;
	
	/**
	 * 错误原因
	 */
	private String reason;
	
	public ErrorCode(){}
	public ErrorCode(int code, String msg){
		this.code = code;
		this.msg = msg;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	/**
	 * 转化为json格式
	 * @return
	 */
	public String toJson() {
		return JsonMapper.toJson(this);
	}
	
	@Override
	public ErrorCode clone() throws CloneNotSupportedException {
		return (ErrorCode)super.clone();
	}
}