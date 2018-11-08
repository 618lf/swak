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
	
	public static App APP = new App();
	
	private MainFrame window;
	
	/**
	 * 打开主窗口
	 */
	private void openMain() {
		window = new MainFrame();
		window.setBlockOnOpen(true);
		window.open();
	}
	
	/**
	 * 打印日志
	 * 
	 * @param text
	 */
	public void log(String text) {
		Display.getDefault().asyncExec(() ->{
			window.log(text);
		});
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
			APP.openMain();
			
			// 结束
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}