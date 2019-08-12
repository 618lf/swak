package com.tmt.api.facade;

import java.util.concurrent.CompletableFuture;

import com.swak.vertx.transport.codec.Msg;
import com.tmt.api.entity.Goods;

public interface GoodsServiceFacadeAsyncx {
  CompletableFuture<Msg> sayHello();
  CompletableFuture<Goods> get();
}
