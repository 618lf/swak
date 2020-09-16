package com.swak.zookeeper;

/**
 * 状态变化
 * 
 * @author lifeng
 * @date 2020年9月15日 下午8:47:12
 */
public interface StateListener {

	int SESSION_LOST = 0;

	int CONNECTED = 1;

	int RECONNECTED = 2;

	int SUSPENDED = 3;

	int NEW_SESSION_CREATED = 4;

	void stateChanged(int connected);
}
