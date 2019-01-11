package com.swak.hello;

import com.swak.fx.support.FXMLView;
import com.swak.fx.support.Window;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * 界面
 * 
 * @author lifeng
 */
@FXMLView(title = "个税易客户端", value = "/fxml/MainPage.fxml", css = "/css/bootstrapfx.css", stageStyle = "TRANSPARENT")
public class MainPage extends Window {

	@FXML
	private WebView webView;
	private WebEngine browser;

	@FXML
	public void initialize() {
		browser = webView.getEngine();
		browser.load("http://www.baidu.com");
	}
}
