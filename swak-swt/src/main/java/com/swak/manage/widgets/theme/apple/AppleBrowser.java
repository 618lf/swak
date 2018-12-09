package com.swak.manage.widgets.theme.apple;

import java.awt.Component;
import java.lang.reflect.Field;

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;

import com.swak.manage.config.Settings;

/**
 * 实现浏览器的功能
 * 
 * @author lifeng
 */
public class AppleBrowser {

	private CefApp cefApp;
	private CefClient cefClient;
	private CefBrowser browser;

	public AppleBrowser() {
		loadLibrary();
		CefSettings settings = new CefSettings();
		settings.windowless_rendering_enabled = false;
		settings.background_color = settings.new ColorType(100, 255, 242, 211);
		cefApp = CefApp.getInstance(settings);
		cefClient = cefApp.createClient();
		browser = cefClient.createBrowser("http://www.baidu.com", false, false);
	}

	public Component getUIComponent() {
		return browser.getUIComponent();
	}

	private void loadLibrary() {
		try {
			String libraryPath = getLibraryPath();
			System.out.println(libraryPath);
			Field userPathsField = ClassLoader.class.getDeclaredField("usr_paths");
			userPathsField.setAccessible(true);
			String[] paths = (String[]) userPathsField.get(null);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < paths.length; i++) {
				if (libraryPath.equals(paths[i])) {
					continue;
				}
				sb.append(paths[i]).append(';');
			}
			sb.append(libraryPath);
			System.setProperty("java.library.path", sb.toString());
			final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
			sysPathsField.setAccessible(true);
			sysPathsField.set(null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getLibraryPath() {
		return Settings.me().getLibPath() + "\\win64"; 
	}

	public void dispost() {
		cefApp.dispose();
		cefClient.dispose();
	}
}