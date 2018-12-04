package com.swak.manage.command.impl;

import org.eclipse.swt.widgets.Display;

import com.swak.manage.command.Command;
import com.swak.manage.command.Commands.Sign;
import com.swak.manage.command.Commands.Signal;
import com.swak.manage.widgets.theme.def.BrowserFrame;

/**
 * 打开界面
 * 
 * @author lifeng
 */
public class BrowserOpenedCommand implements Command {

	@Override
	public void exec() {
		new Thread(() -> {
			this.openBrowser();
		}).start();
	}

	private void openBrowser() {
		Display.getDefault().asyncExec(() -> {
			this.sendSignal(Signal.newSignal(Sign.browser_opened));
			new BrowserFrame().open();
		});
	}
}
