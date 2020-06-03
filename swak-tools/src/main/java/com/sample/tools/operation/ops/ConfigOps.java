package com.sample.tools.operation.ops;

import java.io.File;
import java.util.List;

import com.sample.tools.config.Settings;
import com.sample.tools.operation.AbsOps;
import com.sample.tools.operation.OpsException;
import com.sample.tools.operation.OpsFile;
import com.sample.tools.operation.OpsFile.OpsEntry;

/**
 * 配置文件的操作
 * 
 * @author lifeng
 */
public class ConfigOps extends AbsOps {

	@Override
	protected void doInnerOps(OpsFile file) throws Exception {
		try {
			if (file.continuAbled()) {
				File base = Settings.me().getConfigPath();
				List<OpsEntry> sattics = file.configs();
				for (OpsEntry entry : sattics) {
					// h2，sqlite下为数据库文件不能直接替换
					if (entry.getName().indexOf("h2") != -1 || entry.getName().indexOf("sqlite") != -1) {
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
