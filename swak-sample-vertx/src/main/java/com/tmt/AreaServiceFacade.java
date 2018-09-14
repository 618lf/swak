package com.tmt;

import java.util.List;

import com.swak.vertx.transport.async.VertxAsync;

/**
 * 区域服务
 * 同步服务接口
 * @author lifeng
 */
@VertxAsync
public interface AreaServiceFacade {

	/**
	 * 所有的区域
	 * 
	 * @return
	 */
	Area get(Long id);
	
	/**
	 * 所有的区域
	 * 
	 * @return
	 */
	List<Area> list();
	
	/**
	 * 保存区域
	 * 
	 * @return
	 */
	void save(Area area);
	
	/**
	 * 删除区域
	 * 
	 * @return
	 */
	void remove(Area area);
}