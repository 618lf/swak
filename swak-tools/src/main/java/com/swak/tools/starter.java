package com.swak.tools;

import java.awt.SystemTray;
import java.util.concurrent.CompletableFuture;

import com.swak.fx.support.AbstractApplication;
import com.swak.fx.support.Display;
import com.swak.tools.config.Settings;
import com.swak.tools.operation.cmd.StarterCommand;
import com.swak.tools.page.PackagerPage;
import com.swak.ui.Event;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class starter extends AbstractApplication {

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
		});
	}

	@Override
	protected CompletableFuture<Void> stop(Stage stage) {
		return CompletableFuture.runAsync(() -> {
		});
	}

	@Override
	protected void customStage(Stage stage, SystemTray tray) {
		stage.setTitle("深大穿戴.陀螺仪");
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
		launch(starter.class, PackagerPage.class, args);
	}
}
