package com.swak.manage;

import org.eclipse.swt.widgets.Display;

import com.swak.manage.config.Settings;
import com.swak.manage.widgets.BaseApp;
import com.swak.manage.widgets.theme.Theme;
import com.swak.manage.widgets.theme.def.DefTheme;

/**
 * 整个项目
 * 
 * @author lifeng
 */
public abstract class App {

	private BaseApp window;

	/**
	 * 打开主窗口
	 */
	protected void open() {

		// 默认的主题
		Theme theme = this.theme();

		// 加载主题
		try {
			String windowClass = Theme.class.getPackage().getName() + "." + theme.path();
			window = (BaseApp) (App.class.getClassLoader().loadClass(windowClass).newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 设置主题
		window.setTheme(theme);

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
		return new DefTheme();
	}
}