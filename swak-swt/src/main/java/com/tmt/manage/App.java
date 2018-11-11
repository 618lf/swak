package com.tmt.manage;

import org.eclipse.swt.widgets.Display;

import com.tmt.manage.config.Settings;
import com.tmt.manage.widgets.MainFrame;

/**
 * 整个项目
 * 
 * @author lifeng
 */
public abstract class App {
	
	private MainFrame window;
	
	/**
	 * 打开主窗口
	 */
	protected void open() {
		window = new MainFrame();
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
		
		//启动主界面
		this.open();
		
		// 结束
		Display.getDefault().dispose();
	}
	
	/**
	 * 注册命令
	 */
	protected abstract void commands();
}