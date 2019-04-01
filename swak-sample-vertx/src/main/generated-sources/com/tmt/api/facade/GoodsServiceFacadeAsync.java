package com.tmt.api.facade;

import com.weibo.api.motan.rpc.ResponseFuture;

public interface GoodsServiceFacadeAsync extends GoodsServiceFacade {
  ResponseFuture sayHelloAsync();
}
