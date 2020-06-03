package com.sample.tools.plugin.config;

import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;

import com.sample.tools.plugin.Plugin;

/**
 * 必须继承 Plugin
 * 
 * @author lifeng
 * @date 2020年6月2日 下午5:07:10
 */
public class PluginClassFilter extends AbstractClassTestingTypeFilter {

	@Override
	protected boolean match(ClassMetadata metadata) {
		String[] interfaces = metadata.getInterfaceNames();
		for (String inter : interfaces) {
			if (Plugin.class.getName().equals(inter)) {
				return true;
			}
		}
		return false;
	}
}
