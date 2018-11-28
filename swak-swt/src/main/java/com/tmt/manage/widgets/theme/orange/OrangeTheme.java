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
		return SWT.NONE;
	}
	
	/**
	 * 显示工具栏
	 * @return
	 */
	public  boolean showTools() {
		return Boolean.TRUE;
	}
	
	/**
	 * 显示底部
	 * @return
	 */
	public  boolean showFoot() {
		return Boolean.TRUE;
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
	
	/**
	 * secure
	 * 
	 * @return
	 */
	public abstract Action secure();
	
	/**
	 * close
	 * 
	 * @return
	 */
	public abstract Action close();
	
}