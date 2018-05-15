package com.swak.reactivex;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * 认证后的身份信息,只需要存储一个登录的用户ID
 * @author lifeng
 */
public class Principal implements Externalizable {

	private static final long serialVersionUID = 1L;
	private Long id;// 用户id
	private String account; // 登录帐号

	public Principal(){}
	public Principal(Long userId, String account) {
		this.id = userId;
		this.account = account;
	}
	public Long getId() {
		return id;
	}
	public String getAccount() {
		return account;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	/**
	 * 序列化
	 */
	public String toString(){
		return new StringBuilder("Principal").append("@user=").append(this.getId()).toString();
	}
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.id = (Long)in.readObject();
		this.account = (String)in.readObject();
	}
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(this.id);
		out.writeObject(this.account);
	}
}
