package com.tmt.manage.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.tmt.manage.widgets.theme.Theme;

/**
 * 基本的frame
 * 
 * @author lifeng
 */
public abstract class BaseApp {

	protected Theme theme;
	protected Shell shell;
	protected Point FULL_POINT = new Point(-1, -1);

	/**
	 * Configure the theme.
	 */
	public void setTheme(Theme theme) {
		this.theme = theme;
	}
	
	/**
	 * open the window
	 */
	public void open() {
		Display display = Display.getDefault();
		shell = newShell();
		createContents();
		configureShell();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create Shell
	 */
	protected Shell newShell() {
		shell = new Shell(Display.getDefault(), getShellStyle());
		
		// 屏幕分辨率
		Rectangle clientArea = Display.getDefault().getPrimaryMonitor().getBounds();
		
		// 根据可视区域自定义窗口大小
		Point point = getShellSize(clientArea);

		// 默认全屏
		if (point == null || point == FULL_POINT) {
			shell.setBounds(Display.getDefault().getPrimaryMonitor().getClientArea());
		}
		
		// 居中显示
		else {
			// 设置窗口大小
			shell.setSize(point.x, point.y);

			// 整个窗口大小
			int width = Display.getDefault().getPrimaryMonitor().getClientArea().width;
			int height = Display.getDefault().getPrimaryMonitor().getClientArea().height;

			int x = shell.getSize().x;
			int y = shell.getSize().y;
			if (x > width) {
				shell.getSize().x = width;
			}
			if (y > height) {
				shell.getSize().y = height;
			}

			// 默认
			shell.setLocation((width - x) / 2, (height - y) / 2);
		}
		return shell;
	}

	/**
	 * close the window
	 */
	public void close() {
		shell.close();
		shell.dispose();
		ResourceManager.dispose();
	}

	/**
	 * get Shell Style, Default
	 */
	protected int getShellStyle() {
		return SWT.SHELL_TRIM;
	}

	/**
	 * Return the initial size of the window.
	 * 
	 * @return
	 */
	protected Point getShellSize(Rectangle clientArea) {
		return FULL_POINT;
	}

	/**
	 * Create contents of the window.
	 */
	protected abstract void createContents();

	/**
	 * Configure the shell.
	 */
	protected abstract void configureShell();
}
