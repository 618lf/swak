package com.sample.tools;

import java.awt.SystemTray;
import java.util.concurrent.CompletableFuture;

import com.sample.tools.config.Settings;
import com.sample.tools.operation.cmd.StarterCommand;
import com.sample.tools.page.MainPage;
import com.sample.tools.page.PackagerPage;
import com.swak.Application;
import com.swak.fx.support.AbstractApplication;
import com.swak.fx.support.Display;
import com.swak.ui.Event;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * 启动脚本
 * 
 * @author lifeng
 * @date 2020年6月2日 下午3:15:57
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
		} else if (event == Event.START) {
			this.stop(Display.getStage()).whenComplete((v, t) -> {
				new StarterCommand().exec();
				Platform.exit();
			});
		}
	}

	@Override
	protected CompletableFuture<Void> start(String[] savedArgs) {
		return CompletableFuture.runAsync(() -> {
			Application.run(AppRunner.class, savedArgs);
		});
	}

	@Override
	protected CompletableFuture<Void> stop(Stage stage) {
		return CompletableFuture.runAsync(() -> {
			Application.stop();
		});
	}

	@Override
	protected void customStage(Stage stage, SystemTray tray) {
		stage.setTitle("开发工具");
		stage.getIcons().add(new Image(Display.load(PackagerPage.class, "logo.png").toExternalForm()));
		stage.sizeToScene();
		stage.setOnCloseRequest(event -> {
			Display.getEventBus().post(Event.CLOSE);
			event.consume();
		});
	}

	/**
	 * 启动系统
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Settings.intSettings();
		Platform.setImplicitExit(false);
		launch(Starter.class, MainPage.class, args);
	}
}
