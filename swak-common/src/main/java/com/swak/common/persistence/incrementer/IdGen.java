package com.swak.common.persistence.incrementer;

import java.io.Serializable;

import org.springframework.beans.factory.InitializingBean;

import com.swak.common.utils.RegexpUtil;
import com.swak.common.utils.SpringContextHolder;

/**
 * 封装各种生成唯一性ID算法的工具类.
 * @author root
 */
public final class IdGen implements IdGenerator, InitializingBean{

	private static IdGen instance = null;
	
	private static IdGen getInstance() {
		if (instance == null) {
			instance = SpringContextHolder.getBean(IdGen.class);
		}
		return instance;
	}

	/**
	 * id 生成器
	 */
	private IdGenerator idGenerator;
	
	/**
	 * 无效的id
	 */
	public static final long INVALID_ID = -1;

	/**
	 * 默认的RootID，一般作为默认值
	 */
	public static final long ROOT_ID = 0;

	/**
	 * 代理主键生成器
	 */
	public Serializable generateId() {
		return idGenerator.generateId();
	}
	
	/**
	 * 先设置机器号
	 * @param strategy
	 */
	public void setServerSn(String serverSn) {
		String[] ls = RegexpUtil.newRegexpMatcher("([^-]+)-([\\d]+)-([\\d]+)").getArrayGroups(serverSn);
		if(ls != null && ls.length == 4) {
		   idGenerator = new Long64Generator(Integer.parseInt(ls[2]),Integer.parseInt(ls[3]));
		}
	}
	
	/**
	 * 初始化验证
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (idGenerator == null) {
			idGenerator = new UUIdGenerator();
		}
	}
	
	/**
	 * 判断一个id是否有效 全局定义 id 为-1无效
	 * 
	 * @return
	 */
	public static Boolean isInvalidId(Serializable id) {
		if (id == null || "".equals(id) || id.equals(IdGen.INVALID_ID)) {
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
		if (id != null && id.equals(IdGen.ROOT_ID)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
	 * 生成主键
	 * @return
	 */
	public static Serializable key() {
		return getInstance().generateId();
	}
	
	/**
	 * String 格式的
	 * @return
	 */
	public static String stringKey() {
		return getInstance().generateId().toString();
	}
}