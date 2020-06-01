package com.swak.tools.operation.ops;

import java.io.File;

import com.swak.tools.config.Settings;
import com.swak.tools.operation.AbsOps;
import com.swak.tools.operation.OpsFile;

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
