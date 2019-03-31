package com.swak.flux.web.method.resolver;

import java.util.Map;

import org.springframework.core.convert.ConversionService;

import com.swak.flux.transport.multipart.MultipartFile;
import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.web.annotation.MultipartParam;
import com.swak.flux.web.method.MethodParameter;

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