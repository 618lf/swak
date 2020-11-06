package com.swak.vertx.config;

import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;

import com.swak.annotation.Context;
import com.swak.annotation.FluxService;
import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.ClassMeta;
import com.swak.asm.MethodCache.MethodMeta;
import com.swak.asm.Wrapper;
import com.swak.meters.MetricsFactory;
import com.swak.vertx.transport.codec.Msg;
import com.swak.vertx.transport.vertx.ServiceVerticle;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import lombok.EqualsAndHashCode;

/**
 * 创建服务 bean
 *
 * @author: lifeng
 * @date: 2020/3/29 18:52
 */
@EqualsAndHashCode(callSuper = false)
public class ServiceBean extends AbstractBean implements Handler<Message<Msg>> {

	private static Logger logger = LoggerFactory.getLogger(ServiceVerticle.class);

	private Object ref;
	private Class<?> beanClass;
	private Class<?> interClass;
	private Context context;
	private int instances;
	private String pool;
	private Wrapper wrapper;
	private ClassMeta classMeta;

	@Autowired(required = false)
	private MetricsFactory metricsFactory;

	@Override
	public void initializing() throws Exception {
		FluxService mapping = AnnotatedElementUtils.findMergedAnnotation(beanClass, FluxService.class);
		this.context = mapping.context();
		this.instances = mapping.instances();
		this.pool = mapping.pool();
		this.wrapper = Wrapper.getWrapper(this.interClass);
		this.classMeta = MethodCache.set(this.interClass);
		this.initMetrics();
	}

	private void initMetrics() {
		this.classMeta.getMethods().forEach(meta -> {
			String metricName = this.beanClass.getName() + "." + meta.getMethodDesc();
			meta.applyMetrics(metricsFactory, metricName);
		});
	}

	public Object getRef() {
		return ref;
	}

	public void setRef(Object ref) {
		this.ref = ref;
	}

	public Context getContext() {
		return context;
	}

	public int getInstances() {
		return instances;
	}

	public String getPool() {
		return pool;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	public Class<?> getInterClass() {
		return interClass;
	}

	public void setInterClass(Class<?> interClass) {
		this.interClass = interClass;
	}

	private Object preHandle(MethodMeta method) {
		return method.getMetrics() != null ? method.getMetrics().begin() : null;
	}

	/**
	 * 处理消息
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void handle(Message<Msg> event) {
		Msg request = event.body();
		MethodMeta method = this.classMeta.lookup(request.getMethodDesc());
		Object metrics = this.preHandle(method);
		try {
			Object result = wrapper.invokeMethod(ref, method.getMethodDesc(), request.getArguments());
			if (result != null && result instanceof CompletionStage) {
				CompletionStage<Object> resultFuture = (CompletionStage<Object>) result;
				resultFuture.whenComplete((r, e) -> {
					this.handleResult(r, e, metrics, method, event);
				});
			} else {
				this.handleResult(result, null, metrics, method, event);
			}
		} catch (Throwable e) {
			this.handleResult(null, e, metrics, method, event);
		}
	}

	@SuppressWarnings("unchecked")
	private void handleResult(Object result, Throwable e, Object metrics, MethodMeta method, Message<Msg> event) {
		Msg request = event.body();
		Msg response = request.reset();
		if (e != null) {
			Throwable error = new RuntimeException(String.format("Invoke [Service:{%s} - Method: {%s}] Error.",
					beanClass.getName(), method.getMethodDesc()), e.getCause() != null ? e.getCause() : e);
			logger.error("Invoke [Service:{} - Method: {}] Error.", beanClass.getName(), method.getMethodDesc(), e);
			response.setError(error);
		}
		if (result != null) {
			response.setResult(result);
		}
		event.reply(response);

		// 应用指标统计
		if (method.getMetrics() != null) {
			method.getMetrics().end(metrics, e == null);
		}
	}
}