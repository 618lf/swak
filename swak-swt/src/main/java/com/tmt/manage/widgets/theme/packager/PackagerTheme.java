package com.tmt.manage.widgets.theme.packager;

import com.tmt.manage.widgets.theme.Theme;

/**
 * 打开打包工具
 * @author lifeng
 */
public abstract class PackagerTheme implements Theme{

	@Override
	public String name() {
		return "Packager";
	}

	@Override
	public String path() {
		return "packager.PackagerApp";
	}
	
	/**
	 * logo
	 * 
	 * @return
	 */
	public abstract Action logo();
}
