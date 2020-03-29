package com.swak.entity;

import java.io.Serializable;

import com.swak.utils.StringUtils;

/**
 * 分页参数
 *
 * @author lifeng
 */
public class Parameters implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String DESC = "desc";
    public static final String ASC = "asc";
    public static final int DEFAULT_PAGE_INDEX = 1;
    public static final int DEFAULT_PAGE_SIZE = 15;
    public static final int NO_PAGINATION = -1;

    private int pageIndex;
    private int pageSize;
    private int recordCount = -1;
    private String sortField;
    private String sortType;
    private String sortName;

    /**
     * 以默认当前页面和页面大小构造一个分页对象。 其中，默认当前页数为1，默认页面大小为15。
     */
    public Parameters() {
        this.pageIndex = DEFAULT_PAGE_INDEX;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    /**
     * 以指定的当前页面页数和页面大小构造一个分页对象。
     *
     * @param pageIndex 当前页数，若参数值不大于0，则使用默认值1。
     * @param pageSize  页面大小，若参数值不大于0，则使用默认值10。
     */
    public Parameters(int pageIndex, int pageSize, int recordCount) {
        this.pageIndex = pageIndex > 0 ? pageIndex : DEFAULT_PAGE_INDEX;
        this.pageSize = pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
        this.recordCount = recordCount;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortFields) {
        this.sortField = sortFields;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String orderType) {
        this.sortType = orderType;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageCount() {
        if (this.recordCount == 0) {
            return 0;
        }
        int pageCount = this.recordCount / this.pageSize;
        if (this.recordCount % this.pageSize > 0) {
            pageCount++;
        }
        return pageCount;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    /**
     * 属性转换为 --- 数据库的字段(不建议使用)
     *
     * @param defaulOrder 排序参数
     * @return 排序条件
     */
    public String orderBy(String defaulOrder) {
        StringBuilder orderBy = new StringBuilder();
        if (StringUtils.isNotBlank(this.getSortField())) {
            orderBy.append(StringUtils.convertProperty2Column(this.getSortField()));
            if (StringUtils.isNotBlank(this.getSortType())) {
                orderBy.append(" ").append(StringUtils.upperCase(this.getSortType()));
            }
        }
        if (orderBy.length() == 0 && StringUtils.isNotBlank(defaulOrder)) {
            orderBy.append(defaulOrder);
        }
        return orderBy.toString();
    }

    /**
     * 获取字段转换
     *
     * @return 默认的排序
     */
    public String orderBy() {
        return this.orderBy(null);
    }
}
