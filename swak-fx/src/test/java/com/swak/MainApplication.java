package com.swak;

import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import org.springframework.context.ConfigurableApplicationContext;

import com.swak.fx.support.AbstractApplication;
import com.swak.fx.support.Display;
import com.swak.fx.support.Event;
import com.swak.hello.ClosePage;
import com.swak.hello.MainPage;
import com.swak.hello.SplashPage;
//import com.tmt.AppRunner;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * 启动服务
 * 
 * @author lifeng
 */
public class MainApplication extends AbstractApplication {

	TrayIcon trayIcon;
	ConfigurableApplicationContext applicationContext;

	/**
	 * 启动服务
	 */
	@Override
	protected CompletableFuture<Void> start(String[] savedArgs) {
		return CompletableFuture.runAsync(() -> {
			// this.applicationContext = Application.run(AppRunner.class, savedArgs);
		});
	}

	/**
	 * 停止服务
	 */
	@Override
	protected CompletableFuture<Void> stop(Stage stage) {
		CompletableFuture<Void> stopFuture = new CompletableFuture<>();
		CompletableFuture<Void> backupFuture = this.backup();
		CompletableFuture.runAsync(() -> {
			Application.stop();
		}).thenAcceptBothAsync(backupFuture, (v1, v2) -> {
			stopFuture.complete(null);
		});
		return stopFuture;
	}

	/**
	 * 启动
	 * @param stage
	 * @return
	 */
	protected CompletableFuture<Void> backup() {
		CompletableFuture<Void> closeFuture = new CompletableFuture<>();
		ClosePage closePage = new ClosePage();
		Display.runUI(() -> {
			Stage closePageState = closePage.openOn(null);
			closePage.waitClose().whenComplete((v, t) -> {
				Display.runUI(() -> {
					closePageState.close();
				});
				closeFuture.complete(null);
			});
		});
		return closeFuture;
	}

	/**
	 * 整个程序的定义
	 */
	@Override
	protected void customStage(Stage stage, SystemTray tray) {

		// 设置主程序
		stage.setTitle("个税易客户端");
		stage.getIcons().add(new Image(Display.load("/images/logo.png").toExternalForm()));
		stage.sizeToScene();
		stage.setOnCloseRequest(event -> {// 只能拦截默认的行为
			Display.getEventBus().post(Event.CLOSE);
			event.consume();
		});

		// 设置托盘(不支持1.8)
		if (tray != null && System.getProperty("java.version").compareTo("1.9") > 0) {
			this.enableTray(stage, tray);
		}
	}

	/**
	 * 如果开启这个，则需要 Platform.exit(); 才能退出
	 * 
	 * @param stage
	 * @param tray
	 */
	protected void enableTray(final Stage stage, final SystemTray tray) {
		PopupMenu popupMenu = new PopupMenu();
		java.awt.MenuItem openItem = new java.awt.MenuItem("显示");
		java.awt.MenuItem quitItem = new java.awt.MenuItem("退出");
		openItem.addActionListener(e -> {
			SystemTray.getSystemTray().remove(trayIcon);
			try {
				this.stop();
			} catch (Exception e1) {
			}
			Platform.exit();
		});
		quitItem.addActionListener(e -> {
			SystemTray.getSystemTray().remove(trayIcon);
			Display.runUI(() -> stage.show());
		});
		popupMenu.add(openItem);
		popupMenu.add(quitItem);
		try {
			Platform.setImplicitExit(false);
			BufferedImage image = ImageIO.read(Display.class.getResourceAsStream("/images/logo.png"));
			TrayIcon trayIcon = new TrayIcon(image, "个税易客户端", popupMenu);
			trayIcon.setToolTip("个税易客户端");
			tray.add(trayIcon);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 启动系统
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Platform.setImplicitExit(false);
		launch(MainApplication.class, MainPage.class, SplashPage.class, args);
	}
}