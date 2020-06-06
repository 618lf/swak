package com.sample.tools.page;

import java.util.concurrent.CompletableFuture;

import com.sample.tools.config.Settings;
import com.swak.fx.support.Dialogs;
import com.swak.fx.support.Display;
import com.swak.fx.support.FXMLView;
import com.swak.fx.support.Notifys;
import com.swak.fx.support.Window;
import com.swak.ui.Event;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;

/**
 * 设置页面
 * 
 * @author lifeng
 */
@FXMLView(title = "开发工具", value = "SettingsPage.fxml", css = { "SettingsPage.css" }, stageStyle = "TRANSPARENT")
public class SettingsPage extends Window {

	@FXML
	private Hyperlink copyOps;
	@FXML
	private Hyperlink upgraderOps;
	@FXML
	private Label serverVersion;
	@FXML
	private Label serverHost;
	@FXML
	private TextArea accessIps;
	@FXML
	private CheckBox accessAble;
	@FXML
	private CheckBox runtimeMode;

	@FXML
	public void initialize() {
		this.serverHost.setText(Settings.me().getServer());
		this.serverVersion.setText(new StringBuilder().append(Settings.me().getVersion().getName()).append(".")
				.append(Settings.me().getVersion().getVersion()).append(" ")
				.append(Settings.me().getVersion().getDescribe()).toString());
		this.copyOps.setOnMouseClicked(event -> {
			ClipboardContent clipboardContent = new ClipboardContent();
			clipboardContent.putString(this.serverHost.getText());
			Display.getClipboard().setContent(clipboardContent);
			Notifys.info("提醒", "地址复制成功，请在浏览器中访问");
		});
		this.upgraderOps.setOnMouseClicked(event -> {
			ButtonType result = Dialogs.confirm("提醒", "系统升级需要停止服务器，确认现在升级吗?", ButtonType.YES, ButtonType.CANCEL);
			if (result == ButtonType.YES) {
				this.onHide(null);
				Display.getScene().getWindow().hide();
				Display.getEventBus().post(Event.UPGRADE);
			}
		});
		this.accessAble.selectedProperty().addListener((observable, oldValue, newValue) -> {
			Notifys.info("提醒", (newValue ? "开启" : "关闭") + "系统远程访问成功！");
		});
		this.runtimeMode.selectedProperty().addListener((observable, oldValue, newValue) -> {
			Notifys.info("提醒", "系统切换到" + (newValue ? "服务器" : "客户端") + "模式！");
			if (newValue) {
				ClipboardContent clipboardContent = new ClipboardContent();
				clipboardContent.putString(this.serverHost.getText());
				Display.getClipboard().setContent(clipboardContent);
				Notifys.info("提醒", "地址复制成功，请在浏览器中访问");
			}
		});
		this.accessIps.textProperty().addListener((observable, oldValue, newValue) -> {
		});
		super.initialize();
	}

	/**
	 * 等待页面关闭
	 */
	public CompletableFuture<Void> waitClose() {
		if (!closeFuture.isDone()) {
			closeFuture.complete(null);
		}
		return initFuture.thenCompose((v) -> {
			return closeFuture;
		});
	}

	/**
	 * 关闭
	 */
	@Override
	public void onClose(MouseEvent evt) {
		this.waitClose().thenAcceptAsync((v) -> {
			Settings.me().storeConfig();
		}).thenAccept((v) -> {
			Display.runUI(() -> {
				super.onClose(evt);
			});
		});
	}
}