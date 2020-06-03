package com.sample.tools.page;

import java.util.List;

import com.sample.tools.plugin.Plugin;
import com.sample.tools.plugin.PluginButton;
import com.sample.tools.plugin.PluginLoader;
import com.swak.fx.support.Dialogs;
import com.swak.fx.support.Display;
import com.swak.fx.support.FXMLView;
import com.swak.fx.support.Window;
import com.swak.ui.Event;
import com.swak.ui.EventListener;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 * 界面
 * 
 * @author lifeng
 */
@FXMLView(title = "开发工具", value = "MainPage.fxml", css = "MainPage.css", stageStyle = "TRANSPARENT")
public class MainPage extends Window implements EventListener {

	@FXML
	private FlowPane menuPane;
	private Stage settings;
	private PluginLoader pluginLoader = PluginLoader.me();

	@FXML
	public void initialize() {
		double h = Display.getScreen().getVisualBounds().getHeight() * 0.93;
		double w = h * 1.66;
		this.root.setPrefWidth(w);
		this.root.setPrefHeight(h);
		Display.getEventBus().register(this);
		this.initPlugins();
		super.initialize();
	}

	/**
	 * 初始化插件
	 */
	private void initPlugins() {
		List<Plugin> plugins = pluginLoader.getPlugins();
		for (Plugin plugin : plugins) {
			menuPane.getChildren().add(PluginButton.create(plugin));
		}
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
		} else if (event.is(Event.CLOSE)) {
			this.onClose(null);
		} else if (event.is(Event.URL)) {
		} else if (event.is(Event.MESS)) {
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

	/**
	 * 菜单加载
	 * 
	 * @author lifeng
	 * @date 2020年6月2日 下午4:33:22
	 */
	public static class MenuLoader {

	}
}