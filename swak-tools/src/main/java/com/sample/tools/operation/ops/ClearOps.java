package com.sample.tools.operation.ops;

import java.io.File;

import com.sample.tools.config.Settings;
import com.sample.tools.operation.AbsOps;
import com.sample.tools.operation.OpsFile;

/**
 * 清除数据
 * 
 * @author lifeng
 */
public class ClearOps extends AbsOps {

	@Override
	protected void doInnerOps(OpsFile file) throws Exception {
		File tmpFiles = new File(Settings.me().getBasePath(), "export_tmp");
		if (tmpFiles.isDirectory()) {
			deleteFile(tmpFiles);
		}
		tmpFiles = new File(Settings.me().getBasePath(), "uploads");
		if (tmpFiles.isDirectory()) {
			deleteFile(tmpFiles);
		}
		tmpFiles = new File(Settings.me().getBasePath(), ".runtime/temps");
		if (tmpFiles.isDirectory()) {
			deleteFile(tmpFiles);
		}
	}
}
