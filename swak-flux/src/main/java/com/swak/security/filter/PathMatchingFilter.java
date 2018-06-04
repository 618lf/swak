package com.swak.security.filter;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.PathMatcher;

import com.swak.reactivex.handler.WebFilter;
import com.swak.reactivex.transport.http.HttpServerRequest;
import com.swak.reactivex.transport.http.HttpServerResponse;
import com.swak.utils.StringUtils;

import reactor.core.publisher.Mono;

public abstract class PathMatchingFilter extends AdviceFilter implements PathConfigProcessor {

	/**
	 * 配置信息
	 */
	protected Map<String, Object> appliedPaths;

	/**
	 * url 匹配器
	 */
	private PathMatcher pathMatcher;

	/**
	 * 设置模式匹配器
	 * 
	 * @param pathMatcher
	 */
	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

	/**
	 * 记录 请求设置中每个请求对应的filter
	 */
	@Override
	public WebFilter processPathConfig(String path, String config) {
		String[] values = null;
		if (config != null) {
			values = StringUtils.split(config);
		}
		if (appliedPaths == null) {
			appliedPaths = new LinkedHashMap<String, Object>();
		}
		this.appliedPaths.put(path, values);
		return this;
	}
	
	/**
	 * 验证请求地址是否能在配置中找到， 一定会有一个被找到
	 * @param path
	 * @param request
	 * @return
	 */
	protected boolean pathsMatch(String path, HttpServerRequest request) {
        String requestURI = request.getRequestURI();
        return pathMatcher.match(path, requestURI);
    }
	
	/**
	 * 找到需要执行的配置
	 */
	protected Mono<Boolean> preHandle(HttpServerRequest request, HttpServerResponse response) {
        Object config = null;
        if (!(this.appliedPaths == null || this.appliedPaths.isEmpty())) {
        	for (String path : this.appliedPaths.keySet()) {
                if (pathsMatch(path, request)) {
                	config = this.appliedPaths.get(path);
                    break;
                }
            }
        }
        return onPreHandle(request, response, config);
    }
	
	/**
	 * 将访问控制的合并到这里
	 * @param request
	 * @param response
	 * @param mappedValue
	 * @return
	 * @throws Exception
	 */
	protected abstract Mono<Boolean> onPreHandle(HttpServerRequest request, HttpServerResponse response, Object mappedValue);
}