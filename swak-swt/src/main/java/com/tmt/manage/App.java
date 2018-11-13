package com.tmt.manage;

import org.eclipse.swt.widgets.Display;

import com.tmt.manage.config.Settings;
import com.tmt.manage.widgets.BaseFrame;

/**
 * 整个项目
 * 
 * @author lifeng
 */
public abstract class App {

	private BaseFrame window;

	/**
	 * 打开主窗口
	 */
	protected void open() {

		// 默认的主题
		Theme theme = this.theme();

		// 加载主题
		try {
			String windowClass = "com.tmt.manage.widgets.theme." + theme.getPath();
			window = (BaseFrame) (App.class.getClassLoader().loadClass(windowClass).newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 打开界面
		window.open();
	}

	/**
	 * 启动服务
	 * 
	 * @param args
	 */
	public void run(String args[]) {

		// 加载配置
		Settings.intSettings(args);

		// 注册命令
		commands();

		// 启动主界面
		this.open();

		// 结束
		Display.getDefault().dispose();
	}

	/**
	 * 注册命令
	 */
	protected abstract void commands();

	/**
	 * 显示默认的主题
	 * 
	 * @return
	 */
	protected Theme theme() {
		return Theme.Def;
	}

	/**
	 * 支持的主题
	 * 
	 * @author lifeng
	 */
	public static enum Theme {
		
		Def("DefApp"), Orange("orange.OrangeApp");

		private String path;

		private Theme(String path) {
			this.path = path;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}
	}
}