package com.swak.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 基础实体类:  bug修复： 如果setXX 返回值不是void，通过反射设置数据的插件会设值失败
 *
 * @author: lifeng
 * @date: 2020/3/29 11:04
 */
@SuppressWarnings("unchecked")
public abstract class BaseEntity<PK> extends IdEntity<PK> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	protected String name;
	/**
	 * 创建人ID
	 */
	protected Long userId;
	/**
	 * 创建人名称
	 */
	protected String userName;
	/**
	 * 创建时间
	 */
	protected LocalDateTime createDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	/**
	 * 新增操作
	 *
	 * @return 主键
	 */
	@Override
	public PK prePersist() {
		this.createDate = LocalDateTime.now();
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
	 * @param user 用户数据
	 */
	public <T> T userOptions(BaseEntity<Long> user) {
		this.userId = user.getId();
		this.userName = user.getName();
		return (T) this;
	}
}