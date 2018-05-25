package com.swak.persistence.incrementer;

import java.io.Serializable;

import com.swak.Constants;
import com.swak.utils.RegexpUtil;

/**
 * 封装各种生成唯一性ID算法的工具类.
 * @author root
 */
public final class IdGen {

	/**
	 * id 生成器
	 */
	private static IdGenerator idGenerator;
	
	/**
	 * 先设置机器号
	 * @param strategy
	 */
	public static void setServerSn(String serverSn) {
		String[] ls = RegexpUtil.newRegexpMatcher("([^-]+)-([\\d]+)-([\\d]+)").getArrayGroups(serverSn);
		if(ls != null && ls.length == 4) {
		   idGenerator = new Long64Generator(Integer.parseInt(ls[2]),Integer.parseInt(ls[3]));
		}
	}
	
	/**
	 * 判断一个id是否有效 全局定义 id 为-1无效
	 * 
	 * @return
	 */
	public static Boolean isInvalidId(Serializable id) {
		if (id == null || "".equals(id) || id.equals(Constants.INVALID_ID)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * 初始的根目录不能删除 根据Id = 0 判断是否是根路劲
	 * 
	 * @param id
	 * @return
	 */
	public static Boolean isRoot(Serializable id) {
		if (id != null && id.equals(Constants.ROOT)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
	 * 生成主键
	 * @return
	 */
	public static <T> T id() {
		return idGenerator.id();
	}
}