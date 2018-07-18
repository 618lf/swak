package com.swak.reactivex.web.method;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;

import com.swak.entity.Result;
import com.swak.exception.ErrorCode;
import com.swak.executor.Workers;
import com.swak.reactivex.transport.http.Subject;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.reactivex.web.Handler;
import com.swak.reactivex.web.HandlerAdapter;
import com.swak.reactivex.web.annotation.Async;
import com.swak.reactivex.web.annotation.Auth;
import com.swak.reactivex.web.method.resolver.HandlerMethodArgumentResolverComposite;
import com.swak.reactivex.web.method.resolver.HttpCookieValueMethodArgumentResolver;
import com.swak.reactivex.web.method.resolver.MultipartParamMethodArgumentResoler;
import com.swak.reactivex.web.method.resolver.PathVariableMethodArgumentResolver;
import com.swak.reactivex.web.method.resolver.RequestHeaderMethodArgumentResolver;
import com.swak.reactivex.web.method.resolver.RequestParamMethodArgumentResolver;
import com.swak.reactivex.web.method.resolver.ServerRequestMethodArgumentResolver;
import com.swak.reactivex.web.method.resolver.ServerResponseMethodArgumentResolver;
import com.swak.reactivex.web.method.resolver.ServerSessionMethodArgumentResolver;
import com.swak.reactivex.web.result.HandlerResult;

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
		resolvers.add(new RequestParamMethodArgumentResolver(conversionService));
		resolvers.add(new ServerRequestMethodArgumentResolver());
		resolvers.add(new ServerResponseMethodArgumentResolver());
		resolvers.add(new ServerSessionMethodArgumentResolver());
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
	public HandlerResult handle(HttpServerRequest request, HttpServerResponse response, Handler handler) {
		HandlerMethod _handler = (HandlerMethod) handler;
		Object[] args = getMethodArgumentValues(request, _handler);
		return new HandlerResult(this.doHandle(request, _handler, args));
	}

	/**
	 * 如果添加注解 Async 则会异步执行代码
	 * 
	 * @param handler
	 * @param args
	 * @return
	 */
	protected Object doHandle(HttpServerRequest request, HandlerMethod handler, Object[] args) {
		Auth auth = handler.getAuth();
		Async async = handler.getAsync();
		if (auth != null && request.getSubject() != null) {
			return doHandle(request.getSubject(), auth, async, handler, args);
		} else if (async != null) {
			return Workers.future(async.value(), () -> {
				return handler.doInvoke(args);
			});
		}
		return handler.doInvoke(args);
	}

	// 加入权限验证
	protected Object doHandle(Subject subject, Auth auth, Async async, HandlerMethod handler, Object[] args) {
		CompletionStage<Boolean> authFuture = null;
		if (auth.roles().length > 0) {
			authFuture = subject.hasAllRoles(auth.roles());
		} else if (auth.permissions().length > 0) {
			authFuture = subject.isPermittedAll(auth.permissions());
		} else {
			authFuture = CompletableFuture.completedFuture(true);
		}
		if (async != null) {
			return authFuture.thenApplyAsync((b) -> {
				if (b) {
					return handler.doInvoke(args);
				}
				return Result.error(ErrorCode.ACCESS_DENIED);
			}, Workers.executor(async.value()));
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
			if (args[i] == null) {
				throw new IllegalStateException("Could not resolve method parameter at index "
						+ parameter.getParameterIndex() + " in " + parameter.getMethod().toGenericString());
			}
		}
		return args;
	}
}