package com.swak.zookeeper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.apache.zookeeper.CreateMode;

import com.swak.lock.Lock;

/**
 * Zookeeper 服务
 * 
 * @author lifeng
 * @date 2020年9月15日 下午8:50:08
 */
public interface ZookeeperService {

	long id();

	void addStateListener(StateListener listener);

	void removeStateListener(StateListener listener);

	List<String> addChildListener(String path, final ChildListener listener);

	void removeChildListener(String path, ChildListener listener);

	void addDataListener(String path, DataListener listener);

	void addDataListener(String path, DataListener listener, Executor executor);

	void removeDataListener(String path, DataListener listener);

	String get(String path);

	void create(String path, CreateMode mode);

	void create(String path, String content, CreateMode mode);

	void update(String path, String content);

	List<String> getChildren(String path);

	boolean checkExists(String path);

	void delete(String path);

	CompletableFuture<String> asyncGet(String path);

	CompletableFuture<Void> asyncCreate(String path, CreateMode mode);

	CompletableFuture<Void> asyncCreate(String path, String content, CreateMode mode);

	CompletableFuture<Void> asyncUpdate(String path, String content);

	CompletableFuture<List<String>> asyncGetChildren(String path);

	CompletableFuture<Boolean> asyncCheckExists(String path);

	CompletableFuture<Void> asyncDelete(String path);

	Lock newLock(String path);
}
