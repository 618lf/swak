package com.swak.reactivex.handler;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

import io.reactivex.Observable;

public interface WebFilterChain {

	Observable<Void> filter(HttpServletRequest request, HttpServletResponse response);
}
