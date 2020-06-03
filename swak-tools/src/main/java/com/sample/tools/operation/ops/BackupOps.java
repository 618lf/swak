package com.sample.tools.operation.ops;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sample.tools.config.Backup;
import com.sample.tools.config.Settings;
import com.sample.tools.config.Settings.Datasource;
import com.sample.tools.operation.AbsOps;
import com.sample.tools.operation.OpsFile;
import com.swak.utils.StringUtils;

/**
 * 每次更新前先备份数据库文件 的操作
 * 
 * @author lifeng
 */
public class BackupOps extends AbsOps {

	/**
	 * 每次更新前先备份数据库文件
	 * 
	 * @throws Exception
	 */
	@Override
	protected void doInnerOps(OpsFile file) throws Exception {

		// 备份文件的目录
		File backupDir = Settings.me().getBackupPath();
		if (!backupDir.exists()) {
			backupDir.mkdirs();
		}

		// 创建备份临时目录
		File temFile = new File(Settings.me().getBasePath(), ".temp");
		if (temFile.exists()) {
			this.deleteFile(temFile);
			temFile.mkdirs();
		}

		// 数据库文件的复制
		this.copyDb(temFile);

		// 数据文件的复制
		this.copyDatas(temFile);

		// 创建备份文件
		File zipFile = new File(backupDir, "数据备份-" + this.date() + ".zip");
		try {
			OpsFile.ops(Backup.newBackup(zipFile)).backup(temFile);
		} catch (Exception e) {
		} finally {
			this.deleteFile(temFile);
		}
	}

	/**
	 * 复制数据库文件
	 * 
	 * @param temFile
	 */
	private void copyDb(File temFile) {
		Datasource datasource = Settings.me().getDatasource();
		if (datasource != null && datasource.getDb() != null) {
			File dbFile = new File(Settings.me().getDbPath(), datasource.getDb());
			this.copyFile(new File(temFile, dbFile.getName()), dbFile, (dir, name) -> {
				return true;
			});
		}
	}

	/**
	 * 复制数据文件
	 * 
	 * @param temFile
	 */
	private void copyDatas(File temFile) {
		File datasFile = Settings.me().getDataPath();
		this.copyFile(new File(temFile, datasFile.getName()), datasFile, (dir, name) -> {
			return !StringUtils.endsWith(name, "_Analysis");
		});
	}

	/**
	 * 当前日期
	 * 
	 * @return
	 */
	String date() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH_mm");
		return df.format(new Date());
	}
}
