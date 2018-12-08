package com.swak.manage.widgets.theme.orange;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * 自适应的浏览器
 * 
 * @author lifeng
 */
public class OrangeBrowser extends Composite {

	private Browser browser;

	/**
	 * 初始化浏览器
	 * 
	 * @param parent
	 * @param style
	 */
	public OrangeBrowser(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new FillLayout());
		this.loadBrowser();
	}

	//
	private void loadBrowser() {
		if (isLinux()) {
			browser = new Browser(this, SWT.WEBKIT);
		} else {
			browser = new Browser(this, SWT.NONE);
		}
	}

	private boolean isLinux() {
		String OS = System.getProperty("os.name").toLowerCase();
		return OS.indexOf("linux") >= 0;
	}

	/**
	 * 显示的地址
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		browser.setUrl(url);
	}
}
