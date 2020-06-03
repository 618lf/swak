package com.sample.tools;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sample.tools.plugin.Plugin;
import com.sample.tools.plugin.PluginLoader;
import com.sample.tools.plugin.config.PluginScan;

/**
 * 项目配置
 * 
 * @author lifeng
 */
@Configuration
@PluginScan("com.sample.tools.plugin.plugins")
public class AppConfiguration {

	/**
	 * 插件加载器
	 * 
	 * @return
	 */
	@Bean
	public PluginLoader pluginLoader(ObjectProvider<List<Plugin>> plugins) {
		List<Plugin> _plugins = plugins.getIfAvailable();
		return new PluginLoader().addPlugins(_plugins);
	}
}