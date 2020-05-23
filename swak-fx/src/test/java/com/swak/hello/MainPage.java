package com.swak.hello;

import com.swak.fx.support.App;
import com.swak.fx.support.Dialogs;
import com.swak.fx.support.Display;
import com.swak.fx.support.DownloadPane;
import com.swak.fx.support.FXMLView;
import com.swak.fx.support.Window;
import com.swak.ui.Event;
import com.swak.ui.EventListener;

import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

/**
 * 界面
 * 
 * @author lifeng
 */
@FXMLView(title = "个税易客户端", value = "/fxml/MainPage.fxml", css = "/css/MainPage.css", stageStyle = "TRANSPARENT")
public class MainPage extends Window implements EventListener {

	@FXML
	private DownloadPane download;
	@FXML
	private WebView webView;
	private WebEngine browser;
	private Worker<Void> worker;
	private App app;

	@FXML
	public void initialize() {
		double w = 1200;
		double h = 1200 / 16 * 9;
		if (h > Display.getScreen().getVisualBounds().getHeight()) {
			h = Display.getScreen().getVisualBounds().getHeight() * 0.8;
		}
		this.app = new MainApp();
		this.root.setPrefWidth(w);
		this.root.setPrefHeight(h);
		browser = webView.getEngine();
		browser.load("http://192.168.0.16:8083");
		worker = browser.getLoadWorker();
		worker.stateProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == State.SUCCEEDED) {
				JSObject win = (JSObject) browser.executeScript("window");
				win.setMember("$App", app);
			}
		});
		Display.getEventBus().register(this);
		super.initialize();
	}
	
    /**
     * 关闭页面
     */
	@FXML
	@Override
	public void onClose(MouseEvent evt) {
		Display.runUI(() -> {
			ButtonType res = Dialogs.confirm("提醒", "确认关闭？", ButtonType.YES, ButtonType.NO);
			if (res == ButtonType.YES) {
				this.onHide(evt);
				Display.getEventBus().post(Event.EXIT);
			}
		});
	}

	/**
	 * 监听事件
	 */
	@Override
	public void listen(Event event) {
		if (event.is(Event.DOWNLOAD)) {
			download.download("张国荣图片张国荣图片张国荣图片.jpg", "http://192.168.0.16:8083/static/img/zgr.jpg");
		} else if (event.is(Event.CLOSE)) {
			this.onClose(null);
		}
	}
}
