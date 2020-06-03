package com.sample.tools.operation.ops;

import java.io.File;
import java.util.List;

import com.sample.tools.config.Settings;
import com.sample.tools.operation.AbsOps;
import com.sample.tools.operation.OpsException;
import com.sample.tools.operation.OpsFile;
import com.sample.tools.operation.OpsFile.OpsEntry;

/**
 * lib 的操作
 * 
 * @author lifeng
 */
public class LibOps extends AbsOps {

	@Override
	protected void doInnerOps(OpsFile file) throws Exception {
		try {
			if (file.continuAbled()) {
				File lib = Settings.me().getLibPath();
				List<OpsEntry> entrys = file.libs();
				for (OpsEntry entry : entrys) {
					this.updateFile(lib, entry);
				}
			}
		} catch (Exception e) {
			throw new OpsException("更新LIB失败");
		}
	}
}