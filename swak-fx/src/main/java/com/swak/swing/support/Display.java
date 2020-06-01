package com.swak.swing.support;

import java.awt.SystemTray;

import javax.swing.SwingUtilities;

import com.google.common.eventbus.EventBus;
import com.swak.ui.Events;

/**
 * The enum {@link Display} stores Scene and Stage objects as singletons in this
 * VM.
 *
 * @author Felix Roske
 * @author Andreas Jay
 */
public enum Display {

	INSTANCE;

	private static String title;
	private static SystemTray systemTray;

	public static String getTitle() {
		return title;
	}

	public static void setTitle(final String title) {
		Display.title = title;
	}

	public static SystemTray getSystemTray() {
		return systemTray;
	}

	static void setSystemTray(SystemTray systemTray) {
		Display.systemTray = systemTray;
	}

	public static EventBus getEventBus() {
		return Events.eventBus;
	}

	static void setEventBus(EventBus eventBus) {
		Events.eventBus = eventBus;
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
	 * 加载资源
	 * 
	 * @param path
	 * @return
	 */
	public static java.net.URL load(Class<?> clazz, String path) {
		return clazz.getResource(path);
	}

	/**
	 * 如果当前线程属于 UI 线程，则执行 runnable，否则调用 SwingUtilities.invokeLater() 来执行 runnable。
	 * 这样能保证 runnable 是在 UI 线程上执行。
	 * 
	 * @param runnable
	 */
	public static void runUI(Runnable runnable) {
		if (SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		} else {
			SwingUtilities.invokeLater(runnable);
		}
	}
}