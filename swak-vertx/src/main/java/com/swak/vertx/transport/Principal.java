package com.swak.vertx.transport;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import com.alibaba.fastjson.util.TypeUtils;

/**
 * 认证后的身份信息,只需要存储一个登录的用户ID
 * 
 * @author lifeng
 */
public class Principal implements Externalizable {

	private static final long serialVersionUID = 1L;
	private Serializable id;// 用户id
	private String name; // 登录帐号

	public Principal() {
	}

	public Principal(Serializable id, String name) {
		this.id = id;
		this.name = name;
	}

	public Serializable getId() {
		return id;
	}

	public String getIdAsString() {
		return TypeUtils.castToString(this.id);
	}

	public Long getIdAsLong() {
		return TypeUtils.castToLong(this.id);
	}

	public void setId(Serializable id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 序列化
	 */
	public String toString() {
		return new StringBuilder("Principal").append("@user=").append(this.getId()).toString();
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Principal) {
			return ((Principal) obj).getId().equals(this.getId());
		}
		return false;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.id = (Serializable) in.readObject();
		this.name = (String) in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(this.id);
		out.writeObject(this.name);
	}
}
