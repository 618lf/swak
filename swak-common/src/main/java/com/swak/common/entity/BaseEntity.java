package com.swak.common.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 基础实体类
 * @author lifeng
 * @param <PK>
 */
public abstract class BaseEntity<PK> extends IdEntity<PK> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//path 中的分隔
	public static final String PATH_SEPARATE = "/";
	//IDS 中的分隔
	public static final String IDS_SEPARATE = ",";
	// 是/否 - TINYINT
	public static final byte YES = 1;
	public static final byte NO = 0;

	// 删除标记（0：正常；1：删除；2：审核；） - TINYINT
	public static final byte DEL_FLAG_NORMAL = 0;
	public static final byte DEL_FLAG_DELETE = 1;
	public static final byte DEL_FLAG_AUDIT = 2;
	
	protected String name;//名称
	protected Long createId;//创建人ID
	protected String createName;//创建人名称
	protected Date createDate;//创建时间
    protected Long updateId;//修改人ID
    protected String updateName;//修改人名称
	protected Date updateDate;//修改时间
    protected Byte delFlag = DEL_FLAG_NORMAL; // 删除标记（0：正常；1：删除；2：审核）
    protected String remarks;//描述 
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getCreateId() {
		return createId;
	}
	public void setCreateId(Long createId) {
		this.createId = createId;
	}
	public Long getUpdateId() {
		return updateId;
	}
	public void setUpdateId(Long updateId) {
		this.updateId = updateId;
	}
	public String getCreateName() {
		return createName;
	}
	public void setCreateName(String createName) {
		this.createName = createName;
	}
	public String getUpdateName() {
		return updateName;
	}
	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	@JSONField(format="yyyy-MM-dd HH:mm:ss")
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public Byte getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(Byte delFlag) {
		this.delFlag = delFlag;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	/**
	 * 新增操作
	 * @return
	 */
	@Override
	public PK prePersist() {
		this.createDate = new Timestamp(new Date().getTime());
		this.updateDate = this.createDate;
		return super.prePersist();
	}
	
	/**
	 * 修改操作
	 */
	public void preUpdate() {
		this.updateDate = new Timestamp(new Date().getTime());
	}
	
	/**
	 * 用户当前的操作
	 * @param user
	 */
	public void userOptions(BaseEntity<Long> user) {
		this.createId = user.getId(); 
		this.createName = user.getName();
		this.updateId = user.getId();
		this.updateName = user.getName();
	}
}