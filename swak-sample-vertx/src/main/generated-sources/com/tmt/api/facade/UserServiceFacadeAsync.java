package com.tmt.api.facade;

import com.weibo.api.motan.rpc.ResponseFuture;

public interface UserServiceFacadeAsync extends UserServiceFacade {
  ResponseFuture getUserAsync();
}
