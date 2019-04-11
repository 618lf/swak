package com.swak.http.handler;

import org.asynchttpclient.Response;

/**
 * 直接响应 String
 * @author lifeng
 */
public class TextResponse extends AbstractResponse<String>{

	@Override
	public String onCompleted(Response response) throws Exception {
		int status = response.getStatusCode();
		if (status >= 200 && status < 300) {
			return response.getResponseBody(charset);
		}
	    throw new RuntimeException(response.toString());
	}
	
	/**
	 * 对象
	 * @return
	 */
	public static TextResponse create() {
		return new TextResponse();
	}
}