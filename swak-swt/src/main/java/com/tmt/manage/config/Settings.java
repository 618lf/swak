package com.tmt.manage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Properties;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * 优化系统配置
 * 
 * @author lifeng
 */
public class Settings {

	private static volatile Settings context;

	public static Settings getSettings() {
		if (context == null) {
			context = Settings.init();
		}
		return context;
	}

	/**
	 * 初始化
	 * 
	 * @return
	 */
	public static Settings init() {
		try {
			String main_class = "com/tmt/manage/App.class";
			String classPath = ClassLoader.getSystemResource(main_class).getPath();
			classPath = URLDecoder.decode(classPath, "UTF-8");
			String basePath = "", configPath = "";
			if (classPath != null && classPath.indexOf("!") != -1) {
				if (classPath.indexOf("file:/") != -1) {
					basePath = classPath.substring(6, classPath.indexOf("!"));
				} else {
					basePath = classPath.substring(0, classPath.indexOf("!"));
				}
				basePath = basePath.substring(0, basePath.lastIndexOf("/manage/"));
				configPath = basePath + "/config/";
			} else if (classPath != null) {
				if (classPath.indexOf("file:/") != -1) {
					basePath = classPath.substring(6, classPath.indexOf(main_class));
				} else {
					basePath = classPath.substring(1, classPath.indexOf(main_class));
				}
				configPath = basePath;
			}
			Settings settings = new Settings();
			settings.setBasePath(basePath);
			settings.setConfigPath(configPath);
			settings.handleOs();
			settings.handleLog();
			settings.handleFileSeparator();
			settings.handleProperties();
			return settings;
		} catch (Exception e) {
		}
		throw new RuntimeException("解析配置文件错误");
	}

	private String basePath;
	private String configPath;
	private String serverName;
	private String serverPage;

	public String getBasePath() {
		return basePath;
	}
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	public String getConfigPath() {
		return configPath;
	}
	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerPage() {
		return serverPage;
	}
	public void setServerPage(String serverPage) {
		this.serverPage = serverPage;
	}

	protected void handleOs() {
		if (configPath.startsWith("home/")) {
			configPath = "/" + configPath;
			basePath = "/" + basePath;
		}
	}

	protected void handleLog() {
		File file = new File(configPath, "logback.xml");
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator joranConfigurator = new JoranConfigurator();
		joranConfigurator.setContext(loggerContext);
		loggerContext.reset();
		try {
			joranConfigurator.doConfigure(file);
		} catch (Exception e) {
		}
		StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
	}

	protected void handleFileSeparator() {
		String separator = File.separator;
		if ("\\".equals(separator)) {
			separator = "\\\\";
		}
		if (this.basePath != null) {
			this.basePath = this.basePath.replaceAll("\\\\", separator);
			this.basePath = this.basePath.replaceAll("/", separator);
		}
		if (this.configPath != null) {
			this.configPath = this.configPath.replaceAll("\\\\", separator);
			this.configPath = this.configPath.replaceAll("/", separator);
		}
	}

	protected void handleProperties() {
		try {
			File propertiesFile = new File(configPath, "application.properties");
			InputStream is = new FileInputStream(propertiesFile);
			Properties properties = new Properties();
			properties.load(is);
			is.close();
			this.serverName = properties.getProperty("server.name");
			this.serverPage =  properties.getProperty("server.page");
		} catch (Exception e) {
		}
	}
}