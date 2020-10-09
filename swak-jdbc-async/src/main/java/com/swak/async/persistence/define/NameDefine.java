package com.swak.async.persistence.define;

/**
 * 名称定义
 * 
 * @author lifeng
 * @date 2020年10月8日 下午6:55:07
 */
public abstract class NameDefine {

	/**
	 * 必须是唯一的名称
	 */
	public String name;

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof String) {
			return name.equals((String) obj);
		} else if (obj != null && obj instanceof NameDefine) {
			return name.equals(((NameDefine) obj).name);
		}
		return false;
	}

	@Override
	public String toString() {
		return name.toString();
	}
}
