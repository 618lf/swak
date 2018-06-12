package com.tmt.shop.service;

import java.util.concurrent.CompletableFuture;

import com.swak.rpc.annotation.RpcService;

/**
 * 服务
 * @author lifeng
 */
@RpcService
public class ShopServiceImpl implements ShopService{

	@Override
	public CompletableFuture<String> get(String id) {
		return CompletableFuture.completedFuture(id);
	}
}
