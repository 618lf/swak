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

	private PageParameters param;
	
	private List data;

	public  Page(){
		super();
	}

	public <T> Page(int pageSize,int pageNum,int recordCount,List<T> page){
		this.param = new PageParameters(pageNum,pageSize,recordCount);
		this.data = page;
	}
	
	public <T> Page(PageParameters pageParameters,List<T> page){
		this.param = new PageParameters(pageParameters.getPageIndex(),pageParameters.getPageSize(),pageParameters.getRecordCount());
		this.param.setSortField(pageParameters.getSortField());
		this.param.setSortType(pageParameters.getSortType());	
		this.param.setPageUrl(pageParameters.getPageUrl());
		this.param.setSerializePage(pageParameters.getSerializePage());
		this.data = page;
	}

	public void setPage(PageParameters pageParameters) {
		this.param = pageParameters;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getData() {
		return data;
	}
	
	public void setData(List data) {
		this.data = data;
		if(this.param == null && this.data != null) {
			param = new PageParameters();
			param.setRecordCount(this.data.size());
		}
	}

	public PageParameters getParam() {
		if(this.param == null) {
			param = new PageParameters();
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
