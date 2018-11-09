package com.tmt.manage.command.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmt.manage.App;
import com.tmt.manage.command.Command;
import com.tmt.manage.config.Settings;

/**
 * 开始按钮
 * 
 * @author lifeng
 */
public class StartCommand implements Command {
	private Logger Logger = LoggerFactory.getLogger(App.class);

	@Override
	public void exec() {
		Logger.info("服务器启动中");
		openBrowser();
	}

	/**
	 * 这是一种方式，最简单的默认方式， 99%的支持ele，有一点样式不支持
	 */
	private void openBrowser() {
		final Shell shell = new Shell(Display.getDefault(), SWT.SHELL_TRIM);
		shell.setText(Settings.getSettings().getServerName());
		shell.setBounds(Display.getDefault().getPrimaryMonitor().getBounds());
		FillLayout layout = new FillLayout();
		shell.setLayout(layout);
		Browser browser = new Browser(shell, SWT.NONE);
		browser.setBounds(Display.getDefault().getPrimaryMonitor().getBounds());
		browser.addTitleListener(new TitleListener() {
			public void changed(TitleEvent event) {
				shell.setText(event.title);
			}
		});
		browser.setUrl(Settings.getSettings().getServerPage());
		shell.open();
	}
}
