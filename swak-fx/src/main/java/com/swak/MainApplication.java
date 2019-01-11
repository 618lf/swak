package com.swak;

import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.swak.fx.support.AbstractApplication;
import com.swak.fx.support.Display;
import com.swak.hello.MainPage;

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

	/**
	 * 启动内部服务
	 */
	@Override
	protected <T> T start(String[] savedArgs) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 停止服务
	 */
	@Override
	public void stop() throws Exception {
		super.stop();
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
		launch(MainApplication.class, MainPage.class, args);
	}
}