package com.swak.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author TMT
 */
@SuppressWarnings("rawtypes")
public class Page implements Serializable {

	private static final long serialVersionUID = 1L;

	private Parameters param;

	private List data;

	public Page() {
		super();
	}

	public <T> Page(Parameters Parameters, List<T> page) {
		this.param = new Parameters(Parameters.getPageIndex(), Parameters.getPageSize(), Parameters.getRecordCount());
		this.param.setSortField(Parameters.getSortField());
		this.param.setSortType(Parameters.getSortType());
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
		if (this.param == null && this.data != null) {
			param = new Parameters();
			param.setRecordCount(this.data.size());
		}
	}

	public Parameters getParam() {
		if (this.param == null) {
			param = new Parameters();
		}
		return param;
	}
}
