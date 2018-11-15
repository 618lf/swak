package com.tmt.manage.widgets.theme.orange;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import com.tmt.manage.widgets.theme.Theme;

/**
 * 支持的主题
 * 
 * @author lifeng
 */
public abstract class OrangeTheme implements Theme {

	@Override
	public String name() {
		return "Orange";
	}

	@Override
	public String path() {
		return "orange.OrangeApp";
	}

	/**
	 * 窗口大小
	 * 
	 * @return
	 */
	public abstract Point getShellSize(Rectangle clientArea);

	/**
	 * 窗口大小
	 * 
	 * @return
	 */
	public int getShellStyle() {
		return SWT.NONE ;
	}

	/**
	 * background
	 * 
	 * @return
	 */
	public abstract Action background();

	/**
	 * logo
	 * 
	 * @return
	 */
	public abstract Action logo();
}