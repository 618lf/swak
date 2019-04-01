package com.tmt.api.facade;

import com.swak.vertx.transport.codec.Msg;
import java.util.concurrent.CompletableFuture;

public interface GoodsServiceFacadeAsyncx {
  CompletableFuture<Msg> sayHello();
}
