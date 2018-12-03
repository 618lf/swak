package com.tmt.manage.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

/**
 * 一些弹出提示
 * 
 * @author lifeng
 */
public class Message {

	private final String message;
	private final int type;

	private Message(String message, int type) {
		this.message = message;
		this.type = type;
	}

	public void open() {
		MessageBox messageBox = new MessageBox(Display.getDefault().getShells()[0], this.getStyle());
		messageBox.setText("操作成功");
		messageBox.setMessage(message);
		messageBox.open();
	}

	private int getStyle() {
		if (type == 1) {
			return SWT.APPLICATION_MODAL | SWT.YES;
		}
		return SWT.APPLICATION_MODAL;
	}

	/**
	 * 显示成功
	 * 
	 * @param message
	 */
	public static void success(String message) {
		new Message(message, 1).open();
	}
}
