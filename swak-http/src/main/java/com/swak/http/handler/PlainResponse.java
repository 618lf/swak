package com.swak.http.handler;

import org.asynchttpclient.Response;

/**
 * 原始的 Response
 * 
 * @author lifeng
 */
public class PlainResponse extends AbstractResponse<Response>{

	@Override
	public Response onCompleted(Response response) throws Exception {
		return response;
	}
	
	/**
	 * 对象
	 * @return
	 */
	public static PlainResponse create() {
		return new PlainResponse();
	}
}