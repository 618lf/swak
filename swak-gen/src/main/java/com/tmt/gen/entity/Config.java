/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.tmt.gen.entity;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 生成方案Entity
 * @author ThinkGem
 * @version 2013-10-15
 */
@XmlRootElement(name="config")
public class Config implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private List<Category> categoryList;	// 代码模板分类
	private List<Label> javaTypeList;		// Java类型
	private List<Label> queryTypeList;		// 查询类型
	private List<Label> showTypeList;		// 显示类型
	private List<Label> checkTypeList;      // 校验类型

	public Config() {
		super();
	}

	@XmlElementWrapper(name = "categorys")
	@XmlElement(name = "category")
	public List<Category> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<Category> categoryList) {
		this.categoryList = categoryList;
	}

	@XmlElementWrapper(name = "javaType")
	@XmlElement(name = "dict")
	public List<Label> getJavaTypeList() {
		return javaTypeList;
	}

	public void setJavaTypeList(List<Label> javaTypeList) {
		this.javaTypeList = javaTypeList;
	}

	@XmlElementWrapper(name = "queryType")
	@XmlElement(name = "dict")
	public List<Label> getQueryTypeList() {
		return queryTypeList;
	}

	public void setQueryTypeList(List<Label> queryTypeList) {
		this.queryTypeList = queryTypeList;
	}

	@XmlElementWrapper(name = "showType")
	@XmlElement(name = "dict")
	public List<Label> getShowTypeList() {
		return showTypeList;
	}

	public void setShowTypeList(List<Label> showTypeList) {
		this.showTypeList = showTypeList;
	}

	@XmlElementWrapper(name = "checkType")
	@XmlElement(name = "dict")
	public List<Label> getCheckTypeList() {
		return checkTypeList;
	}

	public void setCheckTypeList(List<Label> checkTypeList) {
		this.checkTypeList = checkTypeList;
	}
	
}