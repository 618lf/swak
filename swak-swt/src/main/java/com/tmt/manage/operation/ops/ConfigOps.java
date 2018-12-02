package com.tmt.manage.operation.ops;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.tmt.manage.config.Settings;
import com.tmt.manage.operation.AbsOps;
import com.tmt.manage.operation.OpsException;
import com.tmt.manage.operation.OpsFile;
import com.tmt.manage.operation.OpsFile.OpsEntry;

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
					this.updateConfig(base, entry);
				}
			}
		} catch (Exception e) {
			throw new OpsException("更新配置文件失败");
		}
	}
	
	// 更新 lib
	protected void updateConfig(File parent, OpsEntry entry) throws IOException {
		File libFile = new File(parent, entry.getName());
		libFile.delete();
		libFile.createNewFile();
		FileOutputStream out = new FileOutputStream(libFile);
		try {
			out.write(entry.getData());
		} finally {
			out.close();
		}
	}
}
