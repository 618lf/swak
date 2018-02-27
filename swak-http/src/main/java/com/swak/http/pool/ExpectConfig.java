package com.swak.http.pool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.swak.common.utils.StringUtils;

/**
 * 如何设置参数： https://www.cnblogs.com/waytobestcoder/p/5323130.html
 * 
 * 针对线程池的特点， 只需要根据业务的特点，设置业务中的基本情况自动设置线程池的相关参数 : 
 * [EXP]每秒任务数:任务执行平均时间:最大的等待时间
 * 
 * @author lifeng
 */
public abstract class ExpectConfig implements ConfigableThreadPool {

	/**
	 * 创建期望配置的线程池
	 * 
	 * @param name
	 * @param configs
	 */
	public ThreadPoolExecutor expectPool(String name, String configs) {

		/**
		 * 必须符合[EXP]
		 */
		if (!StringUtils.startsWithIgnoreCase(configs, "[EXP]")) {
			return null;
		}
		configs = StringUtils.removeStart(configs, "[EXP]");
		
		/**
		 * 必须符合[EXP]每秒任务数:任务执行平均时间:最大的等待时间的格式
		 */
		String[] _configs = configs.split(":");
		if (_configs == null || _configs.length != 3) {
			return null;
		}

		/**
		 * 解析出识别的参数
		 */
		int tasks = Integer.parseInt(_configs[0]);
		int taskcost = Integer.parseInt(_configs[1]);
		int responsetime = Integer.parseInt(_configs[2]);

		/**
		 * 计算出适合业务的线程池的大小
		 * queueCapacity 尽量设置的小一点才能使用的上 maxPoolSize
		 */
		int corePoolSize = tasks / (1000 / taskcost) * 4 / 5;
		int queueCapacity = responsetime / taskcost * corePoolSize;
		int maxPoolSize = corePoolSize * 2;
		
		/**
		 * 创建线程池
		 */
		return new ThreadPoolExecutor(corePoolSize, maxPoolSize, queueCapacity,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}
}