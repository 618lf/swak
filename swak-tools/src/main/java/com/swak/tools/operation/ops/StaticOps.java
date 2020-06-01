package com.swak.tools.operation.ops;

import java.io.File;
import java.util.List;

import com.swak.tools.config.Settings;
import com.swak.tools.operation.AbsOps;
import com.swak.tools.operation.OpsException;
import com.swak.tools.operation.OpsFile;
import com.swak.tools.operation.OpsFile.OpsEntry;

/**
 * 
 * 静态文件的操作 -- 需要全部删除
 * 
 * @author lifeng
 */
public class StaticOps extends AbsOps {

	@Override
	protected void doInnerOps(OpsFile file) throws Exception {
		try {
			if (file.continuAbled()) {

				// 更新文件
				List<OpsEntry> sattics = file.statics();
				if (sattics != null && sattics.size() > 0) {
					File base = Settings.me().getStaticsPath();
					if (base.isDirectory()) {
						deleteChildFile(base);
					}
					for (OpsEntry entry : sattics) {
						this.updateFile(base, entry);
					}
				}
			}
		} catch (Exception e) {
			throw new OpsException("更新静态文件失败");
		}
	}
}