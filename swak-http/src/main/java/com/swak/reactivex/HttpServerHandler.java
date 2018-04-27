package com.swak.reactivex;

import com.swak.common.utils.Lists;
import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.reactivex.handler.ExceptionHandlingWebHandler;
import com.swak.reactivex.handler.FilteringWebHandler;
import com.swak.reactivex.handler.HttpHandler;
import com.swak.reactivex.handler.HttpWebHandlerAdapter;
import com.swak.reactivex.handler.WebHandler;
import com.swak.reactivex.web.DispatcherHandler;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 测试入口
 * @author lifeng
 */
public class HttpServerHandler {
	
	public static WebHandler buildWebHandler() {
		return new DispatcherHandler();
	}
	
	public static HttpHandler buildHttpHandler(WebHandler handler) {
		WebHandler delegate = new FilteringWebHandler(handler, Lists.newArrayList());
		delegate = new ExceptionHandlingWebHandler(delegate, Lists.newArrayList());
		HttpHandler httpHandler = new HttpWebHandlerAdapter(delegate);
		return httpHandler;
	}
	
	public static void main(String[] args) {
		HttpServletRequest request = HttpServletRequest.build(null, null);
		HttpServletResponse response = request.getResponse();
		HttpHandler httpHandler = buildHttpHandler(buildWebHandler());
		httpHandler.apply(request, response).subscribe(new Observer<Void>() {
			@Override
			public void onSubscribe(Disposable d) {
				System.out.println("订阅");
			}

			@Override
			public void onNext(Void t) {
				System.out.println("收到数据");
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("执行出错" + e);
			}

			@Override
			public void onComplete() {
				System.out.println("完成");
			}
		});
	}
}