package com.swak.tools.operation.ops;

import java.io.File;

import com.swak.tools.config.Settings;
import com.swak.tools.operation.AbsOps;
import com.swak.tools.operation.OpsException;
import com.swak.tools.operation.OpsFile;
import com.swak.tools.operation.OpsFile.OpsEntry;

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