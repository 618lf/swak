package com.sample.tools.plugin;

import java.util.List;

import com.swak.utils.Lists;

/**
 * 插件加载
 * 
 * @author lifeng
 * @date 2020年6月2日 下午4:34:26
 */
public class PluginLoader {

	private static PluginLoader ME = null;

	private List<Plugin> plugins;

	public PluginLoader() {
		ME = this;
	}

	/**
	 * 添加插件
	 * 
	 * @param plugins
	 * @return
	 */
	public PluginLoader addPlugins(List<Plugin> plugins) {
		if (this.plugins == null) {
			this.plugins = Lists.newArrayList();
		}
		this.plugins.addAll(plugins);
		return this;
	}

	/**
	 * 返回插件
	 * 
	 * @return
	 */
	public List<Plugin> getPlugins() {
		return plugins;
	}

	/**
	 * 返回当前对象
	 * 
	 * @return
	 */
	public static PluginLoader me() {
		assert ME != null;
		return ME;
	}
}