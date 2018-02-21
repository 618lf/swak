package com.swak.http.pool;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.swak.http.metric.Reportable;

/**
 * 可配置的线程池
 * @author lifeng
 */
public interface ConfigableThreadPool extends Reportable {
	
	/**
	 * 得到指定的线程
	 * @param name
	 * @return
	 */
	List<String> pools();
	
	/**
	 * 得到一个pool
	 * 
	 * @param request
	 * @return
	 */
	ThreadPoolExecutor getPool(String path);
	
	/**
	 * 执行
	 * @param run
	 */
	void onExecute(String lookupPath, Runnable run); 
	
}