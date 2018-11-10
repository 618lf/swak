package com.tmt.manage.command.impl;

import org.eclipse.swt.widgets.Display;

import com.tmt.manage.command.Command;
import com.tmt.manage.command.Commands.Sign;
import com.tmt.manage.command.Commands.Signal;
import com.tmt.manage.widgets.BrowserFrame;

/**
 * 打开界面
 * 
 * @author lifeng
 */
public class OpenCommand implements Command {

	@Override
	public void exec() {
		new Thread(() -> {
			this.openBrowser();
		}).start();
	}

	private void openBrowser() {
		Display.getDefault().asyncExec(() -> {
			this.sendSignal(Signal.newSignal(Sign.opened));
			new BrowserFrame().open();
		});
	}
}
