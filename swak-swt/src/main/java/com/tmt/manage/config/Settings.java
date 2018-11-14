package com.tmt.manage.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
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

	public static Settings me() {
		return context;
	}

	/**
	 * 默认取这个文件的路径： application.properties
	 * 
	 * @param args
	 * @return
	 */
	public static Settings intSettings(String args[]) {
		if (context == null) {
			String mainClass = args != null && args.length > 0 ? args[0] : "application.properties";
			context = Settings.init(mainClass);
		}
		return context;
	}

	/**
	 * 初始化
	 * 
	 * @return
	 */
	public static Settings init(String mainClass) {
		URL classPathUrl = Settings.class.getClassLoader().getResource(mainClass);
		if (classPathUrl == null) {
			throw new RuntimeException("解析配置文件错误：" + mainClass);
		}
		try {
			String classPath = classPathUrl.getPath();
			classPath = URLDecoder.decode(classPath, "UTF-8");
			String basePath = "", configPath = "";

			// 结合 springboot 一起发布
			if (classPath.indexOf("/BOOT-INF/") != -1) {
				basePath = classPath.substring(classPath.indexOf("file:/") + 6, classPath.indexOf("!"));
				basePath = basePath.substring(0, basePath.lastIndexOf("/"));
				configPath = basePath + "/config/";
			}
			// 开发环境中,作为jar包发布
			else if (classPath.indexOf("!") != -1) {
				basePath = classPath.substring(classPath.indexOf("file:/") + 6, classPath.indexOf("!"));
				basePath = basePath.substring(0, basePath.lastIndexOf("/"));
				configPath = basePath + "/config/";
			}
			// 开发环境中
			else {
				basePath = classPath;
				if (mainClass != null && !"".equals(mainClass)) {
					basePath = basePath.substring(0, basePath.indexOf(mainClass));
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
			e.printStackTrace();
		}
		throw new RuntimeException("解析配置文件错误:" + mainClass);
	}

	private String basePath;
	private String configPath;
	private String serverName;
	private String serverPage;
	private String serverVersion;

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

	public String getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
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
			this.serverName = properties.getProperty("app.name");
			this.serverPage = properties.getProperty("app.page");
			this.serverVersion = properties.getProperty("app.version");
		} catch (Exception e) {
		}
	}
}