package com.swak.vertx.config;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.swak.exception.BaseRuntimeException;
import com.swak.vertx.handler.VertxProxy;
import com.swak.vertx.transport.codec.Msg;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.file.FileSystem;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.impl.VertxImpl;
import io.vertx.core.json.JsonObject;

/**
 * vertx 的配置 bean
 * 
 * @author lifeng
 */
public class StandardVertx implements VertxProxy {

	protected VertxOptions vertxOptions;
	protected DeliveryOptions deliveryOptions;
	protected boolean inited;
	protected VertxImpl vertx;
	protected ContextInternal context;

	public StandardVertx(VertxOptions vertxOptions, DeliveryOptions deliveryOptions) {
		this.vertxOptions = vertxOptions;
		this.deliveryOptions = deliveryOptions;
	}

	/**
	 * 启动服务器
	 * 
	 * @param startFuture
	 */
	@Override
	public void apply(Consumer<Vertx> apply) {
		Vertx vertx = Vertx.vertx(vertxOptions);
		apply.accept(vertx);
		this.inited = true;
		this.vertx = (VertxImpl) vertx;
		this.context = this.vertx.createEventLoopContext(null, null, new JsonObject(),
				Thread.currentThread().getContextClassLoader());
	}

	/**
	 * 停止服务器
	 * 
	 * @param startFuture
	 */
	@Override
	public void destroy(Consumer<Vertx> apply) {
		if (this.inited) {
			apply.accept(this.vertx);
		}
	}

	/**
	 * 发送消息服务
	 */
	@Override
	public void sentMessage(String address, Msg request, int timeout) {
		this.sentMessage(address, request, timeout, null);
	}

	/**
	 * 发送消息服务
	 */
	@Override
	public void sentMessage(String address, Msg request, int timeout, Handler<AsyncResult<Message<Msg>>> replyHandler) {
		if (!this.inited) {
			throw new BaseRuntimeException("Vertx doesn't inited");
		}
		if (Vertx.currentContext() == null) {
			context.runOnContext((v) -> {
				this.sentMessageInternal(address, request, timeout, replyHandler);
			});
		} else {
			this.sentMessageInternal(address, request, timeout, replyHandler);
		}
	}

	// 发送消息
	private void sentMessageInternal(String address, Msg request, int timeout,
			Handler<AsyncResult<Message<Msg>>> replyHandler) {
		DeliveryOptions deliveryOptions = this.deliveryOptions;
		if (timeout >= 1) {
			deliveryOptions = new DeliveryOptions(this.deliveryOptions);
			deliveryOptions.setSendTimeout(timeout);
		}
		vertx.eventBus().send(address, request, deliveryOptions, replyHandler);
	}

	/**
	 * 立即提交代码
	 * 
	 * @param supplier
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> CompletableFuture<T> future(Supplier<T> supplier) {
		CompletableFuture<T> future = new CompletableFuture<T>();
		this.currentContext().executeBlocking((f) -> {
			T t = supplier.get();
			f.complete(t);
		}, false, (r) -> {
			Throwable exception = r.cause();
			if (exception != null) {
				future.completeExceptionally(exception);
			} else {
				T t = (T) r.result();
				future.complete(t);
			}
		});
		return future;
	}

	/**
	 * 有序的提交代码
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> CompletableFuture<T> order(Supplier<T> supplier) {
		CompletableFuture<T> future = new CompletableFuture<T>();
		this.currentContext().executeBlocking((f) -> {
			T t = supplier.get();
			f.complete(t);
		}, (r) -> {
			Throwable exception = r.cause();
			if (exception != null) {
				future.completeExceptionally(exception);
			} else {
				T t = (T) r.result();
				future.complete(t);
			}
		});
		return future;
	}

	/**
	 * 如果有则使用当前的 context
	 * 
	 * @return
	 */
	private ContextInternal currentContext() {
		ContextInternal currentContext = vertx.getContext();
		return currentContext == null ? this.context : currentContext;
	}

	/**
	 * 返回文件系统
	 */
	@Override
	public FileSystem fileSystem() {
		return this.vertx.fileSystem();
	}
}