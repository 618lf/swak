package com.sample.tools.operation.ops;

import java.io.File;

import com.sample.tools.config.Settings;
import com.sample.tools.operation.AbsOps;
import com.sample.tools.operation.OpsException;
import com.sample.tools.operation.OpsFile;
import com.sample.tools.operation.OpsFile.OpsEntry;

/**
 * jar 的操作
 * 
 * @author lifeng
 */
public class JarOps extends AbsOps {

	@Override
	protected void doInnerOps(OpsFile file) throws Exception {
		try {
			OpsEntry entry = null;
			if (file.continuAbled() && (entry = file.jar()) != null) {
				File base = Settings.me().getBasePath();
				this.updateFile(base, entry);
			}
		} catch (Exception e) {
			throw new OpsException("更新JAR失败");
		}
	}
}