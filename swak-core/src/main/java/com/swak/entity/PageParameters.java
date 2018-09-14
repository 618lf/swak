package com.swak.entity;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.swak.utils.StringUtils;

public class PageParameters implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public final static String DESC = "desc";
    public final static String ASC = "asc";
    public static final int DEFAULT_PAGE_INDEX = 1;//默认当前页面页数为第一页
	public static final int DEFAULT_PAGE_SIZE = 15;//默认页面大小为15
	public static final int NO_PAGINATION = -1;//表示不分页
		
	private int pageIndex;//当前页面的页数
	private int pageSize;// 页面大小
	private int recordCount = -1;//数据总量
    private String sortField;//排序参数
    private String sortType;//排序类型
    private String sortName;//排序名称(数据库中统一管理排序)
	private int first;// 首页索引
	private int last;// 尾页索引
	private int prev;// 上一页索引
	private int next;// 下一页索引
	private int length = 8;// 显示页面长度
	private int slider = 1;// 前后显示页面长度
	private String funcName = "page"; // 设置点击页码调用的js函数名称，默认为page，在一页有多个分页对象时使用。
	private String pageUrl;//分页的地址，如果设置了这个这优先使用格式?pageNO={pageIndex}&pageSize={pageSize},格式不限，但通过这两个参数获取
	private boolean serializePage = Boolean.TRUE;//默认是序列化page
	
	/**
	 * 以默认当前页面和页面大小构造一个分页对象。 其中，默认当前页数为1，默认页面大小为15。
	 */
	public PageParameters() {
		this.pageIndex = DEFAULT_PAGE_INDEX;
		this.pageSize = DEFAULT_PAGE_SIZE;
	}
	
	public boolean isOrderEmptiy(){
		return StringUtils.isEmpty(this.getSortField())||StringUtils.isEmpty(this.getSortType());
	}
	
	/**
	 * 以指定的当前页面页数和页面大小构造一个分页对象。
	 * 
	 * @param pageIndex
	 *            当前页数，若参数值不大于0，则使用默认值1。
	 * @param pageSize
	 *            页面大小，若参数值不大于0，则使用默认值10。
	 */
	public PageParameters(int pageIndex, int pageSize,int recordCount) {
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
		if(this.recordCount == 0)
			return 0;
		int pageCount = this.recordCount/this.pageSize;
		if(this.recordCount%this.pageSize > 0)
			pageCount ++;
		return pageCount;
	}
	
	public int getRecordCount() {
		return recordCount;
	}
	
	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}
	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	
	public String getPageUrl(int pageIndex, int pageSize) {
		String _url = StringUtils.replace(this.pageUrl, "{pageIndex}", String.valueOf(pageIndex));
	    return StringUtils.replace(_url, "{pageSize}", String.valueOf(pageSize));
	}
	public void setSerializePage(boolean serializePage) {
		this.serializePage = serializePage;
	}
	public boolean getSerializePage() {
		return this.serializePage;
	}

	/**
	 * 初始化参数
	 */
	public void initialize(){
		this.first = 1;
		this.last = (int)(recordCount / (this.pageSize < 1 ? 20 : this.pageSize) + first - 1);
		if (this.recordCount % this.pageSize != 0 || this.last == 0) {
			this.last++;
		}
		if (this.last < this.first) {
			this.last = this.first;
		}
		if (this.pageIndex <= 1) {
			this.pageIndex = this.first;
		}
		if (this.pageIndex >= this.last) {
			this.pageIndex = this.last;
		}
		if (this.pageIndex < this.last - 1) {
			this.next = this.pageIndex + 1;
		} else {
			this.next = this.last;
		}
		if (this.pageIndex > 1) {
			this.prev = this.pageIndex - 1;
		} else {
			this.prev = this.first;
		}
		//2
		if (this.pageIndex < this.first) {// 如果当前页小于首页
			this.pageIndex = this.first;
		}
		if (this.pageIndex > this.last) {// 如果当前页大于尾页
			this.pageIndex = this.last;
		}
	}
	
	/**
	 * serializePage :默认是 true
	 * 是否需要序列化分页条
	 * 分页条
	 * @return
	 */
	@JSONField(serialize=false)
	public String getPagination(){
		if (serializePage) {
			if (StringUtils.hasText(this.getPageUrl())) {
				return this.getUrlPagination();
			}
			return this.getSimplePagination();
		}
		return null;
	}
	
	/**
	 * 获取分页，不需要js的支持
	 * @return
	 */
	@JSONField(serialize=false)
	public String getUrlPagination() {
		initialize();
		StringBuilder sb = new StringBuilder();
		if (pageIndex == first) {
			sb.append("<li class=\"disabled\"><a href=\"javascript:\">&#171; 上一页</a></li>");
		} else {
			String _url  = this.getPageUrl(prev, pageSize);
			sb.append("<li><a href=\"").append(_url).append("\">&#171; 上一页</a></li>");
		}
		
		int begin = pageIndex - (length / 2);
		if (begin < first) {
			begin = first;
		}
		int end = begin + length - 1;
		if (end >= last) {
			end = last;
			begin = end - length + 1;
			if (begin < first) {
				begin = first;
			}
		}
		
		if (begin > first) {
			int i = 0;
			for (i = first; i < first + slider && i < begin; i++) {
				String _url  = this.getPageUrl(i, pageSize);
				sb.append("<li><a href=\"").append(_url).append("\">").append((i + 1 - first)).append("</a></li>");
			}
			if (i < begin) {
				sb.append("<li class=\"disabled\"><a href=\"javascript:void(0)\">...</a></li>");
			}
		}
		
		for (int i = begin; i <= end; i++) {
			if (i == pageIndex) {
				sb.append("<li class=\"active\"><a href=\"javascript:void(0)\">").append((i + 1 - first))
				  .append("</a></li>");
			} else {
				String _url  = this.getPageUrl(i, pageSize);
				sb.append("<li><a href=\"").append(_url).append("\">").append((i + 1 - first)).append("</a></li>");
			}
		}
		
		if (last - end > slider) {
			sb.append("<li class=\"disabled\"><a href=\"javascript:void(0)\">...</a></li>");
			end = last - slider;
		}
		
		for (int i = end + 1; i <= last; i++) {
			String _url  = this.getPageUrl(i, pageSize);
			sb.append("<li><a href=\"").append(_url).append("\">").append((i + 1 - first)).append("</a></li>");
		}
		
		if (pageIndex == last) {
			sb.append("<li class=\"disabled\"><a href=\"javascript:void(0)\">下一页 &#187;</a></li>");
		} else {
			String _url  = this.getPageUrl(next, pageSize);
			sb.append("<li><a href=\"").append(_url).append("\">").append("下一页 &#187;").append("</a></li>");
		}
		sb.insert(0,"<ul class=\"pagination\">").append("</ul>");
		return sb.toString();
	}
	
	/**
	 * 获取分页统一通过 page 获取
	 * @return
	 */
	@JSONField(serialize=false)
	public String getSimplePagination(){
        initialize();
		StringBuilder sb = new StringBuilder();
		if (pageIndex == first) {// 如果是首页
			sb.append("<li class=\"disabled\"><a href=\"javascript:\">&#171; 上一页</a></li>");
		} else {
			sb.append("<li><a href=\"javascript:").append(funcName).append("(").append(prev).append(",").append(pageSize).append(");\">&#171; 上一页</a></li>");
		}
		int begin = pageIndex - (length / 2);
		if (begin < first) {
			begin = first;
		}
		int end = begin + length - 1;
		if (end >= last) {
			end = last;
			begin = end - length + 1;
			if (begin < first) {
				begin = first;
			}
		}
		if (begin > first) {
			int i = 0;
			for (i = first; i < first + slider && i < begin; i++) {
				sb.append("<li><a href=\"javascript:").append(funcName).append("(").append(i).append(",").append(pageSize).append(");\">")
				.append(i + 1 - first).append("</a></li>");
			}
			if (i < begin) {
				sb.append("<li class=\"disabled\"><a href=\"javascript:\">...</a></li>");
			}
		}
		for (int i = begin; i <= end; i++) {
			if (i == pageIndex) {
				sb.append("<li class=\"active\"><a href=\"javascript:\">").append((i + 1 - first))
						.append("</a></li>");
			} else {
				sb.append("<li><a href=\"javascript:").append(funcName).append("(").append(i).append(",").append(pageSize).append(");\">")
						.append(i + 1 - first).append("</a></li>");
			}
		}
		if (last - end > slider) {
			sb.append("<li class=\"disabled\"><a href=\"javascript:\">...</a></li>");
			end = last - slider;
		}
		for (int i = end + 1; i <= last; i++) {
			sb.append("<li><a href=\"javascript:").append(funcName).append("(").append(i).append(",").append(pageSize).append(");\">")
					.append(i + 1 - first).append("</a></li>");
		}
		if (pageIndex == last) {
			sb.append("<li class=\"disabled\"><a href=\"javascript:\">下一页 &#187;</a></li>");
		} else {
			sb.append("<li><a href=\"javascript:").append(funcName).append("(").append(next).append(",").append(pageSize).append(");\">")
					.append("下一页 &#187;</a></li>");
		}
		sb.insert(0,"<ul class=\"pagination\">").append("</ul>");
		return sb.toString();
	}
	
	/**
	 * 属性转换为 --- 数据库的字段(不建议使用)
	 * @param clazz
	 * @return
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
	 * @param clazz
	 * @return
	 */
	public String orderBy() {
		return this.orderBy(null);
	}
}
