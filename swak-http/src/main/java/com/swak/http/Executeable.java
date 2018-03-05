package com.swak.http;

import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

import com.swak.common.utils.Maps;
import com.swak.common.utils.StringUtils;

/**
 * 任务执行器 984
 * @author lifeng
 */
public interface Executeable {

	/**
	 * 执行
	 * @param run
	 */
	void onExecute(String lookupPath, Runnable run); 
	
	/**
	 * url 定义的配置 url 配置是有顺序的
	 * 
	 * @param definitions
	 */
	default void setPoolDefinitions(String definitions) {
		Map<String, String> poolDefinitions = Maps.newOrderMap();
		Scanner scanner = new Scanner(definitions);
		while (scanner.hasNextLine()) {
			String line = StringUtils.clean(scanner.nextLine());
			if (!StringUtils.hasText(line)) {
				continue;
			}
			String[] parts = StringUtils.split(line, '=');
			if (!(parts != null && parts.length == 2)) {
				continue;
			}
			String path = StringUtils.clean(parts[0]);
			String configs = StringUtils.clean(parts[1]);
			if (!(StringUtils.hasText(path) && StringUtils.hasText(configs))) {
				continue;
			}
			poolDefinitions.put(path, configs);
		}
		IOUtils.closeQuietly(scanner);

		// 构建线程池
		poolDefinitions.keySet().stream().forEach(s -> {
			String configs = poolDefinitions.get(s);
			this.createPool(s, configs);
		});
	}

	/**
	 * 创建池化
	 * @param name
	 * @param configs
	 */
	default void createPool(String name, String configs) {}

	/**
	 * 得到配置项目的值
	 * @param configs
	 * @param index
	 * @param def
	 * @return
	 */
	default int getDefault(String[] configs, int index, int def) {
		if (configs == null || index >= configs.length) {
			return def;
		}
		return Integer.parseInt(configs[index]);
	}
}
