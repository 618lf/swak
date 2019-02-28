package com.tmt.page;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.io.Resources;
import com.swak.fx.support.Dialogs;
import com.swak.fx.support.Display;
import com.swak.fx.support.Event;
import com.swak.fx.support.EventListener;
import com.swak.fx.support.FXMLView;
import com.swak.fx.support.Window;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * 界面
 * 
 * @author lifeng
 */
@FXMLView(title = "个税易客户端", value = "MainPage.fxml", css = "MainPage.css", stageStyle = "TRANSPARENT")
public class MainPage extends Window implements EventListener {

	@FXML
	private WebView webView;
	private WebEngine browser;
	private Stage settings;

	@FXML
	public void initialize() {
		double h = Display.getScreen().getVisualBounds().getHeight() * 0.93;
		double w = h * 1.66;
		this.root.setPrefWidth(w);
		this.root.setPrefHeight(h);
		browser = webView.getEngine();
		browser.loadContent(this.loading());
		Display.getEventBus().register(this);
		super.initialize();
	}

	// 加载中的过渡页面
	private String loading() {
		StringBuilder welcome = new StringBuilder();
		try {
			URL url = MainPage.class.getResource("Welcome.html");
			List<String> lines = Resources.readLines(url, StandardCharsets.UTF_8);
			for (String line : lines) {
				welcome.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
			welcome.append("数据加载中...");
		}
		return welcome.toString();
	}

	/**
	 * 关闭
	 */
	@FXML
	public void onClose(MouseEvent evt) {
		Display.runUI(() -> {
			ButtonType res = Dialogs.confirm("提醒", "确认关闭？", ButtonType.YES, ButtonType.NO);
			if (res == ButtonType.YES) {
				this.onHide(evt);
				if (settings != null) {
					settings.close();
				}
				Display.getEventBus().post(Event.EXIT);
			}
		});
	}

	/**
	 * 监听事件
	 */
	@Override
	public void listen(Event event) {
		if (event.is(Event.CLOSE)) {
			this.onClose(null);
		} else if (event.is(Event.URL)) {
			String url = event.getMessage();
			Display.runUI(() -> {
				browser.load(url);
			});
		}
	}
}