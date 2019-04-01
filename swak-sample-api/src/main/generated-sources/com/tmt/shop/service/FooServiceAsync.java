package com.tmt.shop.service;

import com.tmt.shop.entity.Foo;
import com.weibo.api.motan.rpc.ResponseFuture;
import java.lang.String;

public interface FooServiceAsync extends FooService {
  ResponseFuture helloAsync(String name);

  ResponseFuture renameAsync(Foo user, String name);
}
