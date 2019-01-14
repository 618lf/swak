package com.swak.fx.operation.ops;

import java.io.File;

import com.swak.fx.config.Settings;
import com.swak.fx.operation.AbsOps;
import com.swak.fx.operation.OpsException;
import com.swak.fx.operation.OpsFile;
import com.swak.fx.operation.OpsFile.OpsEntry;

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
				File base = new File(Settings.me().getBasePath());
				this.updateFile(base, entry);
			}
		} catch (Exception e) {
			throw new OpsException("更新JAR失败");
		}
	}
}