package com.swak.reactivex.web.method.resolver;

import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;

import com.swak.reactivex.transport.http.multipart.MultipartFile;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.web.annotation.MultipartParam;

/**
 * 上传的文件
 * @author lifeng
 */
public class MultipartParamMethodArgumentResoler extends AbstractMethodArgumentResolver{

	public MultipartParamMethodArgumentResoler(ConversionService conversionService) {
		super(conversionService);
	}
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterAnnotation(MultipartParam.class) != null;
	}

	@Override
	protected Object resolveArgumentInternal(MethodParameter parameter, HttpServerRequest webRequest) {
		MultipartParam param = parameter.getParameterAnnotation(MultipartParam.class);
		Map<String, MultipartFile> files = webRequest.getMultipartFiles();
		return files != null ? files.get(param.value()) : null;
	}
}