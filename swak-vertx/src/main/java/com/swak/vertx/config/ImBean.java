package com.swak.vertx.config;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;

import com.swak.annotation.FluxService;
import com.swak.annotation.ImApi;
import com.swak.annotation.ImMapping;
import com.swak.annotation.ImOps;
import com.swak.meters.MetricsFactory;
import com.swak.vertx.invoker.FluxMethodInvoker;
import com.swak.vertx.invoker.MethodInvoker;
import com.swak.vertx.protocol.im.ImContext;
import com.swak.vertx.protocol.im.ImHandler;
import com.swak.vertx.protocol.im.ImRouter;
import com.swak.vertx.protocol.im.ImRouter.ImRoute;
import com.swak.vertx.transport.VertxProxy;

import io.vertx.core.Handler;

/**
 * 如何处理WebSocket消息
 * 
 * @author lifeng
 * @date 2020年8月25日 下午2:36:24
 */
public class ImBean extends AbstractBean implements Handler<ImContext> {

	@Autowired
	private VertxProxy proxy;
	@Autowired
	private ImHandler handlerAdapter;
	@Autowired(required = false)
	private MetricsFactory metricsFactory;
	private MethodInvoker methodInvoker;
	private Class<?> type;
	private Object ref;
	private Method method;
	private String path;
	private int port;
	private ImOps ops;

	/**
	 * 有可能收集 Bean 的时候这个 init 还没执行
	 */
	@Override
	public void initializing() throws Exception {
		ImApi apiMapping = AnnotatedElementUtils.findMergedAnnotation(type, ImApi.class);
		FluxService serviceMapping = AnnotatedElementUtils.findMergedAnnotation(type, FluxService.class);
		ImMapping classMapping = AnnotatedElementUtils.findMergedAnnotation(type, ImMapping.class);
		ImMapping methodMapping = AnnotatedElementUtils.findMergedAnnotation(method, ImMapping.class);

		// 支持的路径
		this.path = apiMapping.path();

		// 端口
		this.port = apiMapping.port();

		// 服务个数
		this.instances = apiMapping.instances();

		// method
		ImOps requestMethod = classMapping.method() == ImOps.All ? methodMapping.method() : classMapping.method();
		this.ops = ops == ImOps.All ? null : requestMethod;

		this.methodInvoker = this.prependMethodHandler(proxy, type, ref, method, serviceMapping != null);

		// 应用监控
		this.methodInvoker.applyMetrics(metricsFactory);
	}

	/**
	 * 处理请求的执行的方法
	 *
	 * @param mergeService
	 * @return
	 */
	private MethodInvoker prependMethodHandler(VertxProxy vertx, Class<?> clazz, Object bean, Method method,
			boolean mergeService) {
		return mergeService ? new FluxMethodInvoker(vertx, clazz, bean, method)
				: new MethodInvoker(clazz, bean, method);
	}

	@Override
	public void handle(ImContext context) {
		this.handlerAdapter.handle(context, methodInvoker);
	}

	/**
	 * 挂载
	 * 
	 * @param router
	 */
	public void mounton(ImRouter router) {
		ImRoute route = router.route();
		route.path(path).ops(ops).handler(this);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public Object getRef() {
		return ref;
	}

	public void setRef(Object ref) {
		this.ref = ref;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}