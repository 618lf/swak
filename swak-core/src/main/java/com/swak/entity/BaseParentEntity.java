package com.swak.entity;

import java.io.Serializable;

import com.swak.Constants;
import com.swak.utils.StringUtils;

/**
 * 针对有 parent属性的实体
 * 
 * @author liFeng 2014年5月22日
 */
public abstract class BaseParentEntity<PK> extends BaseEntity<PK> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	// path 中的分隔
	public static final String PATH_SEPARATE = "/";
	// IDS 中的分隔
	public static final String IDS_SEPARATE = ",";
	
	//公有字段
	protected String path;
	protected Integer level = 1; // 给一个默认值
	protected Integer sort = 1;// 给一个默认值
	protected PK parentId;
	protected String parentIds;
	protected String parentName;

	public String getPath() {
		return path == null?"":path;
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
	 * @param parent
	 */
	public void fillByParent(BaseParentEntity<PK> parent) {
		int level = parent == null ? 0 : parent.getLevel();
		String parentIds = parent == null ? String.valueOf(Constants.INVALID_ID): (new StringBuilder(parent.getParentIds()).append(this.getParentId()).toString());
		this.setLevel(level + 1);
		this.setParentIds(new StringBuilder(parentIds).append(IDS_SEPARATE).toString());
		this.setPath(new StringBuilder((parent == null?"":StringUtils.defaultString(parent.getPath(), ""))).append(PATH_SEPARATE).append(this.getName()).toString());
	}

	/**
	 * 父节点的Path修改之后，所有的子节点都要修改
	 * 
	 * @param parent
	 * @param oldParentIds
	 */
	public void updateIdsByParent(BaseParentEntity<PK> parent, String oldParentIds, String oldPaths, Integer oldLevel) {
		String _parentIds = (IDS_SEPARATE + this.getParentIds()).replace(IDS_SEPARATE + oldParentIds, IDS_SEPARATE + parent.getParentIds());
		if (!StringUtils.isNotBlank(_parentIds)) {
			_parentIds = String.valueOf(Constants.INVALID_ID);
		}
		if (StringUtils.startsWith(_parentIds, IDS_SEPARATE) ) {
			_parentIds = StringUtils.removeStart(_parentIds, IDS_SEPARATE);
		}
		this.setParentIds(_parentIds);
		
		//level
		int changeLevel = parent.getLevel() - oldLevel;
		this.setLevel(this.getLevel() + changeLevel);
		
		//path
		if (StringUtils.isNotBlank(oldPaths)) {
			String _paths = (this.getPath()).replace(oldPaths, parent.getPath());
			if (!StringUtils.isNotBlank(_paths)) {
				_paths = PATH_SEPARATE + this.getName();
			}
			this.setPath(_paths);
		}
	}
}