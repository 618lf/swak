package com.swak.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author TMT
 */
@SuppressWarnings("rawtypes")
public class Page implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Parameters param;
	
	private List data;

	public  Page(){
		super();
	}

	public <T> Page(int pageSize,int pageNum,int recordCount,List<T> page){
		this.param = new Parameters(pageNum,pageSize,recordCount);
		this.data = page;
	}
	
	public <T> Page(Parameters Parameters,List<T> page){
		this.param = new Parameters(Parameters.getPageIndex(),Parameters.getPageSize(),Parameters.getRecordCount());
		this.param.setSortField(Parameters.getSortField());
		this.param.setSortType(Parameters.getSortType());	
		this.param.setPageUrl(Parameters.getPageUrl());
		this.param.setSerializePage(Parameters.getSerializePage());
		this.data = page;
	}

	public void setPage(Parameters Parameters) {
		this.param = Parameters;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getData() {
		return data;
	}
	
	public void setData(List data) {
		this.data = data;
		if(this.param == null && this.data != null) {
			param = new Parameters();
			param.setRecordCount(this.data.size());
		}
	}

	public Parameters getParam() {
		if(this.param == null) {
			param = new Parameters();
		}
		return param;
	}
	
	/**
	 *  分页的三种方式：
	 *  1. jqgrid 获取json数据，本身有分页的功能，不许要序列化分页组件
	 *  2. ajax 显示分页数据，需要序列化，统一使用 page.pagination;
	 *  3. jsp 中使用分页 ${page.pagination}
	 */
	public String getPagination(){
		return getParam().getPagination();
	}
}
