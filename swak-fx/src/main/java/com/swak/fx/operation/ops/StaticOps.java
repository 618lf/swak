package com.swak.fx.operation.ops;

import java.io.File;
import java.util.List;

import com.swak.fx.config.Settings;
import com.swak.fx.operation.AbsOps;
import com.swak.fx.operation.OpsException;
import com.swak.fx.operation.OpsFile;
import com.swak.fx.operation.OpsFile.OpsEntry;

/**
 * 
 * 静态文件的操作
 * 
 * @author lifeng
 */
public class StaticOps extends AbsOps {

	@Override
	protected void doInnerOps(OpsFile file) throws Exception {
		try {
			if (file.continuAbled()) {
				File base = new File(Settings.me().getStaticsPath());
				List<OpsEntry> sattics = file.statics();
				for (OpsEntry entry : sattics) {
					this.updateFile(base, entry);
				}
			}
		} catch (Exception e) {
			throw new OpsException("更新静态文件失败");
		}
	}
}