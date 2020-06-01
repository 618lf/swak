package com.swak.tools.operation.dialect;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.swak.tools.config.Backup;
import com.swak.tools.config.Settings;
import com.swak.tools.operation.OpsFile;

/**
 * 数据库方言
 * 
 * @author lifeng
 */
public interface Dialect {

	/**
	 * 数据库类型
	 * 
	 * @return
	 */
	String db();

	/**
	 * 打开数据库链接
	 * 
	 * @param url
	 * @param user
	 * @param password
	 * @return
	 * @throws SQLException
	 */
	Connection open(String url, String user, String password) throws SQLException;

	/**
	 * 备份数据库， 默认时直接备份整个数据库文件
	 * 
	 * @param url
	 * @param user
	 * @param password
	 * @throws SQLException
	 */
	default void backup(String url, String user, String password) throws SQLException {
		File backupDir = Settings.me().getBackupPath();
		if (!backupDir.exists()) {
			backupDir.mkdirs();
		}
		File dbFile = new File(Settings.me().getDbPath(), this.db());
		File zipFile = new File(backupDir, "数据备份-" + this.date() + ".zip");
		try {
			OpsFile.ops(Backup.newBackup(zipFile)).backup(dbFile);
		} catch (Exception e) {
		}
	}

	/**
	 * 当前日期
	 * 
	 * @return
	 */
	default String date() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH_mm");
		return df.format(new Date());
	}
}
