package com.swak.tools.operation.ops;

import java.io.File;

import com.swak.tools.config.Settings;
import com.swak.tools.operation.OpsFile;

/**
 * 运行时清理，用于发布代码之后
 * 
 * @author lifeng
 */
public class ClearRuntimeOps extends ClearOps {

	@Override
	protected void doInnerOps(OpsFile file) throws Exception {
		File tmpFiles = new File(Settings.me().getBasePath(), ".runtime");
		if (tmpFiles.isDirectory()) {
			deleteFile(tmpFiles);
		}
		super.doInnerOps(file);
	}
}
