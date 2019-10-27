package com.tmt.api.facade;

import com.tmt.api.entity.User;
import com.weibo.api.motan.transport.async.MotanAsync;

/**
 * 用户服务
 * 
 * @author lifeng
 */
@MotanAsync
public interface UserServiceFacade {

	User getUser();
}