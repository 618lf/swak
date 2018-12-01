package com.tmt.manage.operation.ops;

import java.io.File;
import java.util.List;

import com.tmt.manage.config.Settings;
import com.tmt.manage.operation.OpsException;
import com.tmt.manage.operation.OpsFile;
import com.tmt.manage.operation.OpsFile.OpsEntry;

/**
 * lib 的操作
 * 
 * @author lifeng
 */
public class LibOps extends JarOps {

	@Override
	protected void doInnerOps(OpsFile file) throws Exception {
		try {
			if (file.continuAbled()) {
				File lib = new File(Settings.me().getLibPath());
				List<OpsEntry> entrys = file.libs();
				for (OpsEntry entry : entrys) {
					this.updateJar(lib, entry);
				}
			}
		} catch (Exception e) {
			throw new OpsException("更新LIB失败");
		}
	}
}