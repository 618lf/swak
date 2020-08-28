package com.swak.vertx.protocol.im;

import java.util.Map;

import com.swak.annotation.ImOps;
import com.swak.utils.Maps;
import com.swak.vertx.protocol.im.ImRouter.ImMatch;
import com.swak.vertx.protocol.im.ImRouter.ImRouteChain;
import com.swak.vertx.protocol.im.ImRouter.ImRouteState;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import io.vertx.core.http.impl.VertxHttpUtils;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.impl.VertxThread;

/**
 * ImContext 实现
 * 
 * @author lifeng
 * @date 2020年8月25日 下午11:13:40
 */
public class ImContextImpl implements ImContext {

	int index;
	String path;
	ImOps ops;
	ServerWebSocket socket;
	Throwable error;
	WebSocketFrame message;
	ImRouteState routeState;
	ImRouteChain chain;
	Map<String, Object> attributes = Maps.newHashMap();
	ContextInternal context;
	ImRequestImpl request;
	ImResponseImpl response;
	Map<String, String> variables;

	public ImContextImpl(ImOps ops, ImRouteState routeState, ServerWebSocket socket) {
		this.ops = ops;
		this.path = socket.path();
		this.socket = socket;
		this.init();
	}

	public ImContextImpl(ImOps ops, ImRouteState routeState, ServerWebSocket socket, Throwable error) {
		this.ops = ops;
		this.path = socket.path();
		this.socket = socket;
		this.routeState = routeState;
		this.error = error;
		this.init();
	}

	public ImContextImpl(ImOps ops, ImRouteState routeState, ServerWebSocket socket, WebSocketFrame message) {
		this.ops = ops;
		this.path = socket.path();
		this.routeState = routeState;
		this.socket = socket;
		this.message = message;
		this.init();
	}

	private void init() {
		ImMatch match = this.routeState.lookup(new ImPredicate(this.path, this.ops));
		this.chain = match.getChain();
		this.variables = match.getVariables();
		this.context = this.context();
		this.request = new ImRequestImpl();
		this.response = new ImResponseImpl();
		this.request.params.addAll(match.getVariables());
	}

	/**
	 * 3.8.0 之后不能获取 Context
	 */
	private ContextInternal context() {
		Thread current = Thread.currentThread();
		if (current instanceof VertxThread) {
			return ((VertxThread) current).getContext();
		}
		return null;
	}

	@Override
	public ImOps getOps() {
		return this.ops;
	}

	public ImContextImpl setOps(ImOps ops) {
		this.ops = ops;
		return this;
	}

	@Override
	public void next() {
		chain.next(index++).handler.handle(this);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) attributes.get(key);
	}

	@Override
	public <T> ImContextImpl put(String key, T t) {
		attributes.put(key, t);
		return this;
	}

	@Override
	public ContextInternal getContext() {
		return this.context;
	}

	@Override
	public boolean closed() {
		return this.socket.isClosed();
	}

	@Override
	public ImRequest request() {
		return this.request;
	}

	@Override
	public ImResponse response() {
		return this.response;
	}

	/**
	 * 请求的处理，对参数的处理
	 * 
	 * @author lifeng
	 * @date 2020年8月26日 上午11:22:25
	 */
	public class ImRequestImpl implements ImRequest {

		private MultiMap params;

		@Override
		public String getBodyAsString() {
			return message != null ? message.textData() : null;
		}

		@Override
		public Buffer getBody() {
			return message != null ? message.binaryData() : null;
		}

		@Override
		public MultiMap headers() {
			return socket.headers();
		}

		@Override
		public String getHeader(String header) {
			return socket.headers().get(header);
		}

		@Override
		public String uri() {
			return socket.path();
		}

		@Override
		public String getParam(String param) {
			return variables.get(param);
		}

		@Override
		public MultiMap params() {
			if (params == null) {
				params = VertxHttpUtils.params(uri());
			}
			return params;
		}
	}

	/**
	 * 响应的处理
	 * 
	 * @author lifeng
	 * @date 2020年8月26日 上午11:22:47
	 */
	public class ImResponseImpl implements ImResponse {

		@Override
		public ImResponse putHeader(CharSequence name, CharSequence value) {
			return this;
		}

		@Override
		public ImResponse out(String chunk) {
			socket.writeTextMessage(chunk);
			return this;
		}

		@Override
		public ImResponse out(Buffer buffer) {
			socket.writeBinaryMessage(buffer);
			return this;
		}

		@Override
		public ImResponse sendFile(String filename, Handler<AsyncResult<Void>> resultHandler) {
			return this;
		}
	}
}
