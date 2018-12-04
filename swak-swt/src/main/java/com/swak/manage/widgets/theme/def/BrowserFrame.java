package com.swak.manage.widgets.theme.def;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.layout.FillLayout;

import com.swak.manage.command.Commands;
import com.swak.manage.command.Commands.Sign;
import com.swak.manage.command.Commands.Signal;
import com.swak.manage.config.Settings;
import com.swak.manage.widgets.BaseApp;

/**
 * 浏览器窗口
 * 
 * @author lifeng
 */
public class BrowserFrame extends BaseApp{

	private Browser browser;
	
	@Override
	protected int getShellStyle() {
		return SWT.CLOSE;
	}

	@Override
	protected void createContents() {
		shell.setLayout(new FillLayout());
		browser = new Browser(shell, SWT.NONE);
		browser.addTitleListener(new TitleListener() {
			public void changed(TitleEvent event) {
				shell.setText(event.title);
			}
		});
		browser.setUrl(Settings.me().getServer().getIndex());
	}

	@Override
	protected void configureShell() {
		shell.addDisposeListener(e -> {
			Commands.sendSignal(Signal.newSignal(Sign.browser_closed));
		});
	}
}
