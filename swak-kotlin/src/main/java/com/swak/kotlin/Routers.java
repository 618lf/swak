package com.swak.kotlin;

import java.util.function.Consumer;

import com.swak.reactivex.web.function.RouterFunction;

/**
 * 简化 router 的流程
 * @author lifeng
 */
public class Routers {

	/**
	 * 简化 router 的流程
	 * @param consumer
	 * @return
	 */
	public static RouterFunction router(Consumer<RouterFunctionDsl> consumer) {
		return RouterFunctionDslKt.router((dsl) -> {
			consumer.accept(dsl);
			return null;
		});
	}
}
