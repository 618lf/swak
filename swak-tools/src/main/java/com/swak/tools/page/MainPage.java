package com.swak.tools.page;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.google.common.io.Resources;
import com.swak.fx.support.App;
import com.swak.fx.support.Dialogs;
import com.swak.fx.support.Display;
import com.swak.fx.support.DownloadPane;
import com.swak.fx.support.FXMLView;
import com.swak.fx.support.Notifys;
import com.swak.fx.support.Window;
import com.swak.tools.config.Mode;
import com.swak.tools.config.Settings;
import com.swak.ui.Event;
import com.swak.ui.EventListener;

import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

/**
 * 界面
 * 
 * @author lifeng
 */
@FXMLView(title = "深大穿戴.陀螺仪", value = "MainPage.fxml", css = "MainPage.css", stageStyle = "TRANSPARENT")
public class MainPage extends Window implements EventListener {

	@FXML
	private DownloadPane download;
	@FXML
	private WebView webView;
	private WebEngine browser;
	private Worker<Void> worker;
	private App app;
	private Stage settings;
	private String serverUrl;

	@FXML
	public void initialize() {
		double h = Display.getScreen().getVisualBounds().getHeight() * 0.93;
		double w = h * 1.66;
		this.app = new MainApp();
		this.root.setPrefWidth(w);
		this.root.setPrefHeight(h);
		browser = webView.getEngine();
		browser.loadContent(this.loading());
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
		if (event.is(Event.DOWNLOAD)) {
			Map<String, String> params = event.getMessage();
			String name = params.get("name");
			String url = params.get("url");
			download.download(name, url);
		} else if (event.is(Event.CLOSE)) {
			this.onClose(null);
		} else if (event.is(Event.URL)) {
			this.serverUrl = event.getMessage();
			if (!Settings.me().getConfig().server()) {
				this.loadUrl(this.serverUrl);
			} else {
				Display.runUI(() -> {
					ClipboardContent clipboardContent = new ClipboardContent();
					clipboardContent.putString(this.serverUrl);
					Display.getClipboard().setContent(clipboardContent);
					Notifys.info("提醒", "地址复制成功，请在浏览器中访问");
				});
			}
		} else if (event.is(Event.MESS)) {
			Mode mode = event.getMessage();
			this.switchMode(mode);
		} else if (event.is(Event.EXIT)) {
			Display.runUI(() -> {
				if (this.root.getScene().getWindow().isShowing()) {
					this.onHide(null);
					if (settings != null) {
						settings.close();
					}
				}
			});

		}
	}

	/**
	 * 加载Url
	 * 
	 * @param url
	 */
	private void loadUrl(String url) {
		if (browser != null) {
			Display.runUI(() -> {
				browser.load(url);
			});
		}
	}

	/**
	 * 切换模式
	 * 
	 * @param mode
	 */
	private void switchMode(Mode mode) {
		Display.runUI(() -> {
			if (mode != null && mode == Mode.server) {
				this.browser.loadContent(this.loading());
			} else {
				this.browser.load(serverUrl);
			}
		});
	}

	/**
	 * 显示升级界面
	 */
	@FXML
	public void openUpgrader() {
		new UpgraderPage().openOn(Display.getStage());
	}

	/**
	 * 显示设置界面
	 */
	@FXML
	public void openSettings() {
		if (settings == null) {
			settings = new SettingsPage().openOn(Display.getStage());
		}
		if (!settings.isShowing()) {
			settings.show();
		}
	}
}