package com.tmt.manage.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import com.tmt.manage.widgets.theme.Theme;

/**
 * 基本的frame
 * 
 * @author lifeng
 */
public abstract class BaseApp {

	protected Theme theme;
	protected Shell shell;
	protected Tray tray;
	protected Point FULL_POINT = new Point(-1, -1);
	protected Rectangle bounds = null;
	protected Point location = null;
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
		newTray();
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
		Rectangle windowArea = Display.getDefault().getPrimaryMonitor().getBounds();
		Rectangle clientArea = Display.getDefault().getPrimaryMonitor().getClientArea();
		
		// 根据可视区域自定义窗口大小
		Point point = getShellSize(windowArea);

		// 默认全屏
		if (point == null || point == FULL_POINT) {
			shell.setBounds(clientArea);
		}
		
		// 居中显示
		else {
			// 设置窗口大小
			shell.setSize(point.x, point.y);

			// 整个窗口大小
			int width = clientArea.width;
			int height = clientArea.height;

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
	 * 系统托盘
	 * 
	 * @return
	 */
	protected Tray newTray() {
		tray = Display.getDefault().getSystemTray();  
        TrayItem trayItem = new TrayItem(tray, SWT.NONE);  
        trayItem.setVisible(false);  
        trayItem.setToolTipText(shell.getText());
        trayItem.setImage(shell.getImage());
        trayItem.addSelectionListener(new SelectionAdapter() {  
            public void widgetSelected(SelectionEvent e) {  
            	min();
            }  
        });
        return tray;
	}
	
	/**
	 * 最小化到托盘
	 */
	protected void min() {
		try {
			shell.setVisible(!shell.isVisible());  
	        tray.getItem(0).setVisible(!shell.isVisible());  
	        if (shell.getVisible()) {  
	            shell.setActive();  
	        }
		}catch (Exception e) {
			e.printStackTrace();
		}
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
	 * 全屏幕显示
	 */
	public void resize() {
		if (bounds == null) {
			bounds = shell.getBounds();
			location = shell.getLocation();
			shell.setBounds(Display.getDefault().getPrimaryMonitor().getClientArea());
		} else {
			shell.setBounds(bounds);
			shell.setLocation(location);
			bounds = null;
			location = null;
		}
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
