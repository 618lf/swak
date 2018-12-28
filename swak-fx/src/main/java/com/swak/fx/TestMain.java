package com.swak.fx;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

@SuppressWarnings("restriction")
public class TestMain extends Application {
	@Override
	public void start(final Stage stage) {
		stage.setWidth(1200);
		stage.setHeight(900);
		Scene scene = new Scene(new Group());
		final WebView browser = new WebView();
		final WebEngine webEngine = browser.getEngine();
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(browser);
		webEngine.load("https://cloud.catax.cn");
		scene.setRoot(scrollPane);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}