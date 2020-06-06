package com.swak.fetty.transport.promise;

import com.swak.fetty.transport.channel.Channel;

/**
 * 异步执行结果
 * 
 * @author lifeng
 * @date 2020年6月4日 下午5:05:25
 */
public interface ChannelPromise {

	Channel channel();

	ChannelPromise setSuccess(Object success);

	ChannelPromise setFailure(Throwable cause);

	ChannelPromise addListener(PromiseListener listener);
}