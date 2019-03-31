package com.swak.flux.web.method;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.springframework.core.convert.ConversionService;

import com.swak.entity.Result;
import com.swak.exception.ErrorCode;
import com.swak.flux.transport.Subject;
import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.transport.server.HttpServerResponse;
import com.swak.flux.web.Handler;
import com.swak.flux.web.HandlerAdapter;
import com.swak.flux.web.annotation.Auth;
import com.swak.flux.web.method.resolver.HandlerMethodArgumentResolverComposite;
import com.swak.flux.web.method.resolver.HttpCookieValueMethodArgumentResolver;
import com.swak.flux.web.method.resolver.MultipartParamMethodArgumentResoler;
import com.swak.flux.web.method.resolver.PathVariableMethodArgumentResolver;
import com.swak.flux.web.method.resolver.RequestHeaderMethodArgumentResolver;
import com.swak.flux.web.method.resolver.ServerModelMethodArgumentResolver;

/**
 * 请求处理器
 * 
 * @author lifeng
 */
public class RequestMappingHandlerAdapter implements HandlerAdapter {

	private HandlerMethodArgumentResolver argumentResolver;

	public RequestMappingHandlerAdapter(ConversionService conversionService) {
		initArgumentResolvers(conversionService);
	}

	/**
	 * 初始化参数解析
	 * 
	 * @param conversionService
	 */
	private void initArgumentResolvers(ConversionService conversionService) {
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();
		resolvers.add(new PathVariableMethodArgumentResolver(conversionService));
		resolvers.add(new MultipartParamMethodArgumentResoler(conversionService));
		resolvers.add(new RequestHeaderMethodArgumentResolver(conversionService));
		resolvers.add(new HttpCookieValueMethodArgumentResolver(conversionService));
		resolvers.add(new ServerModelMethodArgumentResolver(conversionService));
		this.argumentResolver = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
	}

	@Override
	public boolean supports(Handler handler) {
		return handler instanceof HandlerMethod;
	}

	/**
	 * 支持异步执行代码
	 */
	@Override
	public Object handle(HttpServerRequest request, HttpServerResponse response, Handler handler) {
		HandlerMethod _handler = (HandlerMethod) handler;
		Object[] args = getMethodArgumentValues(request, _handler);
		return this.doHandle(request, _handler, args);
	}

	/**
	 * 
	 * @param handler
	 * @param args
	 * @return
	 */
	protected Object doHandle(HttpServerRequest request, HandlerMethod handler, Object[] args) {
		Auth auth = handler.getAuth();
		if (auth != null && request.getSubject() != null) {
			return doHandle(request.getSubject(), auth, handler, args);
		}
		return handler.doInvoke(args);
	}

	// 加入权限验证
	protected Object doHandle(Subject subject, Auth auth, HandlerMethod handler, Object[] args) {
		CompletionStage<Boolean> authFuture = null;
		if (auth.roles().length > 0) {
			authFuture = subject.hasAllRoles(auth.roles());
		} else if (auth.permissions().length > 0) {
			authFuture = subject.isPermittedAll(auth.permissions());
		} else {
			authFuture = CompletableFuture.completedFuture(true);
		}
		return authFuture.thenApply((b) -> {
			if (b) {
				return handler.doInvoke(args);
			}
			return Result.error(ErrorCode.ACCESS_DENIED);
		});
	}

	private Object[] getMethodArgumentValues(HttpServerRequest request, HandlerMethod handler) {
		MethodParameter[] parameters = handler.getParameters();
		Object[] args = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			MethodParameter parameter = parameters[i];
			args[i] = this.argumentResolver.resolveArgument(parameter, request);
		}
		return args;
	}
}