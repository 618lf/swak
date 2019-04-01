package com.tmt.shop.service;

import com.swak.flux.verticle.Msg;
import com.tmt.shop.entity.Foo;
import java.lang.String;
import java.util.concurrent.CompletableFuture;

public interface FooServiceAsyncx {
  CompletableFuture<Msg> hello(String name);

  CompletableFuture<Msg> rename(Foo user, String name);
}
