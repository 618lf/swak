package com.swak.tools.page;

import java.io.File;
import java.nio.file.Files;

import com.swak.fx.support.Display;
import com.swak.fx.support.FXMLView;
import com.swak.fx.support.Notifys;
import com.swak.fx.support.Window;
import com.swak.tools.config.Patch;
import com.swak.tools.config.Settings;
import com.swak.tools.config.Version;
import com.swak.tools.operation.OpsFile;
import com.swak.tools.operation.ops.PackOps;
import com.swak.ui.Event;
import com.swak.utils.BigDecimalUtil;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

/**
 * 启动界面
 * 
 * @author lifeng
 */
@FXMLView(title = "深大穿戴.陀螺仪", value = "PackagerPage.fxml", css = { "/css/bootstrapfx.css",
		"PackagerPage.css" }, stageStyle = "TRANSPARENT")
public class PackagerPage extends Window {

	@FXML
	Button dirBtn;
	@FXML
	Button initBtn;
	@FXML
	Button patchBtn;
	@FXML
	Label dir;
	String patchPrev = "shenda.patch";

	/**
	 * 制作的目录
	 */
	File makeFile = null;

	/**
	 * 补丁目录
	 */
	File patchFile = null;

	/**
	 * 是否正在操作
	 */
	private ReadOnlyBooleanWrapper loading = new ReadOnlyBooleanWrapper(this, "loading");

	@FXML
	public void initialize() {
		this.dirBtn.setOnAction(event -> {
			DirectoryChooser fileChooser = new DirectoryChooser();
			fileChooser.setTitle("深大穿戴.陀螺仪.选择目录");
			if (this.makeFile != null) {
				fileChooser.setInitialDirectory(this.makeFile);
			}
			this.makeFile = fileChooser.showDialog(Display.getStage());
			this.dir.setText(this.makeFile.getAbsolutePath());
		});
		this.initBtn.setOnAction(event -> {
			this.createStructure();
		});
		this.patchBtn.setOnAction(event -> {
			if (this.patchFile != null) {
				this.loading.setValue(true);
				LoadingPage loading = LoadingPage.open("打包中...");
				new PackOps().doOps(OpsFile.ops(Patch.newPatch(this.patchFile, this.patchPrev)));
				loading.close().thenAccept((v) -> {
					this.loading.setValue(false);
				});
			} else {
				Notifys.error("提醒", "请选择目录并初始化结构！");
			}
		});
		this.loading.getReadOnlyProperty().addListener((observable, oldValue, newValue) -> {
			this.dirBtn.setDisable(newValue);
			this.patchBtn.setDisable(newValue);
		});
		super.initialize();
	}

	/**
	 * 创建结构
	 */
	private void createStructure() {
		if (this.makeFile == null) {
			Notifys.error("提醒", "请选择一个目录！");
			return;
		}

		File file = new File(this.makeFile, patchPrev);
		if (file.exists()) {
			int i = 0;
			while (true) {
				file = new File(this.makeFile, patchPrev + "（" + i + "）");
				if (file.exists()) {
					i++;
					continue;
				}
				break;
			}
		}

		// 创建跟目录
		file.mkdirs();
		this.patchFile = file;
		new File(this.patchFile, OpsFile.SQL).mkdir();
		new File(this.patchFile, OpsFile.JAR).mkdir();
		new File(this.patchFile, OpsFile.LIB).mkdir();
		new File(this.patchFile, OpsFile.STATIC).mkdir();
		new File(this.patchFile, OpsFile.CONFIG).mkdir();
		this.createVersion(this.patchFile);
		this.dir.setText(this.patchFile.getAbsolutePath());
	}

	// 生成版本文件
	protected void createVersion(File makeDir) {
		File version = new File(makeDir, OpsFile.VER);
		if (version.exists()) {
			return;
		}
		try {

			// 项目配置
			Version _version = Settings.me().getVersion();

			// 当前的版本
			Double curr = _version.getVersion();

			// 当前版本累加
			curr = curr == null ? 0.1 : (curr + 0.1);

			// 写入文件
			version.createNewFile();
			StringBuilder info = new StringBuilder();
			info.append("min:").append("\r\n");
			info.append("cur:").append(BigDecimalUtil.floor(curr, 1)).append("\r\n");
			Files.write(version.toPath(), info.toString().getBytes("utf-8"));

			// 保存配置
			Settings.me().getVersion().setVersion(curr);
			Settings.me().storeVersion();
		} catch (Exception e) {
		}
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
