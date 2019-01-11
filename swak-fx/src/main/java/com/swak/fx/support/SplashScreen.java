package com.swak.fx.support;

import java.util.concurrent.CompletableFuture;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * 界面
 * 
 * @author lifeng
 */
@FXMLView(title = "个税易客户端", value = "/fxml/SplashScreen.fxml", css = "/css/splash.css", stageStyle = "TRANSPARENT")
public class SplashScreen extends AbstractPage {

	@FXML
	private VBox root;
	@FXML
	private ImageView logo;
	@FXML
	private ProgressBar progress;
	private volatile Thread thread;
	private volatile int times;
	private volatile boolean finish;

	@FXML
	public void initialize() {
		logo.setImage(new Image(getClass().getResource("/images/logo.png").toExternalForm()));
		this.start();
	}

	/**
	 * 开始
	 */
	public void start() {
		finish = false;
		times = 0;
		Display.runUI(() -> {
			this.progress.setProgress(0);
		});
		thread = new Thread(() -> {
			while (!finish) {
				Display.runUI(() -> {
					double selection = this.progress.getProgress();
					if (selection <= 0.5) {
						this.progress.setProgress(selection + 0.05);
					} else if (selection <= 0.75) {
						times++;
						if (times >= 5) {
							this.progress.setProgress(selection + 0.03);
							times = 0;
						}
					} else if (selection <= 0.95) {
						times++;
						if (times >= 10) {
							this.progress.setProgress(selection + 0.01);
							times = 0;
						}
					}
				});
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					break;
				}
			}
			this.finish();
		});
		thread.setDaemon(true);
		thread.start();
	}

	// 结束
	private void finish() {
		Display.runUI(() -> {
			this.progress.setProgress(1);
			finish = true;
		});
		try {
			Thread.sleep(200);
		} catch (Exception e) {
		}
		closeFuture.complete(Boolean.TRUE);
	}

	/**
	 * 结束
	 */
	@Override
	public CompletableFuture<Boolean> close() {
		if (thread != null) {
			thread.interrupt();
		}
		return closeFuture;
	}
}
