package com.swak.reactivex.handler;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;

import io.reactivex.Observable;

public interface WebFilterChain {

	Observable<Void> filter(HttpServerRequest request, HttpServerResponse response);
}
