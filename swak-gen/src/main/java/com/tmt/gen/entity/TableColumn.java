package com.tmt.gen.entity;

import java.io.Serializable;

import com.swak.entity.BaseEntity;
import com.swak.utils.StringUtils;

/**
 * 表列
 * @author lifeng
 *
 */
public class TableColumn extends BaseEntity<Long> implements Serializable{
   
	private static final long serialVersionUID = -5834373642030771757L;
	
	private Long genTableId;
	private String tableName;
    private String name;
    private String comments;
    private String dbType;
    private String jdbcType;
    private String javaType;
    private String javaField;
    private Byte isPk;
    private Byte isNull;
    private Byte isDbNull;//数据库是否为null
    private Byte isInsert;//暂时不需要这个字段
    private Byte isEdit;
    private Byte isList;
    private Byte isQuery;
    private String queryType;//查询方式
    private String showType;//表单显示
    private String checkType;//校验类型
    
    private String dictType;//暂时不需要这个字段
    private String settings;//暂时不需要这个字段
    private Integer sort;//暂时不需要这个字段
    private Long length;
    private Long scale;
    
	@Override
	public Long getId() {
		return this.id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	public Byte getIsDbNull() {
		return isDbNull;
	}
	public void setIsDbNull(Byte isDbNull) {
		this.isDbNull = isDbNull;
	}
	public void setIsNull(Byte isNull) {
		this.isNull = isNull;
	}
	public void setIsInsert(Byte isInsert) {
		this.isInsert = isInsert;
	}
	public void setIsEdit(Byte isEdit) {
		this.isEdit = isEdit;
	}
	public void setIsList(Byte isList) {
		this.isList = isList;
	}
	public void setIsQuery(Byte isQuery) {
		this.isQuery = isQuery;
	}
	public Byte getIsNull() {
		return isNull;
	}
	public Byte getIsInsert() {
		return isInsert;
	}
	public Byte getIsEdit() {
		return isEdit;
	}
	public Byte getIsList() {
		return isList;
	}
	public Byte getIsQuery() {
		return isQuery;
	}
	public String getCheckType() {
		return checkType;
	}
	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}
	public Long getLength() {
		return length;
	}
	public void setLength(Long length) {
		this.length = length;
	}
	public Long getScale() {
		return scale;
	}
	public void setScale(Long scale) {
		this.scale = scale;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public Long getGenTableId() {
		return genTableId;
	}
	public void setGenTableId(Long genTableId) {
		this.genTableId = genTableId;
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
		return isPk;
	}
	public void setIsPk(Byte isPk) {
		this.isPk = isPk;
	}
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	public String getShowType() {
		return showType;
	}
	public void setShowType(String showType) {
		this.showType = showType;
	}
	public String getDictType() {
		return dictType;
	}
	public void setDictType(String dictType) {
		this.dictType = dictType;
	}
	public String getSettings() {
		return settings;
	}
	public void setSettings(String settings) {
		this.settings = settings;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public String getJdbcType() {
		return jdbcType;
	}
	public void setJdbcType(String jdbcType) {
		this.jdbcType = jdbcType;
	}
	
	/**
	 * 是否内置字段
	 * @return
	 */
	public Boolean getIsBaseEntity() {
		if(StringUtils.contains(",id,parentId,parentIds,level,version,createId,createName,createDate,updateId,updateName,updateDate,delFlag,remarks,", "," + this.getJavaField()+",")) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
	 * 是否是插入时的字段，（不许要修改）
	 * @return
	 */
	public Boolean getIsInsertField() {
		if(StringUtils.contains(",id,version,createId,createName,createDate,", "," + this.getJavaField()+",")) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}