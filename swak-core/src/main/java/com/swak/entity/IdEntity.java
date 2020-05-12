package com.swak.entity;

import java.io.Serializable;

import com.swak.incrementer.IdGen;

/**
 * 最基本实体 bug修复： 如果setXX 返回值不是void，通过反射设置数据的插件会设值失败
 *
 * @author: lifeng
 * @date: 2020/3/29 11:13
 */
public abstract class IdEntity<PK> implements Serializable {

	private static final long serialVersionUID = 1L;
	protected PK id;
	protected Integer version;

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
	 * @return 主键
	 */
	public PK prePersist() {
		this.setId(IdGen.id());
		return this.getId();
	}
}