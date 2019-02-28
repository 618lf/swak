package com.tmt;

import java.awt.SystemTray;
import java.util.concurrent.CompletableFuture;

import com.swak.Application;
import com.swak.MainApp;
import com.swak.fx.support.AbstractApplication;
import com.swak.fx.support.Display;
import com.swak.fx.support.Event;
import com.swak.hello.SplashPage;
import com.tmt.page.MainPage;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * 测试启动
 * 
 * @author lifeng
 */
public class Starter extends AbstractApplication {

	/**
	 * 执行监听关闭
	 */
	@Override
	public void listen(Event event) {
		if (event == Event.EXIT) {
			this.stop(Display.getStage()).whenComplete((v, t) -> {
				Platform.exit();
			});
		} else if (event == Event.UPGRADE) {
			this.stop(Display.getStage()).whenComplete((v, t) -> {
				Platform.exit();
			});
		}
	}

	/**
	 * 启动服务
	 */
	@Override
	protected CompletableFuture<Void> start(String[] savedArgs) {
		return CompletableFuture.runAsync(() -> {
			Application.run(AppRunner.class, savedArgs);
		});
	}

	/**
	 * 停止服务
	 */
	@Override
	protected CompletableFuture<Void> stop(Stage stage) {
		CompletableFuture<Void> stopFuture = new CompletableFuture<>();
		CompletableFuture.runAsync(() -> {
			Application.stop();
		}).thenAccept(v -> {
			stopFuture.complete(null);
		});
		return stopFuture;
	}

	/**
	 * 整个程序的定义
	 */
	@Override
	protected void customStage(Stage stage, SystemTray tray) {
		stage.setTitle("个税易客户端");
		stage.getIcons().add(new Image(Display.load(MainApp.class, "logo.png").toExternalForm()));
		stage.sizeToScene();
		stage.setOnCloseRequest(event -> {
			Display.getEventBus().post(Event.CLOSE);
			event.consume();
		});
	}

	/**
	 * 发送要显示的地址
	 */
	@Override
	protected void postInitialized() {
		String server = Application.getAddresses();
		Display.getEventBus().post(Event.URL.message(server));
	}

	/**
	 * 启动系统
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Platform.setImplicitExit(false);
		launch(Starter.class, MainPage.class, SplashPage.class, args);
	}
}
