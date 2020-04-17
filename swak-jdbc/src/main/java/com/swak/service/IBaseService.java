package com.swak.service;

import java.io.Serializable;

import com.swak.entity.IdEntity;
import com.swak.entity.Page;
import com.swak.entity.Parameters;

/**
 * 服务接口
 * 
 * @author lifeng
 * @date 2020年4月17日 下午5:03:58
 */
public interface IBaseService<T extends IdEntity<PK>, PK extends Serializable> {

	/**
	 * 根据 ID 获取实体
	 * 
	 * @param id 主键
	 * @return 实体
	 */
	T get(PK id);

	/**
	 * 按条件分页查询
	 * 
	 * @param entity 查询的实体
	 * @param param  分页参数
	 * @return 分页数据
	 */
	Page page(T entity, Parameters param);

	/**
	 * 保存实体
	 * 
	 * @param entity 实体对象
	 * @return 分页数据
	 */
	T save(T entity);

	/**
	 * 删除的实体
	 * 
	 * @param entity 实体
	 * @return 删除的个数（用来判断是否删除成功）
	 */
	Integer delete(T entity);
}
