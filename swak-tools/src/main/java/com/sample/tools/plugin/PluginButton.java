package com.sample.tools.plugin;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * 插件按钮
 * 
 * @author lifeng
 * @date 2020年6月3日 上午9:39:19
 */
public class PluginButton extends Pane implements EventHandler<MouseEvent> {

	private final Plugin plugin;
	private VBox root;
	private ImageView image;
	private Label text;

	public PluginButton(Plugin plugin) {
		super();
		this.plugin = plugin;
		this.setPrefSize(80, 100);
		this.root = new VBox();
		this.root.setAlignment(Pos.CENTER);
		this.image = new ImageView(plugin.logo());
		this.image.setFitHeight(80);
		this.image.setFitWidth(80);
		this.text = new Label(plugin.text());
		this.text.setPrefWidth(80);
		this.text.setPrefHeight(20);
		this.root.getChildren().add(this.image);
		this.root.getChildren().add(this.text);
		this.getChildren().add(this.root);
		this.image.setOnMouseClicked(this);
		this.image.setCursor(Cursor.HAND);

		// 显示设置
		this.getStyleClass().add("plugin-panel");
		this.image.getStyleClass().add("plugin-panel__image");
		this.text.getStyleClass().add("plugin-panel__text");
	}

	/**
	 * 处理点击事件
	 */
	@Override
	public void handle(MouseEvent event) {
		this.plugin.action();
	}

	/**
	 * 创建插件按钮
	 * 
	 * @param plugin
	 * @return
	 */
	public static PluginButton create(Plugin plugin) {
		return new PluginButton(plugin);
	}
}