package com.swak.reactivex.threads;

/**
 * 顺序执行
 * 
 * @author lifeng
 * @date 2020年8月20日 下午9:52:03
 */
public class OrderContext implements Context {

	/**
	 * 任务执行器
	 */
	Context context;

	/**
	 * 顺序的队列
	 */
	TaskQueue orderTask = new TaskQueue();

	/**
	 * 创建同步执行器
	 * 
	 * @param context 上下文
	 */
	public OrderContext(Context context) {
		this.context = context;
	}

	/**
	 * 添加指标监控
	 */
	@Override
	public void execute(Runnable command) {
		orderTask.execute(command, context);
	}
}