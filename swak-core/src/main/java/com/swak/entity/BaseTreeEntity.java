package com.swak.entity;

import java.io.Serializable;

/**
 * jqGrid Tree 基础节点
 *
 * @author lifeng
 */
public abstract class BaseTreeEntity<PK> extends BaseParentEntity<PK> implements Serializable {

    private static final long serialVersionUID = 1L;
    protected PK parent;
    protected Boolean isLeaf = Boolean.TRUE;
    protected Boolean expanded = Boolean.FALSE;
    protected Boolean loaded = Boolean.TRUE;

    public PK getParent() {
        return parent;
    }

    public void setParent(PK parent) {
        this.parent = parent;
    }

    public Boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public Boolean getLoaded() {
        return loaded;
    }

    public void setLoaded(Boolean loaded) {
        this.loaded = loaded;
    }

    public Boolean getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(Boolean isLeaf) {
        this.isLeaf = isLeaf;
    }
}