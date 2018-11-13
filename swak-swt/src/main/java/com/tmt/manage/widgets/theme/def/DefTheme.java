package com.tmt.manage.widgets.theme.def;

import java.util.List;

import com.tmt.manage.widgets.theme.Theme;

/**
 * 默认的主题
 * 
 * @author lifeng
 */
public class DefTheme implements Theme {

	/**
	 * 名称
	 */
	@Override
	public String name() {
		return "def";
	}

	/**
	 * app 的相对路径
	 */
	@Override
	public String path() {
		return "def.DefApp";
	}

	/**
	 * 支持的 actions
	 */
	@Override
	public List<Action> actions() {
		return null;
	}
}