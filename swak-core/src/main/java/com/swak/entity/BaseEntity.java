package com.swak.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 基础实体类
 * 
 * @author lifeng
 * @param <PK>
 */
@SuppressWarnings("unchecked")
public abstract class BaseEntity<PK> extends IdEntity<PK> implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String name;// 名称
	protected Long userId;// 创建人ID
	protected String userName;// 创建人名称
	protected Date createDate;// 创建时间

	public String getName() {
		return name;
	}

	public <T> T setName(String name) {
		this.name = name;
		return (T) this;
	}

	public Long getUserId() {
		return userId;
	}

	public <T> T setUserId(Long userId) {
		this.userId = userId;
		return (T) this;
	}

	public String getUserName() {
		return userName;
	}

	public <T> T setUserName(String userName) {
		this.userName = userName;
		return (T) this;
	}

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateDate() {
		return createDate;
	}

	public <T> T setCreateDate(Date createDate) {
		this.createDate = createDate;
		return (T) this;
	}

	/**
	 * 新增操作
	 * 
	 * @return
	 */
	@Override
	public PK prePersist() {
		this.createDate = new Timestamp(System.currentTimeMillis());
		return super.prePersist();
	}

	/**
	 * 修改操作
	 */
	public void preUpdate() {
	}

	/**
	 * 用户当前的操作
	 * 
	 * @param user
	 */
	public <T> T userOptions(BaseEntity<Long> user) {
		this.userId = user.getId();
		this.userName = user.getName();
		return (T) this;
	}
}