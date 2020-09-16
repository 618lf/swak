package com.swak.zookeeper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Zookeeper 服务
 * 
 * @author lifeng
 * @date 2020年9月15日 下午8:50:08
 */
public interface ZookeeperService {

	void addStateListener(StateListener listener);

	void removeStateListener(StateListener listener);

	List<String> addChildListener(String path, final ChildListener listener);

	void removeChildListener(String path, ChildListener listener);

	void addDataListener(String path, DataListener listener);

	void addDataListener(String path, DataListener listener, Executor executor);

	void removeDataListener(String path, DataListener listener);

	String get(String path);

	void create(String path, boolean ephemeral);

	void create(String path, String content, boolean ephemeral);

	List<String> getChildren(String path);

	boolean checkExists(String path);

	void delete(String path);

	CompletableFuture<String> asyncGet(String path);

	CompletableFuture<Void> asyncCreate(String path, boolean ephemeral);

	CompletableFuture<Void> asyncCreate(String path, String content, boolean ephemeral);

	CompletableFuture<List<String>> asyncGetChildren(String path);

	CompletableFuture<Boolean> asyncCheckExists(String path);

	CompletableFuture<Void> asyncDelete(String path);
}
