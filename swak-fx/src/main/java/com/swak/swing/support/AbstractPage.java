package com.swak.swing.support;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基础的页面
 * 
 * @author lifeng
 * @date 2020年5月21日 上午10:35:04
 */
public abstract class AbstractPage {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractPage.class);

	protected CompletableFuture<Void> initFuture = new CompletableFuture<>();
	protected CompletableFuture<Void> closeFuture = new CompletableFuture<>();

	/**
	 * 初始化
	 */
	public AbstractPage() {
		this.initialize();
	}

	/**
	 * 顶层初始化
	 */
	public void initialize() {
		this.initFuture.complete(null);
	}

	/**
	 * 界面显示
	 */
	public abstract void show();

	/**
	 * 界面关闭
	 */
	public abstract void close();

	/**
	 * 等待页面关闭
	 */
	public CompletableFuture<Void> waitClose() {
		if (!closeFuture.isDone()) {
			closeFuture.complete(null);
		}
		return initFuture.thenCompose((v) -> {
			return closeFuture;
		});
	}

	/**
	 * 当初始化之后需要处理
	 * 
	 * @return
	 */
	public CompletableFuture<Void> whenInited() {
		return this.initFuture;
	}

	/**
	 * 当结束之后需要处理
	 * 
	 * @return
	 */
	public CompletableFuture<Void> whenClosed() {
		return this.closeFuture;
	}
}
