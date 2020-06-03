package com.sample.tools.page;

import java.io.File;

import com.sample.tools.config.Patch;
import com.sample.tools.operation.OpsFile;
import com.sample.tools.operation.ops.BackupOps;
import com.sample.tools.operation.ops.PatchOps;
import com.swak.fx.support.Dialogs;
import com.swak.fx.support.Display;
import com.swak.fx.support.FXMLView;
import com.swak.fx.support.Window;
import com.swak.ui.Event;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * 启动界面
 * 
 * @author lifeng
 */
@FXMLView(title = "开发工具", value = "UpgraderPage.fxml", css = { "/css/bootstrapfx.css",
		"UpgraderPage.css" }, stageStyle = "TRANSPARENT")
public class UpgraderPage extends Window {

	@FXML
	Button patchBtn;
	@FXML
	Button backupBtn;
	@FXML
	Button startBtn;
	File initFile = null;

	// 是否正在操作
	private ReadOnlyBooleanWrapper loading = new ReadOnlyBooleanWrapper(this, "loading");

	@FXML
	public void initialize() {
		this.patchBtn.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("开发工具.添加补丁文件");
			fileChooser.setInitialDirectory(initFile);
			fileChooser.setSelectedExtensionFilter(
					new ExtensionFilter("开发工具补丁文件", new String[] { "*.zip", "*.ZIP*" }));
			File file = fileChooser.showOpenDialog(Display.getStage());
			if (file != null) {
				this.initFile = file.getParentFile();
				this.loading.setValue(true);
				LoadingPage loading = LoadingPage.open("添加补丁中...");
				new PatchOps().doOps(OpsFile.ops(Patch.newPatch(file)));
				loading.close().thenAccept((v) -> {
					this.loading.setValue(false);
				});
			}
		});
		this.backupBtn.setOnAction(event -> {
			this.loading.setValue(true);
			LoadingPage loading = LoadingPage.open("备份中...");
			new BackupOps().doOps(null);
			loading.close().thenAccept((v) -> {
				this.loading.setValue(false);
			});
		});
		this.startBtn.setOnAction(event -> {
			ButtonType result = Dialogs.confirm("提醒", "确定启动服务?", ButtonType.YES, ButtonType.CANCEL);
			if (result == ButtonType.YES) {
				Display.getEventBus().post(Event.START);
			}
		});
		this.loading.getReadOnlyProperty().addListener((observable, oldValue, newValue) -> {
			this.patchBtn.setDisable(newValue);
			this.backupBtn.setDisable(newValue);
			this.startBtn.setDisable(newValue);
		});
		super.initialize();
	}

	/**
	 * 关闭
	 */
	@FXML
	public void onClose(MouseEvent evt) {
		if (!this.loading.get()) {
			Display.runUI(() -> {
				this.onHide(evt);
				Display.getEventBus().post(Event.EXIT);
			});
		}
	}
}
