package com.swak.vertx.security;

import java.util.HashMap;

import com.swak.security.jwt.JWTObject;
import com.swak.security.jwt.JWTPayload;
import com.swak.utils.JsonMapper;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;

/**
 * 授权主体
 * 
 * @author lifeng
 */
public class Subject extends JWTObject {

	private static String ID_ATTR = "id";
	private static String NAME_ATTR = "name";
	private static String ROLE_ATTR = "roles";
	private static String PERMISSION_ATTR = "permissions";

	/**
	 * 必须传入 payload
	 * 
	 * @param payload
	 */
	public Subject() {
		this.map = Maps.newHashMap();
	}

	/**
	 * 必须传入 payload
	 * 
	 * @param payload
	 */
	public Subject(JWTPayload payload) {
		if (payload != null) {
			this.map = payload.getData();
		} else {
			this.map = Maps.newHashMap();
		}
	}

	/**
	 * 必须传入 payload
	 * 
	 * @param payload
	 */
	@SuppressWarnings("unchecked")
	public Subject(String payload) {
		if (StringUtils.isNotBlank(payload)) {
			this.map = JsonMapper.fromJson(payload, HashMap.class);
		} else {
			this.map = Maps.newHashMap();
		}
	}

	/**
	 * 设置主体的ID
	 * 
	 * @param id
	 * @return
	 */
	public Subject setId(Long id) {
		return this.put(ID_ATTR, id);
	}

	/**
	 * 设置主体的ID
	 * 
	 * @param id
	 * @return
	 */
	public Subject setName(String name) {
		return this.put(NAME_ATTR, name);
	}

	/**
	 * 设置主体的ID
	 * 
	 * @param id
	 * @return
	 */
	public Subject setRoles(String roles) {
		return this.put(ROLE_ATTR, roles);
	}

	/**
	 * 设置主体的ID
	 * 
	 * @param id
	 * @return
	 */
	public Subject setPermissions(String permissions) {
		return this.put(PERMISSION_ATTR, permissions);
	}

	/**
	 * 设置主体的ID
	 * 
	 * @param id
	 * @return
	 */
	public Long getId() {
		String id = this.getValue(ID_ATTR);
		return id != null ? Long.parseLong(id) : null;
	}

	/**
	 * 设置主体的ID
	 * 
	 * @param id
	 * @return
	 */
	public String getName() {
		return this.getValue(NAME_ATTR);
	}

	/**
	 * 设置主体的ID
	 * 
	 * @param id
	 * @return
	 */
	public String getRoles() {
		return this.getValue(ROLE_ATTR);
	}

	/**
	 * 设置主体的ID
	 * 
	 * @param id
	 * @return
	 */
	public String getPermissions() {
		return this.getValue(PERMISSION_ATTR);
	}

	/**
	 * 已登录
	 * 
	 * @return
	 */
	public boolean isUser() {
		return this.map.containsKey(ID_ATTR);
	}

	/**
	 * 转为 Payload
	 * 
	 * @return
	 */
	public JWTPayload toPayload() {
		return new JWTPayload(this.map);
	}
}