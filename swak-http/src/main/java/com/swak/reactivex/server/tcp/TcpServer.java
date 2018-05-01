package com.swak.reactivex.server.tcp;

import java.net.InetSocketAddress;
import java.util.function.BiConsumer;

import org.springframework.lang.Nullable;

import com.swak.reactivex.handler.HttpHandler;
import com.swak.reactivex.server.ChannelHandler;
import com.swak.reactivex.server.NettyContext;
import com.swak.reactivex.server.channel.ContextHandler;
import com.swak.reactivex.server.options.ServerOptions;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.schedulers.Schedulers;

/**
 * A TCP server connector.
 *
 * @author Stephane Maldini
 * @author Violeta Georgieva
 */
public abstract class TcpServer implements BiConsumer<ChannelPipeline, ContextHandler>, ChannelHandler<Channel, Object>{

	/**
	 * 配置 ServerOptions
	 * @return
	 */
	public abstract ServerOptions options();
	
	/**
	 * 启动服务，并配置HttpHandler
	 * @param handler
	 */
	public abstract void start(HttpHandler handler);
	
	/**
	 * 停止服务器
	 */
	public abstract void stop();
	
	/**
	 * 获得监听端口
	 * @return
	 */
	public abstract InetSocketAddress getAddress();
	
	/**
	 * 异步启动服务器，并注册启动监听
	 * @param handler
	 * @return
	 */
	public final Observable<? extends NettyContext> asyncStart() {
		return ObservableCreate.create(new Consumer<Sink<NettyContext>>() {
			@Override
			public void accept(Sink<NettyContext> sink) throws Exception {
				startWithSink(sink);
			}
		}).subscribeOn(Schedulers.newThread());
	}
	
	/**
	 * 异步启动服务，并注册启动通知 -- 相当于一个回调
	 * @param sink
	 * @param handler
	 */
	protected void startWithSink(Sink<NettyContext> sink) {
		
		/**
		 * init Handler
		 */
		ContextHandler contextHandler = ContextHandler.newServerContext(options(), sink)
				.onPipeline(this).onChannel(this);
		
		/**
		 * start server
		 */
		ServerBootstrap b = options().get()
				.localAddress(options().getAddress())
				.childHandler(contextHandler);
		
		/**
		 * 监听启动过程
		 */
		contextHandler.setFuture(b.bind());
	}
	
	static class ObservableCreate<T> extends Observable<T> {
		
		final Consumer<Sink<T>> callback;
		
		public ObservableCreate(Consumer<Sink<T>> callback) {
			this.callback = callback;
		}
		
		@Override
		protected void subscribeActual(Observer<? super T> observer) {
			DefaultObservableSink<T> emitter = new DefaultObservableSink<T>(observer);
			observer.onSubscribe(emitter);
			try {
				callback.accept(emitter);
			}
			catch (Throwable ex) {
				emitter.error(ex);
			}
		}
		
		public static <T> ObservableCreate<T> create(Consumer<Sink<T>> callback) {
			 ObjectHelper.requireNonNull(callback, "The callback is null");
		     return new ObservableCreate<>(callback);
		}
	}
	
	static class DefaultObservableSink<T> implements Sink<T> , Disposable{
		
		Observer<? super T> actual;
		boolean isDone;
		
		public DefaultObservableSink(Observer<? super T> actual) {
			this.actual = actual;
		}

		@Override
		public void success(T value) {
			if(!isDone) {
				actual.onNext(value);
			}
			actual.onComplete();
		}

		@Override
		public void error(Throwable e) {
			actual.onError(e);
		}

		@Override
		public void dispose() {
			isDone = true;
		}

		@Override
		public boolean isDisposed() {
			return isDone;
		}
	}
	
	/**
	 * 控制数据的返回
	 * @author lifeng
	 * @param <T>
	 */
	public interface Sink<T> {
		void success(@Nullable T value);
		void error(Throwable e);
	}
}