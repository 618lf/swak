package com.tmt.api.facade;

import com.weibo.api.motan.rpc.ResponseFuture;

public interface GoodsServiceFacadeAsync extends GoodsServiceFacade {
  ResponseFuture sayHelloAsync();

  ResponseFuture getAsync();

  ResponseFuture saveAsync();

  ResponseFuture get_saveAsync();

  ResponseFuture get_save_getAsync();

  ResponseFuture hint_get_save_getAsync();
}
