package com.sample.tools.page;

import java.util.concurrent.CompletableFuture;

import com.swak.fx.support.AbstractPage;
import com.swak.fx.support.Display;
import com.swak.fx.support.FXMLView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * 操作中的界面
 * 
 * @author lifeng
 */
@FXMLView(title = "开发工具", value = "LoadingPage.fxml", css = "Loading.css", stageStyle = "TRANSPARENT")
public class LoadingPage extends AbstractPage {

	private Stage _LOADING = null;

	@FXML
	private VBox root;
	@FXML
	private ImageView loading;
	@FXML
	private Label title;
	private volatile Thread thread;

	// use static open()
	private LoadingPage() {
	}

	@FXML
	public void initialize() {
		loading.setImage(new Image(getClass().getResource("loading.gif").toExternalForm()));
		title.setText("加载中...");
		super.initialize();
	}

	/**
	 * 开始
	 */
	private void start() {
		thread = new Thread(() -> {
			// 等待服务结束
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
			}
			// 置为完成
			Display.runUI(() -> {
				loading.setImage(new Image(getClass().getResource("成功.png").toExternalForm()));
				title.setText("操作成功");
			});
			// 等待服务结束
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
			}
			// 结束
			closeFuture.complete(null);
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * 等待页面关闭
	 */
	public CompletableFuture<Void> waitClose() {
		this.start();
		return initFuture.thenCompose((v) -> {
			return closeFuture;
		});
	}

	/**
	 * 显示
	 */
	protected void show(String text) {
		if (_LOADING == null) {
			_LOADING = this.openOn(Display.getStage());
			_LOADING.getIcons().add(new Image(Display.load(LoadingPage.class, "logo.png").toExternalForm()));
			_LOADING.sizeToScene();
			_LOADING.setOnCloseRequest(event -> {
				event.consume();
			});
		} else {
			_LOADING.show();
		}
		title.setText(text);
	}

	/**
	 * 关闭
	 */
	public CompletableFuture<Void> close() {
		return waitClose().thenAccept(v -> {
			Display.runUI(() -> {
				_LOADING.close();
			});
		});
	}

	/**
	 * 打开一个 loading 框
	 * 
	 * @param text
	 * @return
	 */
	public static LoadingPage open(String text) {
		LoadingPage loadingPage = new LoadingPage();
		loadingPage.show(text);
		return loadingPage;
	}
}