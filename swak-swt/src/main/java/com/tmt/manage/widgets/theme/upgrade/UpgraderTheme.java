package com.tmt.manage.widgets.theme.upgrade;

import java.util.List;

import com.tmt.manage.widgets.theme.Theme;

/**
 * 升级模式
 * 
 * @author lifeng
 */
public abstract class UpgraderTheme implements Theme {

	@Override
	public String name() {
		return "升级";
	}

	@Override
	public String path() {
		return "upgrade.UpgraderApp";
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
	 * close
	 * 
	 * @return
	 */
	public abstract Action close();

	/**
	 * 增量包
	 * 
	 * @return
	 */
	public abstract List<Patch> patchs();

	/**
	 * 备份
	 * 
	 * @return
	 */
	public abstract List<Backup> backups();
	
}
