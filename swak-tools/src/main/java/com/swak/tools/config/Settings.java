package com.swak.tools.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;

import com.google.common.io.Files;

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
	 * 默认取这个文件的路径
	 * 
	 * @param args
	 * @return
	 */
	public static Settings intSettings() {
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
	private static Settings init() {
		try {
			FileSystemResource resource = new FileSystemResource("");
			File baseFile = resource.getFile().getAbsoluteFile();
			File basePath = baseFile, configPath = new File(baseFile, "/src/main/resources/");
			if (new File(baseFile, "config").exists()) {
				configPath = new File(baseFile, "config");
			}
			Settings settings = new Settings();
			settings.basePath = basePath;
			settings.configPath = configPath;
			settings.handleVersion();
			settings.handleConfig();
			settings.handleLog();
			settings.handleProperties();
			return settings;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private File basePath;
	private File configPath;
	private String server;
	private Config config;
	private Version version;
	private Datasource datasource = new Datasource();

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public File getBasePath() {
		return basePath;
	}

	public File getConfigPath() {
		return configPath;
	}

	public File getUpgradePath() {
		return new File(basePath, ".upgrade");
	}

	public File getBackupPath() {
		return new File(basePath, ".back");
	}

	public File getLibPath() {
		return new File(basePath, "lib");
	}

	public File getLogsPath() {
		return new File(basePath, "logs");
	}
	
	public File getDataPath() {
		return new File(basePath, "datas");
	}

	public File getStaticsPath() {
		return new File(basePath, "statics");
	}

	public File getDbPath() {
		if (this.isProduct()) {
			return configPath;
		}
		return new File(basePath, "/src/main/db/");
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	public Datasource getDatasource() {
		return datasource;
	}

	protected void handleVersion() {
		try {
			File version = new File(this.getConfigPath(), "version");
			InputStream in = Files.asByteSource(version).openStream();
			this.version = Xmls.fromXml(in, Version.class);
		} catch (Exception e) {
			this.version = new Version();
		}
	}

	protected void handleConfig() {
		try {
			File version = new File(this.getConfigPath(), "settings");
			InputStream in = Files.asByteSource(version).openStream();
			this.config = Xmls.fromXml(in, Config.class);
		} catch (Exception e) {
			this.config = new Config();
		}
	}

	protected void handleLog() {
		if (this.isProduct()) {
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
		String db = properties.getProperty("spring.datasource.db");
		String url = properties.getProperty("spring.datasource.url");
		String username = properties.getProperty("spring.datasource.username");
		String password = properties.getProperty("spring.datasource.password");

		if (db != null) {
			this.datasource.setDb(db);
		}
		if (url != null) {
			this.datasource.setUrl(url);
		}
		if (username != null) {
			this.datasource.setUsername(username);
		}
		if (password != null) {
			this.datasource.setPassword(password);
		}
	}

	/**
	 * 是否是发布环境
	 */
	protected boolean isProduct() {
		if (new File(basePath, "config").exists()) {
			return true;
		}
		return false;
	}

	/**
	 * 存储版本
	 */
	public void storeVersion() {
		try {
			File version = new File(this.getConfigPath(), "version");
			String xml = Xmls.toXml(this.version);
			Files.write(xml.getBytes(StandardCharsets.UTF_8), version);
		} catch (Exception e) {
		}
	}

	/**
	 * 存储配置
	 */
	public void storeConfig() {
		try {
			File version = new File(this.getConfigPath(), "settings");
			String xml = Xmls.toXml(this.config);
			Files.write(xml.getBytes(StandardCharsets.UTF_8), version);
		} catch (Exception e) {
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