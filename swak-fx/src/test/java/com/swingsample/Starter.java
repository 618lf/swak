package com.swingsample;

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import com.swak.swing.support.AbstractApplication;
import com.swak.swing.support.AbstractPage;
import com.swak.swing.support.Display;

/**
 * 启动
 * 
 * @author lifeng
 * @date 2020年5月21日 上午11:02:00
 */
public class Starter extends AbstractApplication {

	TrayIcon trayIcon;

	@Override
	protected CompletableFuture<Void> start(String[] savedArgs) {
		return CompletableFuture.runAsync(() -> {
			System.out.println("开始启动...");
			try {
				Thread.sleep(100000);
			} catch (InterruptedException e) {
			}
			System.out.println("启动成功...");
		});
	}

	@Override
	protected CompletableFuture<Void> stop() {
		CompletableFuture<Void> stopFuture = new CompletableFuture<>();
		CompletableFuture<Void> backupFuture = this.backup();
		CompletableFuture.runAsync(() -> {
		}).thenAcceptBothAsync(backupFuture, (v1, v2) -> {
			stopFuture.complete(null);
		});
		return stopFuture;
	}

	/**
	 * 启动
	 * 
	 * @param stage
	 * @return
	 */
	protected CompletableFuture<Void> backup() {
		CompletableFuture<Void> closeFuture = new CompletableFuture<>();
		ClosePage closePage = new ClosePage();
		Display.runUI(() -> {
			closePage.show();
			closePage.waitClose().whenComplete((v, t) -> {
				Display.runUI(() -> {
					closePage.close();
				});
				closeFuture.complete(null);
			});
		});
		return closeFuture;
	}

	@Override
	protected void customStage(AbstractPage page, SystemTray tray) {
		if (tray != null) {
			this.enableTray(page, tray);
		}
	}

	/**
	 * 如果开启这个，则需要 Platform.exit(); 才能退出
	 * 
	 * @param stage
	 * @param tray
	 */
	protected void enableTray(final AbstractPage page, final SystemTray tray) {
		try {
			BufferedImage image = ImageIO.read(Display.class.getResourceAsStream("/images/logo.png"));
			trayIcon = new TrayIcon(image, "个税易客户端");
			trayIcon.setImageAutoSize(true);
			trayIcon.setToolTip("个税易客户端");
			trayIcon.addActionListener((event) -> {
				page.show();
			});
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
		launch(Starter.class, MainPage.class, SplashPage.class, args);
	}
}
