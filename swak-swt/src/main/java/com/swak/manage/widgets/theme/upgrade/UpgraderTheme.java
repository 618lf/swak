package com.swak.manage.widgets.theme.upgrade;

import java.util.List;

import com.swak.manage.widgets.theme.AbsTheme;

/**
 * 升级模式
 * 
 * @author lifeng
 */
public abstract class UpgraderTheme extends AbsTheme {

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
	
	/**
	 * 日志
	 * 
	 * @return
	 */
	public abstract List<Log> logs();
	
}