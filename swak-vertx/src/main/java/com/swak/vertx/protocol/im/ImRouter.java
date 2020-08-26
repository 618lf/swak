package com.swak.vertx.protocol.im;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.swak.annotation.ImOps;
import com.swak.utils.Lists;
import com.swak.utils.Maps;

import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;

/**
 * Im 路由器
 * 
 * @author lifeng
 * @date 2020年8月25日 下午10:28:55
 */
public class ImRouter implements Handler<ServerWebSocket> {

	/**
	 * 所有的处理器
	 */
	private List<ImRoute> routes = Lists.newArrayList(5);

	/**
	 * 处理器分类
	 */
	private ImRouteState routeState = new ImRouteState(this);

	/**
	 * 创建一个
	 * 
	 * @return
	 */
	public ImRoute route() {
		ImRoute route = new ImRoute();
		routes.add(route);
		return route;
	}

	/**
	 * 处理socket事件
	 */
	@Override
	public void handle(ServerWebSocket event) {
		this.consumer().accept(event);
	}

	/**
	 * 处理socket事件
	 * 
	 * @return
	 */
	private Consumer<ServerWebSocket> consumer() {
		return (decorate) -> {

			// 处理连接事件
			new ImContextImpl(ImOps.Connect, routeState, decorate).next();

			// 处理异常
			decorate.exceptionHandler(e -> {
				new ImContextImpl(ImOps.Error, routeState, decorate, e).next();
			});

			// 处理消息
			decorate.frameHandler(message -> {
				new ImContextImpl(ImOps.Message, routeState, decorate, message).next();
			});

			// 关闭
			decorate.closeHandler(v -> {
				new ImContextImpl(ImOps.Close, routeState, decorate).next();
			});
		};
	}

	public List<ImRoute> getRoutes() {
		return routes;
	}

	/**
	 * Route 状态
	 * 
	 * @author lifeng
	 * @date 2020年8月25日 下午11:47:28
	 */
	public static class ImRouteState {

		/**
		 * 路由信息
		 */
		Map<ImOps, ImRouteChain> routes = Maps.newHashMap();
		ImRouter router;

		public ImRouteState(ImRouter router) {
			this.router = router;
		}

		public ImRouteChain get(ImOps ops) {
			ImRouteChain chain = routes.get(ops);
			return chain != null ? chain : this.getSync(ops);
		}

		private synchronized ImRouteChain getSync(ImOps ops) {
			List<ImRoute> routes = router.getRoutes();
			for (ImRoute route : routes) {
				if (route.getOps() == ImOps.All || route.getOps() == ops) {
					this.route(ops, route);
				}
			}
			return this.routes.computeIfAbsent(ops, (key) -> {
				return new ImRouteChain(key);
			});
		}

		private void route(ImOps ops, ImRoute route) {
			ImRouteChain chain = routes.computeIfAbsent(ops, (key) -> {
				return new ImRouteChain(key);
			});
			chain.route(route);
		}
	}

	/**
	 * ImRoute 链式操作
	 * 
	 * @author lifeng
	 * @date 2020年8月25日 下午11:44:24
	 */
	public static class ImRouteChain {

		ImOps ops;
		List<ImRoute> routes;

		public ImRouteChain(ImOps ops) {
			this.ops = ops;
			this.routes = Lists.newArrayList(1);
		}

		public ImRouteChain route(ImRoute route) {
			this.routes.add(route);
			return this;
		}

		/**
		 * 总是会取到数据
		 * 
		 * @param index
		 * @return
		 */
		public ImRoute next(int index) {
			return routes.size() != 0 ? routes.get(index % routes.size()) : ImRoute.NONE;
		}
	}

	/**
	 * 路由信息
	 * 
	 * @author lifeng
	 * @date 2020年8月25日 下午10:45:45
	 */
	public static class ImRoute {

		/**
		 * NULL 处理
		 */
		static ImRoute NONE = new ImRoute().handler((context) -> {

		});

		ImOps ops;
		Handler<ImContext> handler;

		public ImRoute ops(ImOps ops) {
			this.ops = ops;
			return this;
		}

		public ImRoute handler(Handler<ImContext> handler) {
			this.handler = handler;
			return this;
		}

		public ImOps getOps() {
			return ops;
		}

		public Handler<ImContext> getHandler() {
			return handler;
		}
	}
}