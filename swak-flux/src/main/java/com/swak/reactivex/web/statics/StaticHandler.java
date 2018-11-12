package com.swak.reactivex.web.statics;

import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.swak.Constants;
import com.swak.exception.BaseRuntimeException;
import com.swak.reactivex.transport.http.HttpConst;
import com.swak.reactivex.transport.http.multipart.FileProps;
import com.swak.reactivex.transport.http.multipart.MimeType;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.reactivex.web.Handler;
import com.swak.utils.Sets;
import com.swak.utils.StringUtils;

/**
 * 静态资源处理
 * 
 * @author lifeng
 */
public class StaticHandler implements Handler, InitializingBean {

	// 目前只支持这三个目录,可以是jar或者jar外
	private ResourceLoader resourceLoader;
	private PathResourceResolver pathResourceResolver;
	private final Set<String> locationsValues = Sets.newHashSet("/static/", "/files/", "/META-INF/resources/");
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
	 * 静态资源直接输出就行了
	 */
	public void handle(HttpServerRequest request) {

		// 查找的路径
		String requestPath = request.getRequestURL();
		
		// 设置首页
	    if (StringUtils.isBlank(requestPath) || requestPath.equals(Constants.URL_PATH_SEPARATE)) {
	    	requestPath = INDEX_PAGE;
	    }

		// 处理请求
		try {

			// 获取资源
			Resource resource = pathResourceResolver.resolveResource(requestPath, locations);

			// 静态的文件
			FileProps fileProps = null;

			// 资源存在, 处理输出
			if (resource != null && (fileProps = FileProps.props(Paths.get(resource.getURI()))) != null) {

				// 304 NotModified
				if (HTTP_CACHE_SECONDS >0 && request.ifModified(fileProps)) {
					return;
				}

				// 写头部
				this.writeHeaders(requestPath, request.getResponse(), fileProps);

				// 输出内容
				this.writeResource(request.getResponse(), fileProps);

				// 正常的返回
				return;
			}
		} catch (Exception e) {
		}

		// 返回404错误， 让默认的错误处理器来处理
		throw new BaseRuntimeException(requestPath);
	}

	// 输出头部
	private void writeHeaders(String url, HttpServerResponse response, FileProps fileProps) {
		CharSequence mime = MimeType.getMimeType(url);
		if (mime.equals(HttpConst.APPLICATION_STREAM)) {
			mime = HttpConst.APPLICATION_HTML;
		}
		response.mime(mime);
		response.cache(HTTP_CACHE_SECONDS, fileProps.lastModifiedTime());
	}

	// 输出内容
	private void writeResource(HttpServerResponse response, FileProps fileProps) {
		response.buffer(fileProps);
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