package com.swak.vertx.transport.vertx;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.swak.vertx.transport.VertxProxy;
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

/**
 * Vertx 的配置 单机版本
 *
 * @author: lifeng
 * @date: 2020/3/29 19:11
 */
public class StandardVertx implements VertxProxy {

	protected VertxOptions vertxOptions;
	protected DeliveryOptions deliveryOptions;
	protected VertxImpl vertx;
	protected ContextInternal context;

	public StandardVertx(VertxOptions vertxOptions, DeliveryOptions deliveryOptions) {
		this.vertxOptions = vertxOptions;
		this.deliveryOptions = deliveryOptions;
		this.vertx = (VertxImpl)Vertx.vertx(vertxOptions);
		this.context = this.vertx.createEventLoopContext(this.vertx.getEventLoopGroup().next(), null,
				Thread.currentThread().getContextClassLoader());
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
		if (Vertx.currentContext() == null) {
			context.runOnContext((v) -> this.sentMessageInternal(address, request, timeout, replyHandler));
		} else {
			this.sentMessageInternal(address, request, timeout, replyHandler);
		}
	}

	/**
	 * 发送消息
	 */
	private void sentMessageInternal(String address, Msg request, int timeout,
			Handler<AsyncResult<Message<Msg>>> replyHandler) {
		DeliveryOptions deliveryOptions = this.deliveryOptions;
		if (timeout >= 1) {
			deliveryOptions = new DeliveryOptions(this.deliveryOptions);
			deliveryOptions.setSendTimeout(timeout);
		}
		vertx.eventBus().request(address, request, deliveryOptions, replyHandler);
	}

	/**
	 * 立即提交代码
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> CompletableFuture<T> future(Supplier<T> supplier) {
		CompletableFuture<T> future = new CompletableFuture<>();
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
		CompletableFuture<T> future = new CompletableFuture<>();
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

	/**
	 * 返回实际的Vertx对象
	 */
	@Override
	public Vertx me() {
		return this.vertx;
	}
}