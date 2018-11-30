package com.tmt.manage.operation;

/**
 * 抽象的操作
 * 
 * @author lifeng
 */
public abstract class AbsOps implements Ops {

	Ops next;

	/**
	 * 一步一步往下执行
	 */
	@Override
	public void doOps(OpsFile file) {
		
		// 执行本次任务
		try {
			doInnerOps(file);
		} catch (Exception e) {
			file.error(e.getMessage());
		}

		// 执行下一个任务
		if (next != null) {
			next.doOps(file);
		}
	}

	/**
	 * 设置下一个执行器
	 * 
	 * @param next
	 */
	public void next(Ops next) {
		this.next = next;
	}

	/**
	 * 子类需要完成的
	 * 
	 * @param file
	 * @return
	 */
	protected abstract void doInnerOps(OpsFile file) throws Exception;
}