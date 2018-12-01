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
			settings.basePath = basePath;
			settings.configPath = configPath;
			settings.upgradePath = basePath + "/.upgrade/";
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
	private String upgradePath;
	private Server server = new Server();
	private Datasource datasource = new Datasource();

	public String getBasePath() {
		return basePath;
	}
	public String getConfigPath() {
		return configPath;
	}
	public String getUpgradePath() {
		return upgradePath;
	}
	public String getUnUpgradePath() {
		return upgradePath + "un";
	}
	public String getDoUpgradePath() {
		return upgradePath + "do";
	} 
	public String getLogUpgradePath() {
		return upgradePath + "log";
	}
	public String getLibPath() {
		return upgradePath + "lib";
	}
	public String getLogsPath() {
		return upgradePath + "logs";
	}
	public Server getServer() {
		return server;
	}
	public Datasource getDatasource() {
		return datasource;
	}
	protected void handleOs() {
		if (configPath.startsWith("home/")) {
			configPath = "/" + configPath;
			basePath = "/" + basePath;
			upgradePath = "/" + upgradePath;
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
		String profiles = null;
		try {
			File propertiesFile = new File(configPath, "application.properties");
			InputStream is = new FileInputStream(propertiesFile);
			Properties properties = new Properties();
			properties.load(is);
			profiles = properties.getProperty("spring.profiles.active");
			is.close();
			this.handleProperties(properties);
		} catch (Exception e) {
		}
		if (profiles != null) {
			try {
				File propertiesFile = new File(configPath, "application-" + profiles + ".properties");
				InputStream is = new FileInputStream(propertiesFile);
				Properties properties = new Properties();
				properties.load(is);
				is.close();
				this.handleProperties(properties);
			} catch (Exception e) {
			}
		}
	}
	protected void handleProperties(Properties properties) {
		String serverName = properties.getProperty("app.name");
		String serverIndex = properties.getProperty("app.name");
		String serverVersion = properties.getProperty("app.version");
		String db = properties.getProperty("spring.datasource.db");
		String url = properties.getProperty("spring.datasource.url");
		String username = properties.getProperty("spring.datasource.username");
		String password = properties.getProperty("spring.datasource.password");

		this.server.setName(serverName);
		this.server.setIndex(serverIndex);
		this.server.setVersion(serverVersion);
		this.datasource.setDb(db);
		this.datasource.setUrl(url);
		this.datasource.setUsername(username);
		this.datasource.setPassword(password);
	}

	// 服务的配置
	public class Server {
		
		private String name;
		private String index;
		private String version;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
			this.index = index;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

	}

	// 数据库配置
	public class Datasource {
		
		private String db;
		private String url;
		private String username;
		private String password;

		public String getDb() {
			return db;
		}

		public void setDb(String db) {
			this.db = db;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}