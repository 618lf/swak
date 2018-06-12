package com.tmt.shop.service;

import java.util.concurrent.CompletableFuture;

public interface ShopService {

	CompletableFuture<String> get(String id);
}
