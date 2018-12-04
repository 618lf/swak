package com.swak.manage.operation;

/**
 * 操作
 * 
 * @author lifeng
 */
public interface Ops {
	
	/**
	 * 验证的盐值
	 */
	String SALT = "SWAK";
			
	/**
	 * 执行操作
	 */
	void doOps(OpsFile file);
	
	/**
	 * 下一个处理器
	 * @param next
	 */
	void next(Ops next);
}