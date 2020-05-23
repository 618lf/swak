package com.swak.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 分页数据
 *
 * @author: lifeng
 * @date: 2020/3/29 11:16
 */
@SuppressWarnings("rawtypes")
public class Page implements Serializable {

    private static final long serialVersionUID = 1L;

    private Parameters param;

    private List data;

    public Page() {
        super();
    }

    public <T> Page(Parameters parameters, List<T> page) {
        this.param = new Parameters(parameters.getPageIndex(), parameters.getPageSize(), parameters.getRecordCount());
        this.param.setSortField(parameters.getSortField());
        this.param.setSortType(parameters.getSortType());
        this.data = page;
    }

    public void setPage(Parameters parameters) {
        this.param = parameters;
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
