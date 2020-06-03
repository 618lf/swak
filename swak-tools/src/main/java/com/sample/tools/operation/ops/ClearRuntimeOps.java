package com.sample.tools.operation.ops;

import java.io.File;

import com.sample.tools.config.Settings;
import com.sample.tools.operation.OpsFile;

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
