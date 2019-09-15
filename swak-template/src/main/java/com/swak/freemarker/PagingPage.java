package com.swak.freemarker;

import java.util.List;

import com.swak.entity.Page;
import com.swak.entity.Parameters;
import com.swak.utils.StringUtils;

/**
 * 待有分页条的分页数据
 * 
 * @author lifeng
 */
public class PagingPage extends Page {

	private static final long serialVersionUID = 1L;

	private String url = "{pageIndex}.html";
	private int pageIndex;// 当前页面的页数
	private int pageSize;// 页面大小
	private int recordCount = -1;// 数据总量
	private int first;// 首页索引
	private int last;// 尾页索引
	private int prev;// 上一页索引
	private int next;// 下一页索引
	private int length = 8;// 显示页面长度
	private int slider = 1;// 前后显示页面长度

	/**
	 * 可以设置分页的地址
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 初始化参数
	 */
	private void initialize() {
		this.first = 1;
		this.last = (int) (recordCount / (this.pageSize < 1 ? 20 : this.pageSize) + first - 1);
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
		// 2
		if (this.pageIndex < this.first) {// 如果当前页小于首页
			this.pageIndex = this.first;
		}
		if (this.pageIndex > this.last) {// 如果当前页大于尾页
			this.pageIndex = this.last;
		}
	}

	private String getPageUrl(int pageIndex, int pageSize) {
		String _url = StringUtils.replace(this.url, "{pageIndex}", String.valueOf(pageIndex));
		return StringUtils.replace(_url, "{pageSize}", String.valueOf(pageSize));
	}
	
	/**
	 * 获取数据
	 */
	public <T> List<T> getData() {
		return super.getData();
	}

	/**
	 * 分页参数
	 */
	public Parameters getParam() {
		return super.getParam();
	}
	
	/**
	 * 获取分页
	 * 
	 * @return
	 */
	public String getPagination() {
		initialize();
		StringBuilder sb = new StringBuilder();
		if (pageIndex == first) {
			sb.append("<li class=\"disabled\"><a href=\"javascript:\">&#171; 上一页</a></li>");
		} else {
			String _url = this.getPageUrl(prev, pageSize);
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
				String _url = this.getPageUrl(i, pageSize);
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
				String _url = this.getPageUrl(i, pageSize);
				sb.append("<li><a href=\"").append(_url).append("\">").append((i + 1 - first)).append("</a></li>");
			}
		}

		if (last - end > slider) {
			sb.append("<li class=\"disabled\"><a href=\"javascript:void(0)\">...</a></li>");
			end = last - slider;
		}

		for (int i = end + 1; i <= last; i++) {
			String _url = this.getPageUrl(i, pageSize);
			sb.append("<li><a href=\"").append(_url).append("\">").append((i + 1 - first)).append("</a></li>");
		}

		if (pageIndex == last) {
			sb.append("<li class=\"disabled\"><a href=\"javascript:void(0)\">下一页 &#187;</a></li>");
		} else {
			String _url = this.getPageUrl(next, pageSize);
			sb.append("<li><a href=\"").append(_url).append("\">").append("下一页 &#187;").append("</a></li>");
		}
		sb.insert(0, "<ul class=\"pagination\">").append("</ul>");
		return sb.toString();
	}

	public static PagingPage page(Page page) {
		PagingPage _page = new PagingPage();
		_page.pageIndex = page.getParam().getPageIndex();
		_page.pageSize = page.getParam().getPageSize();
		_page.recordCount = page.getParam().getRecordCount();
		return _page;
	}
}
