package com.swak.incrementer;

import java.io.Serializable;

import com.swak.Constants;
import com.swak.utils.RegexUtil;

/**
 * 封装各种生成唯一性ID算法的工具类.
 *
 * @author: lifeng
 * @date: 2020/3/29 11:32
 */
public final class IdGen {

	/**
	 * id 生成器
	 */
	private static IdGenerator idGenerator;

	/**
	 * 先设置机器号
	 *
	 * @param serverSn 机器号
	 */
	public static void setServerSn(String serverSn) {
		String[] ls = RegexUtil.newRegexpMatcher("([^-]+)-([\\d]+)-([\\d]+)").getArrayGroups(serverSn);
		int serverSnLength = 4;
		if (ls != null && ls.length == serverSnLength) {
			idGenerator = new Long64Generator(Integer.parseInt(ls[2]), Integer.parseInt(ls[3]));
		}
	}

	/**
	 * 判断一个id是否有效 全局定义 id 为-1无效
	 *
	 * @param id id值
	 * @return 是否有效
	 */
	public static Boolean isInvalidId(Serializable id) {
		if (id == null || "".equals(id) || id.equals(Constants.INVALID_ID)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * 判断一个id是否有效 全局定义 id 为-1无效
	 *
	 * @param id id值
	 * @return 是否有效
	 */
	public static <T> Boolean isInvalidId(T id) {
		if (id == null || "".equals(id)) {
			return Boolean.TRUE;
		} else if (id instanceof Long && id.equals(Constants.INVALID_ID)) {
			return Boolean.TRUE;
		} else if (id instanceof String && Constants.STR_INVALID_ID.equals(id)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * 初始的根目录不能删除 根据Id = 0 判断是否是根路劲
	 *
	 * @param id id值
	 * @return 是否根
	 */
	public static Boolean isRoot(Serializable id) {
		if (id != null && id.equals(Constants.ROOT)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * 生成主键
	 *
	 * @return 主键
	 */
	public static <T> T id() {
		return idGenerator.id();
	}
}