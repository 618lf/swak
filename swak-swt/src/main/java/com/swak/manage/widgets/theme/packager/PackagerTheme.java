package com.swak.manage.widgets.theme.packager;

import com.swak.manage.widgets.theme.AbsTheme;

/**
 * 打开打包工具
 * @author lifeng
 */
public abstract class PackagerTheme extends AbsTheme {

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
