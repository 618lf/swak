package com.swak.reactivex.web;

import java.util.List;
import java.util.Map;

import com.swak.reactivex.HttpServerRequest;
import com.swak.utils.JsonMapper;
import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;

public class WebUtils {

	/**
	 * 简单的获取表格中的数据,约定大于配置，表格的参数名称已items.作为前缀
	 */
	public static final String DEFAULT_ITEMS_PARAM = "items.";

	/**
	 * 导出文件时提交的参数前缀
	 */
	public static final String DEFAULT_EXPORT_PARAM = "export.";

	/**
	 * 获得用户远程地址
	 */
	public static String getRemoteAddr(HttpServerRequest request) {
		String remoteAddr = request.getRequestHeader("X-Real-IP");
		if (StringUtils.isBlank(remoteAddr)) {
			remoteAddr = request.getRequestHeader("X-Forwarded-For");
		} else if (StringUtils.isBlank(remoteAddr)) {
			remoteAddr = request.getRequestHeader("Proxy-Client-IP");
		} else if (StringUtils.isBlank(remoteAddr)) {
			remoteAddr = request.getRequestHeader("WL-Proxy-Client-IP");
		}
		return remoteAddr != null ? remoteAddr : request.getRemoteAddress();
	}

	/**
	 * 得到request 中的数据，并清除两端的空格
	 * 
	 * @param request
	 * @param paramName
	 * @return
	 */
	public static String getCleanParam(HttpServerRequest request, String paramName) {
		List<String> values = request.getParameterValues(paramName);
		return StringUtils.trimToNull(values != null && !values.isEmpty() ? values.get(0) : null);
	}

	/**
	 * 获取表格数据
	 * 
	 * @param request
	 * @param clazz
	 *            表格对应的实体类
	 * @param paramPrefix
	 *            --参数前缀 例如:items.id,items.name
	 * @return
	 */
	public static <T> List<T> fetchItemsFromRequest(HttpServerRequest request, Class<T> clazz, String paramPrefix) {
		List<Map<String, String>> relas = fetchItemsFromRequest(request, paramPrefix);
		// 转换
		List<T> items = JsonMapper.fromJsonToList(JsonMapper.toJson(relas), clazz);
		return items;
	}

	/**
	 * 获取参数
	 * 
	 * @param request
	 * @param paramPrefix
	 * @return
	 */
	public static List<Map<String, String>> fetchItemsFromRequest(HttpServerRequest request, String paramPrefix) {
		String itemsParam = (paramPrefix == null ? DEFAULT_ITEMS_PARAM : paramPrefix);
		Map<String, List<String>> params = Maps.newOrderMap();
		for (String key : request.getParameterMap().keySet()) {
			if (key != null && StringUtils.startsWith(key, itemsParam)) {
				params.put(StringUtils.substringAfter(key, itemsParam), request.getParameterValues(key));
			}
		}
		List<Map<String, String>> relas = Lists.newArrayList();
		if (params != null && !params.isEmpty()) {
			List<String> ids = params.entrySet().iterator().next().getValue();// 取第一个值
			for (int i = 0, j = ids.size(); i < j; i++) {
				Map<String, String> rela = Maps.newHashMap();
				for (String key : params.keySet()) {
					List<String> values = params.get(key);
					if (values == null || values.size() <= i) {
						continue;
					}
					rela.put(key, values.get(i));
				}
				relas.add(rela);
			}
		}
		return relas;
	}
}
