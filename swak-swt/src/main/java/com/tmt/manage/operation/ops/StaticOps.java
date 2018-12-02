package com.tmt.manage.operation.ops;

import java.io.File;
import java.util.List;

import com.tmt.manage.config.Settings;
import com.tmt.manage.operation.AbsOps;
import com.tmt.manage.operation.OpsException;
import com.tmt.manage.operation.OpsFile;
import com.tmt.manage.operation.OpsFile.OpsEntry;

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