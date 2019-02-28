package com.swak.gen.config;

import java.io.Serializable;
import java.util.List;

import com.swak.entity.BaseEntity;
import com.swak.utils.Lists;

/**
 * 表
 * 
 * @author lifeng
 *
 */
public class Table extends BaseEntity<Long> implements Serializable {

	private static final long serialVersionUID = -6205310680425185756L;

	private String name;
	private String comments;
	private String className;
	private String parentTable;// 现在改为：子表，内置表
	private String parentTableFk;
	private List<TableColumn> columns;

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public List<TableColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<TableColumn> columns) {
		this.columns = columns;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getParentTable() {
		return parentTable;
	}

	public void setParentTable(String parentTable) {
		this.parentTable = parentTable;
	}

	public String getParentTableFk() {
		return parentTableFk;
	}

	public void setParentTableFk(String parentTableFk) {
		this.parentTableFk = parentTableFk;
	}

	public String getPkJavaType() {
		return "Long";
	}

	/**
	 * 第一个string类型的字段
	 * 
	 * @return
	 */
	public String getFisrtStringField() {
		if (this.columns != null && this.columns.size() != 0) {
			for (TableColumn column : columns) {
				if ("String".equals(column.getJavaType()) && !column.getIsBaseEntity()) {
					return column.getJavaField();
				}
			}
		}
		return "name";
	}

	/**
	 * 是否包含path字段
	 * 
	 * @return
	 */
	public Boolean getHasPath() {
		if (this.columns != null && this.columns.size() != 0) {
			for (TableColumn column : columns) {
				if ("String".equals(column.getJavaType()) && "path".equals(column.getJavaField())) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * 富文本字段
	 * 
	 * @return
	 */
	public List<String> getRichtexts() {
		if (this.columns != null && this.columns.size() != 0) {
			List<String> richs = Lists.newArrayList();
			for (TableColumn column : columns) {
				if ("richtext".equals(column.getShowType())) {
					richs.add(column.getJavaField());
				}
			}
			return richs.size() == 0 ? null : richs;
		}
		return null;
	}

	/**
	 * 是否包含path字段
	 * 
	 * @return
	 */
	public Boolean getHasSort() {
		if (this.columns != null && this.columns.size() != 0) {
			for (TableColumn column : columns) {
				if ("sort".equals(column.getJavaField())) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	public TableColumn getImageColumn() {
		if (this.columns != null && this.columns.size() != 0) {
			for (TableColumn column : columns) {
				if ("singleimg".equals(column.getShowType())) {
					return column;
				}
			}
		}
		return null;
	}

	/**
	 * 是否有发布字段
	 * 
	 * @return
	 */
	public TableColumn getPublishColumn() {
		if (this.columns != null && this.columns.size() != 0) {
			for (TableColumn column : columns) {
				if ("pub_unpub".equals(column.getShowType())) {
					return column;
				}
			}
		}
		return null;
	}

	/**
	 * 是否有日期
	 * 
	 * @return
	 */
	public Boolean getHasDate() {
		if (this.columns != null && this.columns.size() != 0) {
			for (TableColumn column : columns) {
				if ("date".equals(column.getShowType()) && !column.getIsBaseEntity()) {
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}
}