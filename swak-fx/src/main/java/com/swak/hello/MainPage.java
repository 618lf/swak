package com.swak.hello;

import com.swak.fx.support.Display;
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
@FXMLView(title = "个税易客户端", value = "/fxml/MainPage.fxml", css = "/css/MainPage.css", stageStyle = "TRANSPARENT")
public class MainPage extends Window {

	@FXML
	private WebView webView;
	private WebEngine browser;

	@FXML
	public void initialize() {
		super.initialize();
		double w = 1200;
		double h = 1200 / 16 * 9;
		if (h > Display.getScreen().getVisualBounds().getHeight()) {
			h = Display.getScreen().getVisualBounds().getHeight() * 0.8;
		}
		this.root.setPrefWidth(w);
		this.root.setPrefHeight(h);
		browser = webView.getEngine();
		browser.load("http://www.baidu.com");
	}
}
