package com.swak.vertx.config;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.swak.exception.BaseRuntimeException;
import com.swak.vertx.handler.VertxHandler;
import com.swak.vertx.transport.codec.Msg;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.file.FileSystem;

/**
 * vertx 的配置 bean
 * 
 * @author lifeng
 */
public class VertxBean implements VertxHandler {

	protected VertxOptions vertxOptions;
	protected DeliveryOptions deliveryOptions;
	protected boolean inited;
	protected Vertx vertx;

	public VertxBean(VertxOptions vertxOptions, DeliveryOptions deliveryOptions) {
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
		// build standard vertx
		Vertx vertx = Vertx.vertx(vertxOptions);
		apply.accept(vertx);

		// 初始化完成标志
		this.inited = true;
		this.vertx = vertx;
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
			vertx.executeBlocking((f) -> {
				this.sentMessageInternal(address, request, timeout, replyHandler);
				f.complete();
			}, null);
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
	 * 获取文件系统
	 */
	@Override
	public FileSystem fileSystem() {
		return vertx.fileSystem();
	}

	/**
	 * 使用异步队列执行代码
	 * 
	 * @param supplier
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> CompletableFuture<T> future(Supplier<T> supplier) {
		CompletableFuture<T> future = new CompletableFuture<T>();
		vertx.executeBlocking((f) -> {
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
}