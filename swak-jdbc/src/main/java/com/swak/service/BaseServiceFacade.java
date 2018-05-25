package com.swak.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.swak.persistence.Page;
import com.swak.persistence.PageParameters;
import com.swak.persistence.QueryCondition;

/**
 * 基础服务
 * @author root
 *
 * @param <T>
 * @param <PK>
 */
public interface BaseServiceFacade<T, PK>{
	
	/**
	 * 获取单个值
	 * @param id
	 * @return
	 */
	public CompletableFuture<T> get(final PK id);
	
    /**
     * 条件查询
     * @param qc
     * @return
     */
    public CompletableFuture<List<T>> queryByCondition(QueryCondition qc);
    
    /**
     * 分页查询
     * @param qc
     * @param param
     * @return
     */
    public CompletableFuture<Page> queryForPage(QueryCondition qc, PageParameters param);
    
    /**
     * 条件查询个数
     * @param qc
     * @return
     */
    public CompletableFuture<Integer> countByCondition(QueryCondition qc);
    
    /**
     * 查询指定的页数
     * @param qc
     * @param begin
     * @param end
     * @return
     */
    public CompletableFuture<List<T>> queryForLimitList(QueryCondition qc, int end);
    
    /**
     * 是否存在
     * @param id
     * @return
     */
    public CompletableFuture<Boolean> exists(PK id);
    
    /**
     * 保存数据
     * @param t
     * @return
     */
    public CompletableFuture<T> save(T entity);
    
    /**
     * 保存数据
     * @param t
     * @return
     */
    public CompletableFuture<Void> delete(List<T> entities);
}
