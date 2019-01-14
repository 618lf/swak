package com.swak.fx.operation.ops;

import java.io.File;
import java.util.List;

import com.swak.fx.config.Settings;
import com.swak.fx.operation.AbsOps;
import com.swak.fx.operation.OpsException;
import com.swak.fx.operation.OpsFile;
import com.swak.fx.operation.OpsFile.OpsEntry;

/**
 * 配置文件的操作
 * 
 * @author lifeng
 */
public class ConfigOps extends AbsOps{

	@Override
	protected void doInnerOps(OpsFile file) throws Exception {
		try {
			if (file.continuAbled()) {
				File base = new File(Settings.me().getConfigPath());
				List<OpsEntry> sattics = file.configs();
				for (OpsEntry entry : sattics) {
					// h2下为数据库文件不能直接替换
					if (entry.getName().indexOf("h2") != -1) {
						continue;
					}
					this.updateFile(base, entry);
				}
			}
		} catch (Exception e) {
			throw new OpsException("更新配置文件失败");
		}
	}
}
