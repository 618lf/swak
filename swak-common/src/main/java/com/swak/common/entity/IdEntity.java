package com.swak.common.entity;

import java.io.Serializable;

import com.swak.common.persistence.incrementer.IdGen;

public abstract class IdEntity<PK> implements Serializable{

	private static final long serialVersionUID = 1L;
	//公有字段
	protected PK id; // 编号
	protected Integer version;//版本, 用于乐观锁
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
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PK prePersist() {
		Serializable pk = IdGen.key();
		if(this.id instanceof String) {
		   this.setId((PK)String.valueOf(pk));
		} else {
		   this.setId((PK)pk);
		}
		return this.getId();
	}
}
