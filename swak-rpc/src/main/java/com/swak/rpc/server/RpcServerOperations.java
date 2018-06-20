package com.swak.rpc.server;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.BiFunction;

import com.swak.reactivex.transport.channel.ChannelOperations;
import com.swak.reactivex.transport.channel.ContextHandler;
import com.swak.rpc.api.RpcRequest;
import com.swak.rpc.api.RpcResponse;
import com.swak.utils.IOUtils;

import io.netty.channel.Channel;
import io.netty.util.ReferenceCountUtil;
import reactor.core.publisher.Mono;

/**
 * Rpc ops
 * @author lifeng
 */
public class RpcServerOperations extends ChannelOperations<RpcRequest, RpcResponse> implements Closeable{

	RpcRequest request;
	RpcResponse response;
	
	protected RpcServerOperations(Channel channel,
			BiFunction<? super RpcRequest, ? super RpcResponse, ? extends Mono<Void>> handler, ContextHandler context, RpcRequest request) {
		super(channel, handler, context);
		this.request = request;
		this.response = new RpcResponse();
	}
	
	/**
	 * 处理请求
	 */
	@Override
	public void onHandlerStart() {
		try {
			this.handler.apply(request, response).subscribe(this);
		} catch (Exception e) {
			this.onError(e);
		}
	}

	@Override
	public void onError(Throwable t) {
		response.setException(t);
		this.onComplete();
	}

	@Override
	public void onComplete() {
		try {
			channel().writeAndFlush(response);
		} finally {
			IOUtils.closeQuietly(this);
			ReferenceCountUtil.release(request);
		}
	}
	
	@Override
	public void close() throws IOException {
		this.request = null;
		this.response = null;
	}
	
	//--------------- 创建 ---------------------
	public static RpcServerOperations bind(Channel channel, BiFunction<? super RpcRequest, ? super RpcResponse, 
			Mono<Void>> handler, ContextHandler context, RpcRequest request) {
		return new RpcServerOperations(channel, handler, context, request);
	}
}