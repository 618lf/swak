/**
 * 使用方式： 
 * 只需一步： HttpClients.request().XXX.future()
 * 多步操作：
 * Request request = RequestBuilder.create().XXX.build(); 
 * HttpClients.future(request, XXX);
 * @author lifeng
 *
 */
package com.swak.http;