package com.sample.tools.plugin.plugins.codegen;

import java.util.concurrent.CompletableFuture;

import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.swak.fx.support.Display;
import com.swak.fx.support.FXMLView;
import com.swak.fx.support.Window;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * 页面
 * 
 * @author lifeng
 * @date 2020年6月2日 下午3:57:40
 */
/**
 * 设置页面
 * 
 * @author lifeng
 */
@FXMLView(title = "开发工具.代码生成", value = "Page.fxml", css = { "Page.css" }, stageStyle = "TRANSPARENT")
public class Page extends Window {

	@FXML
	private TextArea sqlScript;
	@FXML
	private TextField packageName;
	@FXML
	private TextField entityName;
	@FXML
	private Button buildBtn;

	@FXML
	public void initialize() {
		sqlScript.textProperty().addListener((observable, oldValue, newValue) -> {
			this.parseSql(newValue);
		});
		buildBtn.setOnAction((actionEvent) -> {
			System.out.println("点击事件");
		});
		super.initialize();
	}

	/**
	 * 解析Sql
	 */
	private void parseSql(String sql) {
		SQLCreateTableParser parser = new SQLCreateTableParser(sql);
		parser.parseCreateTable();
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
		}).thenAccept((v) -> {
			Display.runUI(() -> {
				super.onClose(evt);
			});
		});
	}
}
