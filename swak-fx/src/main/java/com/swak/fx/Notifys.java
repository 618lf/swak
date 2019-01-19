package com.swak.fx;

import org.controlsfx.control.Notifications;

import javafx.geometry.Pos;

/**
 * 通知
 * 
 * @author lifeng
 */
public class Notifys {

	/**
	 * 错误
	 * 
	 * @return
	 */
	public static void error(String title, String message) {
		Display.runUI(() -> {
			Notifications.create().title(title).text(message).hideAfter(javafx.util.Duration.seconds(2))
					.position(Pos.BOTTOM_RIGHT).darkStyle().owner(Display.getStage()).showError();
		});
	}

	/**
	 * 错误
	 * 
	 * @return
	 */
	public static void error(String title, String message, Pos pos) {
		Display.runUI(() -> {
			Notifications.create().title(title).text(message).hideAfter(javafx.util.Duration.seconds(2)).position(pos)
					.darkStyle().owner(Display.getStage()).showError();
		});
	}

	/**
	 * 错误
	 * 
	 * @return
	 */
	public static void info(String title, String message) {
		Display.runUI(() -> {
			Notifications.create().title(title).text(message).hideAfter(javafx.util.Duration.seconds(2))
					.position(Pos.BOTTOM_RIGHT).darkStyle().owner(Display.getStage()).showInformation();
		});
	}
}