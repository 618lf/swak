package com.tmt.manage.operation.ops;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import com.tmt.manage.config.Settings;
import com.tmt.manage.operation.AbsOps;
import com.tmt.manage.operation.OpsFile;
import com.tmt.manage.widgets.theme.upgrade.Log;

/**
 * 记录安装的日志
 * 
 * @author lifeng
 */
public class LogOps extends AbsOps {

	@Override
	protected void doInnerOps(OpsFile file) throws Exception {
		if (file.continuAbled()) {
			writeLog(file.patch().getName(), "安装成功");
		} else {
			writeLog(file.patch().getName(), file.error().toString());
		}
	}

	// 写日志文件
	private void writeLog(String name, String text) throws IOException {
		File logFile = new File(Settings.me().getLogUpgradePath());
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
		String xml = Log.newLog(name, text).format(); 
		Files.write(logFile.toPath(), xml.getBytes("utf-8"), StandardOpenOption.APPEND);
	}
}