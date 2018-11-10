package com.tmt.manage;

import org.eclipse.swt.widgets.Display;

import com.tmt.manage.command.Commands;
import com.tmt.manage.config.Settings;
import com.tmt.manage.widgets.MainFrame;

/**
 * 整个项目
 * 
 * @author lifeng
 */
public class App {
	
	private static App APP = new App();
	public static App getDefault() {
		return APP;
	}
	
	private MainFrame window;
	
	/**
	 * 打开主窗口
	 */
	private void open() {
		window = new MainFrame();
		window.open();
	}
	
	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			
			// 加载配置
			Settings.getSettings();
			
			// 注册默认的命令
			Commands.registers();

			// 启动主界面
			App.getDefault().open();
			
			// 结束
			Display.getDefault().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}