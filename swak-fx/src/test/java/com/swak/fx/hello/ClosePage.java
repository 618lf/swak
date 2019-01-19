package com.swak.fx.hello;

import java.util.concurrent.CompletableFuture;

import com.swak.fx.AbstractPage;
import com.swak.fx.FXMLView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * 界面
 * 
 * @author lifeng
 */
@FXMLView(title = "个税易客户端", value = "/fxml/CloseScreen.fxml", css = "/css/ClosePage.css", stageStyle = "TRANSPARENT")
public class ClosePage extends AbstractPage {

	@FXML
	private VBox root;
	@FXML
	private ImageView loading;
	@FXML
	private Label title;

	@FXML
	private volatile Thread thread;
	private volatile boolean finish;

	@FXML
	public void initialize() {
		loading.setImage(new Image(getClass().getResource("/images/loading.gif").toExternalForm()));
		title.setText("数据备份中,清稍等...");
		this.start();
		super.initialize();
	}

	/**
	 * 开始
	 */
	private void start() {
		finish = false;
		thread = new Thread(() -> {
			while (!finish) {
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
		finish = true;
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
		}
		closeFuture.complete(null);
	}

	/**
	 * 结束
	 */
	@Override
	public CompletableFuture<Void> waitClose() {
		return initFuture.thenCompose((v) -> {
			thread.interrupt();
			return closeFuture;
		});
	}
}
