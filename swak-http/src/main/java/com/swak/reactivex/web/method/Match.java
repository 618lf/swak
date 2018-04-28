package com.swak.reactivex.web.method;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import com.swak.reactivex.web.annotation.RequestMethod;
import com.swak.reactivex.web.utils.PathMatcherHelper;

/**
 * 匹配器
 * @author lifeng
 */
public class Match implements Comparable<Match> {

	private final String lookupPath;
	private final Set<String> mapping;
	private final RequestMethod method;
	private HandlerMethod handlerMethod;

	public Match(String lookupPath, Set<String> pathMapping, RequestMethod methodMapping) {
		this.lookupPath = lookupPath;
		this.mapping = pathMapping;
		this.method = methodMapping == null ? RequestMethod.ALL : methodMapping;
	}

	/**
	 * 得到所有匹配的路径
	 * @return
	 */
	public Set<String> getMapping() {
		return mapping;
	}

	/**
	 * 得到请求的方法数
	 * @return
	 */
	public RequestMethod getMethod() {
		return method;
	}

	/**
	 * 得到实际的执行方法
	 * @return
	 */
	public HandlerMethod getHandlerMethod() {
		return handlerMethod;
	}

	/**
	 * 必须设置
	 * 
	 * @param handlerMethod
	 */
	public void setHandlerMethod(HandlerMethod handlerMethod) {
		this.handlerMethod = handlerMethod;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("patterns-[");
		for (Iterator<?> iterator = mapping.iterator(); iterator.hasNext();) {
			Object expression = iterator.next();
			builder.append(expression.toString());
			if (iterator.hasNext()) {
				builder.append("||");
			}
		}
		builder.append("]");
		builder.append(",methos-[");
		builder.append(this.method == null ? "ALL" : this.method.name());
		builder.append("]");
		return builder.toString();
	}

	/**
	 * 优先匹配patterns，在匹配method
	 */
	@Override
	public int compareTo(Match other) {
		int compare = this.comparePatterns(other);
		if (compare == 0) {
			compare = this.compareMethod(other);
		}
		return compare;
	}

	// patterns compare
	private int comparePatterns(Match other) {
		Comparator<String> patternComparator = PathMatcherHelper.getPatternComparator(lookupPath);
		Iterator<String> iterator = this.mapping.iterator();
		Iterator<String> iteratorOther = other.mapping.iterator();
		while (iterator.hasNext() && iteratorOther.hasNext()) {
			int result = patternComparator.compare(iterator.next(), iteratorOther.next());
			if (result != 0) {
				return result;
			}
		}
		if (iterator.hasNext()) {
			return -1;
		} else if (iteratorOther.hasNext()) {
			return 1;
		} else {
			return 0;
		}
	}

	// patterns compare
	private int compareMethod(Match other) {
		if (this.method == other.getMethod()) {
			return 0;
		}
		return this.method == RequestMethod.ALL ? 1 : -1;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other != null && getClass() == other.getClass()) {
			Match obj = (Match) other;
			return mapping.equals(obj.getMapping()) && method == obj.getMethod();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.mapping.hashCode() + method.hashCode();
	}
}