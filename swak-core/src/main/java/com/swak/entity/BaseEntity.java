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
public abstract class BaseEntity<PK> extends IdEntity<PK> implements Serializable {

	private static final long serialVersionUID = 1L;

	// path 中的分隔
	public static final String PATH_SEPARATE = "/";
	// IDS 中的分隔
	public static final String IDS_SEPARATE = ",";
	// 是/否/删除 - TINYINT
	public static final byte YES = 1;
	public static final byte NO = 0;
	public static final byte DEL = -1;

	protected String name;// 名称
	protected Long userId;// 创建人ID
	protected String userName;// 创建人名称
	protected Date createDate;// 创建时间

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
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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
	public void userOptions(BaseEntity<Long> user) {
		this.userId = user.getId();
		this.userName = user.getName();
	}
}