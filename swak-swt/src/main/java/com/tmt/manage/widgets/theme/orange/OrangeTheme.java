package com.tmt.manage.widgets.theme.orange;

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