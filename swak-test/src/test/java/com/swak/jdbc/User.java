package com.swak.jdbc;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.swak.annotation.Table;
import com.swak.entity.BaseEntity;

/**
 * 用户 管理
 * 
 * @author 超级管理员
 * @date 2018-08-22
 */
@Table(value = "CLOUD_USER")
public class User extends BaseEntity<Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name; // 账户名称
	private Byte type; // 账户类型：1单位账户，2个人账户
	private String app; // 所属业务模块
	private String appId; // 用户APP
	private String appSecret; // 用户密码
	private String roles; // 权限
	private Long areaId; // 区域设置
	private String areaName;// 区域设置
	private LocalDateTime expireDate;// 有效期 ： 订阅一个产品之后设置一个权限，权限有有效期，现在默认只有vip 这个权限

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public LocalDateTime getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(LocalDateTime expireDate) {
		this.expireDate = expireDate;
	}
}