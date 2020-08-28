package com.swak.vertx.protocol.im;

import java.util.List;
import java.util.regex.Pattern;

import com.swak.annotation.ImOps;

import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Exclude;

/**
 * 条件
 * 
 * @author lifeng
 * @date 2020年8月28日 上午12:54:22
 */
@EqualsAndHashCode
public class ImPredicate {

	/**
	 * 匹配的模式
	 */
	@Exclude
	Pattern pattern;

	/**
	 * 匹配的分组
	 */
	@Exclude
	List<String> groups;

	/**
	 * 请求的地址： 如果没有则为 "/"
	 */
	String path;

	/**
	 * 必须有
	 */
	ImOps ops;

	public ImPredicate(String path, ImOps ops) {
		this.ops = ops;
		this.path = path;
	}

	/**
	 * 设置匹配模式
	 * 
	 * @param pattern
	 * @return
	 */
	public ImPredicate setPattern(Pattern pattern) {
		this.pattern = pattern;
		return this;
	}

	/**
	 * 查找匹配的
	 * 
	 * @param other
	 * @return
	 */
	public boolean patternMatch(ImPredicate other) {

		// 不满足Ops 的匹配
		if (ops != null && ops != ImOps.All && ops != other.ops) {
			return false;
		}

		// 不满足匹配
		if (pattern.matcher(other.path) == null) {
			return false;
		}

		// 满足条件
		return true;
	}

	/**
	 * 查找匹配的
	 * 
	 * @param other
	 * @return
	 */
	public boolean pathMatch(ImPredicate other) {

		// 不满足Ops 的匹配
		if (ops != null && ops != ImOps.All && ops != other.ops) {
			return false;
		}

		// 路径要全匹配
		return this.path.equals(other.path);
	}
}
