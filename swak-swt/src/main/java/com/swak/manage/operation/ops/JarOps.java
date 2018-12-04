package com.swak.manage.operation.ops;

import java.io.File;

import com.swak.manage.config.Settings;
import com.swak.manage.operation.AbsOps;
import com.swak.manage.operation.OpsException;
import com.swak.manage.operation.OpsFile;
import com.swak.manage.operation.OpsFile.OpsEntry;

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