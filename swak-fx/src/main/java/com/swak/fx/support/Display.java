package com.swak.fx.support;

import java.awt.SystemTray;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The enum {@link Display} stores Scene and Stage objects as singletons in this
 * VM.
 *
 * @author Felix Roske
 * @author Andreas Jay
 */
public enum Display {

	INSTANCE;

	private static Scene scene;
	private static Stage stage;
	private static String title;
	private static HostServices hostServices;
	private static SystemTray systemTray;

	public static String getTitle() {
		return title;
	}

	public static Scene getScene() {
		return scene;
	}

	public static Stage getStage() {
		return stage;
	}

	public static void setScene(final Scene scene) {
		Display.scene = scene;
	}

	public static void setStage(final Stage stage) {
		Display.stage = stage;
	}

	public static void setTitle(final String title) {
		Display.title = title;
	}

	public static HostServices getHostServices() {
		return hostServices;
	}

	static void setHostServices(HostServices hostServices) {
		Display.hostServices = hostServices;
	}

	public static SystemTray getSystemTray() {
		return systemTray;
	}

	static void setSystemTray(SystemTray systemTray) {
		Display.systemTray = systemTray;
	}

	/**
	 * 加载资源
	 * 
	 * @param path
	 * @return
	 */
	public static java.net.URL load(String path) {
		return Display.class.getResource(path);
	}
	
	/**
	 * 如果当前线程属于 UI 线程，则执行 runnable，否则调用 Platform.runLater() 来执行 runnable。
     * 这样能保证 runnable 是在 UI 线程上执行。
	 * 
	 * @param runnable
	 */
	public static void runUI(Runnable runnable) {
		if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
	}
}