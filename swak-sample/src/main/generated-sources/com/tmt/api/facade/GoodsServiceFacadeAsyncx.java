package com.tmt.api.facade;

import com.swak.vertx.transport.codec.Msg;
import java.util.concurrent.CompletableFuture;

public interface GoodsServiceFacadeAsyncx {
  CompletableFuture<Msg> sayHello();

  CompletableFuture<Msg> get();

  CompletableFuture<Msg> save();

  CompletableFuture<Msg> get_save();

  CompletableFuture<Msg> get_save_get();

  CompletableFuture<Msg> hint_get_save_get();
}
