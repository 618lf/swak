package com.tmt.api.facade;

import com.sample.api.facade.UserServiceFacade;
import com.weibo.api.motan.rpc.ResponseFuture;

public interface UserServiceFacadeAsync extends UserServiceFacade {
  ResponseFuture getUserAsync();
}
