package com.swak.fx.hello;

import com.swak.fx.AbstractPage;
import com.swak.fx.Display;
import com.swak.fx.FXMLView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * 界面
 * 
 * @author lifeng
 */
@FXMLView(title="个税易客户端", value = "/fxml/Hello.fxml", css = "/css/bootstrapfx.css", stageStyle = "TRANSPARENT")
public class HelloPage extends AbstractPage {

	@FXML
	private HBox tools;

	@FXML
	private WebView webView;
	private WebEngine browser;
	@FXML
	private StackPane stackPane;

	@FXML
	private VBox logs;
	@FXML
	private ImageView btn0;
	@FXML
	private ImageView btn1;
	@FXML
	private ImageView btn2;
	@FXML
	private ImageView btn3;
	@FXML
	private ImageView btn4;
	@FXML
	private Label label;

	@FXML
	public void initialize() {
		label.setText("个税易客户端");
		browser = webView.getEngine();
		browser.load("http://www.baidu.com");
		btn1.setOnMouseClicked(event -> {
			Display.getStage().hide();
		});
	}
}
