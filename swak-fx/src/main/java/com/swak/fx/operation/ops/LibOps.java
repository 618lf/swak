package com.swak.fx.operation.ops;

import java.io.File;
import java.util.List;

import com.swak.fx.config.Settings;
import com.swak.fx.operation.AbsOps;
import com.swak.fx.operation.OpsException;
import com.swak.fx.operation.OpsFile;
import com.swak.fx.operation.OpsFile.OpsEntry;

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
				File lib = new File(Settings.me().getLibPath());
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