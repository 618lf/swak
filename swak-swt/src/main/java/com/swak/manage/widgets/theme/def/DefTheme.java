package com.swak.manage.widgets.theme.def;

import java.util.List;

import com.swak.manage.widgets.theme.AbsTheme;

/**
 * 默认的主题
 * 
 * @author lifeng
 */
public class DefTheme extends AbsTheme {

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