package com.swak.reactivex.web.statics;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.swak.Constants;
import com.swak.reactivex.transport.http.HttpConst;
import com.swak.reactivex.transport.http.multipart.FileProps;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.web.Handler;
import com.swak.utils.Sets;
import com.swak.utils.StringUtils;

import reactor.core.publisher.Mono;

/**
 * 静态资源处理
 * 
 * @see ResourceWebHandler
 * 
 * @author lifeng
 */
public class StaticHandler implements Handler, InitializingBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	// 目前只支持这三个目录,可以是jar或者jar外
	private ResourceLoader resourceLoader;
	private PathResourceResolver pathResourceResolver;
	private final Set<String> locationsValues = Sets.newHashSet("classpath:/static/", "classpath:/files/",
			"classpath:/META-INF/resources/");
	private final Set<Resource> locations = Sets.newHashSet();
	private long HTTP_CACHE_SECONDS = 86400 * 30; // 30 day
	private String INDEX_PAGE = "index.html";

	public StaticHandler(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	/**
	 * index
	 */
	public void setIndexPage(String indexPage) {
		this.INDEX_PAGE = indexPage;
	}

	/**
	 * cache
	 */
	public void setMaxAgeSeconds(long maxAgeSeconds) {
		this.HTTP_CACHE_SECONDS = maxAgeSeconds;
	}

	/**
	 * 设置支持的资源
	 */
	public void setLocationValues(List<String> locationValues) {
		this.locationsValues.clear();
		this.locationsValues.addAll(locationValues);
	}

	/**
	 * 使用 NIO 写文件
	 * 
	 * @param request
	 * @return
	 */
	public Mono<Void> handle(HttpServerRequest request) {

		// 查找的路径
		String requestPath = request.getRequestURL();

		// 设置首页
		if (StringUtils.isBlank(requestPath) || requestPath.equals(Constants.URL_PATH_SEPARATE)) {
			requestPath = INDEX_PAGE;
		}

		// 获取资源
		Resource resource = pathResourceResolver.resolveResource(requestPath, locations);
		
		// 404 NOT FOUND
		if (resource == null) {
			return Mono.error(HttpConst.NOT_FOUND_EXCEPTION);
		}
		
		// 主要是创建 FileProps，并链接到执行链上
		return FileProps.props(resource).onErrorResume(e -> {
			// 404 NOT FOUND
			return Mono.error(HttpConst.NOT_FOUND_EXCEPTION);
		}).flatMap(fileProps -> {

			// 304 NotModified
			if (HTTP_CACHE_SECONDS > 0 && request.ifModified(fileProps)) {
				return Mono.empty();
			}

			// 输出头部,缓存
			request.getResponse().cache(HTTP_CACHE_SECONDS, fileProps.lastModifiedTime());

			// 输出内容
			request.getResponse().buffer(fileProps);

			// 返回空就OK
			return Mono.empty();
		});
	}

	/**
	 * 初始化
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

		// 资源加载
		pathResourceResolver = new PathResourceResolver();

		// 需要加载的资源
		for (String location : this.locationsValues) {
			Resource resource = this.resourceLoader.getResource(location);
			this.locations.add(resource);
		}
	}

	/**
	 * 清空缓存
	 */
	public void close() {

	}
}