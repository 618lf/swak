package com.swak.entity;

import java.io.Serializable;

import com.swak.incrementer.IdGen;

/**
 * 最基本实体
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

    @SuppressWarnings("unchecked")
    public <T> T setId(PK id) {
        this.id = id;
        return (T) this;
    }

    public Integer getVersion() {
        return version;
    }

    @SuppressWarnings("unchecked")
    public <T> T setVersion(Integer version) {
        this.version = version;
        return (T) this;
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