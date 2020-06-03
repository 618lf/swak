package com.sample.tools.page;

import java.util.concurrent.CompletableFuture;

import com.sample.tools.operation.ops.BackupOps;
import com.sample.tools.operation.ops.ClearOps;
import com.swak.fx.support.AbstractPage;
import com.swak.fx.support.Display;
import com.swak.fx.support.FXMLView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * 备份界面
 * 
 * @author lifeng
 */
@FXMLView(title = "开发工具", value = "BackupPage.fxml", css = "Backup.css", stageStyle = "TRANSPARENT")
public class BackupPage extends AbstractPage {

	@FXML
	private VBox root;
	@FXML
	private ImageView loading;
	@FXML
	private Label title;
	private volatile Thread thread;

	@FXML
	public void initialize() {
		loading.setImage(new Image(getClass().getResource("loading.gif").toExternalForm()));
		title.setText("数据备份中,请稍等...");
		this.start();
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

			// 执行备份和清理
			this.backup();
			this.clear();

			// 结束
			this.finish();
		});
		thread.setDaemon(true);
		thread.start();
	}

	// 备份数据问题
	private void backup() {
		new BackupOps().doOps(null);
	}

	// 清除临时文件
	private void clear() {
		Display.runUI(() -> {
			title.setText("临时数据清理中,请稍等...");
		});
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		new ClearOps().doOps(null);
	}

	// 结束
	private void finish() {
		Display.runUI(() -> {
			title.setText("清理完成,准备关闭系统");
		});
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
			return closeFuture;
		});
	}
}