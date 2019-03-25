/**
 * 使用方式： 
 * 只需一步： HttpClients.request().XXX.future()
 * 多步操作：
 * Request request = RequestBuilder.create().XXX.build(); 
 * HttpClients.future(request, XXX);
 * 
 * https://github.com/AsyncHttpClient/async-http-client
 * 
 * 没见到监控的，可以将自定义 EventLoop
 * 
 * 如果可能，可以将http、redis的 EventLoop 设置为一个。至少可以監控起來
 * @author lifeng
 */
package com.swak.http;