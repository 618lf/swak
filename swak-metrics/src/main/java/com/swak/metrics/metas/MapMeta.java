package com.swak.metrics.metas;

import java.util.HashMap;

import com.swak.App;
import com.swak.utils.JsonMapper;

/**
 * 万能的指标收集
 * 
 * @author lifeng
 * @date 2020年12月8日 下午4:01:23
 */
public class MapMeta extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}

	/**
	 * 添加默认的属性
	 * 
	 * @return
	 */
	public static MapMeta of() {
		MapMeta meta = new MapMeta();
		meta.put("server", App.me().getServerSn());
		return meta;
	}
}