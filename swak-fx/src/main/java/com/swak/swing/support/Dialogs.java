package com.swak.swing.support;

import javax.swing.JOptionPane;

/**
 * 弹出框
 * 
 * @author lifeng
 * @date 2020年5月22日 下午4:48:31
 */
public class Dialogs {

	/**
	 * 确认窗体
	 * 
	 * @param title
	 * @param message
	 * @return 选择的按钮
	 */
	public static int confirm(String title, String message) {
		return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
	}

	/**
	 * 显示错误消息
	 * 
	 * @param title
	 * @param message
	 */
	public static void error(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * 提示信息
	 * 
	 * @param title
	 * @param message
	 */
	public static void info(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * 警告
	 * 
	 * @param title
	 * @param message
	 */
	public static void warn(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
	}
}
