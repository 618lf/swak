package com.swak.zookeeper;

public interface DataListener {
	void dataChanged(String path, Object value, EventType eventType);
}
