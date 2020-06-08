package com.sample.tools.plugin.plugins.codegen.gen;

import com.swak.utils.StringUtils;

/**
 * 表列
 * 
 * @author lifeng
 */
public class TableColumn {

	private String name;
	private String comments;
	private String dbType;
	private String jdbcType;
	private String javaType;
	private String javaField;
	private Byte isPk;
	private Byte isNull;

	public void setIsNull(Byte isNull) {
		this.isNull = isNull;
	}

	public Byte getIsNull() {
		return isNull == null ? 0 : isNull;
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

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	public String getJavaField() {
		return javaField;
	}

	public void setJavaField(String javaField) {
		this.javaField = javaField;
	}

	public Byte getIsPk() {
		return isPk == null ? 0 : isPk;
	}

	public void setIsPk(Byte isPk) {
		this.isPk = isPk;
	}

	public String getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(String jdbcType) {
		this.jdbcType = jdbcType;
	}

	/**
	 * 是否内置字段
	 * 
	 * @return
	 */
	public Boolean getIsBaseEntity() {
		if (StringUtils.contains(
				",id,parentId,parentIds,level,version,createId,createName,createDate,updateId,updateName,updateDate,delFlag,remarks,",
				"," + this.getJavaField() + ",")) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * 是否是插入时的字段，（不许要修改）
	 * 
	 * @return
	 */
	public Boolean getIsInsertField() {
		if (StringUtils.contains(",id,version,createId,createName,createDate,", "," + this.getJavaField() + ",")) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}