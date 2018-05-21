package com.swak.quasar.hello;

import java.io.IOException;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import co.paralleluniverse.fibers.okhttp.FiberOkHttpClient;
import co.paralleluniverse.fibers.okhttp.FiberOkHttpUtil;

public class TestClient {

	public static void main(String[] args) throws InterruptedException, IOException {
		FiberOkHttpClient client = new FiberOkHttpClient();
		Request request = new Request.Builder().url("http://www.baidu.com").build();
		Response response = FiberOkHttpUtil.executeInFiber(client, request);
		System.out.println(response);
	}
}
