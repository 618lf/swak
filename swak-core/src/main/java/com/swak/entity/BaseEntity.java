package com.swak.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
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
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public <T> T setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
        return (T) this;
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