package com.swak.entity;

import java.io.Serializable;

import com.swak.incrementer.IdGen;

public abstract class IdEntity<PK> implements Serializable {

	private static final long serialVersionUID = 1L;
	// 公有字段
	protected PK id; // 编号
	protected Integer version;// 版本, 用于乐观锁

	public PK getId() {
		return id;
	}

	public void setId(PK id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	/**
	 * 新增操作
	 * 
	 * @return
	 */
	public PK prePersist() {
		this.setId(IdGen.id());
		return this.getId();
	}
}