package com.swak.webflux.security;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

/**
 * 安全filter
 * @author lifeng
 */
public class SecurityFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		DataBuffer dataBuffer = exchange.getResponse().bufferFactory().allocateBuffer();
		dataBuffer.write("123".getBytes());
		return exchange.getResponse().writeWith(Mono.just(dataBuffer));
	}
}