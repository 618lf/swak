package com.swak.entity;

import com.swak.Constants;
import com.swak.utils.StringUtils;

import java.io.Serializable;

/**
 * 针对有 parent属性的实体
 *
 * @author liFeng 2014年5月22日
 */
public abstract class BaseParentEntity<PK> extends BaseEntity<PK> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String path;
    protected Integer level = 1;
    protected Integer sort = 1;
    protected PK parentId;
    protected String parentIds;
    protected String parentName;

    public String getPath() {
        return path == null ? "" : path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public PK getParentId() {
        return parentId;
    }

    public void setParentId(PK parentId) {
        this.parentId = parentId;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    /**
     * 将父区域中的属性添加到子区域中
     *
     * @param parent 父实体
     */
    public void fillByParent(BaseParentEntity<PK> parent) {
        int level = parent == null ? 0 : parent.getLevel();
        String parentIds = parent == null ? String.valueOf(Constants.INVALID_ID) : (parent.getParentIds() + this.getParentId());
        this.setLevel(level + 1);
        this.setParentIds(parentIds + Constants.IDS_SEPARATE);
        this.setPath((parent == null ? "" : StringUtils.defaultString(parent.getPath(), "")) + Constants.PATH_SEPARATE + this.getName());
    }

    /**
     * 父节点的Path修改之后，所有的子节点都要修改
     *
     * @param parent       父实体
     * @param oldParentIds 之前的路径
     */
    public void updateIdsByParent(BaseParentEntity<PK> parent, String oldParentIds, String oldPaths, Integer oldLevel) {
        String parentIds = (Constants.IDS_SEPARATE + this.getParentIds()).replace(Constants.IDS_SEPARATE + oldParentIds, Constants.IDS_SEPARATE + parent.getParentIds());
        if (!StringUtils.isNotBlank(parentIds)) {
            parentIds = String.valueOf(Constants.INVALID_ID);
        }
        if (StringUtils.startsWith(parentIds, Constants.IDS_SEPARATE)) {
            parentIds = StringUtils.removeStart(parentIds, Constants.IDS_SEPARATE);
        }
        this.setParentIds(parentIds);

        //level
        int changeLevel = parent.getLevel() - oldLevel;
        this.setLevel(this.getLevel() + changeLevel);

        //path
        if (StringUtils.isNotBlank(oldPaths)) {
            String paths = (this.getPath()).replace(oldPaths, parent.getPath());
            if (!StringUtils.isNotBlank(paths)) {
				paths = Constants.PATH_SEPARATE + this.getName();
            }
            this.setPath(paths);
        }
    }
}