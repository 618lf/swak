package com.swak.tools.operation.ops;

import com.swak.tools.config.Settings;
import com.swak.tools.operation.AbsOps;
import com.swak.tools.operation.OpsException;
import com.swak.tools.operation.OpsFile;
import com.swak.tools.operation.OpsFile.OpsEntry;

/**
 * 版本操作
 * 
 * @author lifeng
 * @date 2020年5月28日 上午10:44:24
 */
public class VersionOps extends AbsOps {

	@Override
	protected void doInnerOps(OpsFile file) throws Exception {
		try {
			OpsEntry version = file.version();
			if (version == null) {
				file.error("版本文件不存在");
				return;
			}
			String _version = new String(version.getData(), "utf-8");
			String vString = _version.substring(_version.indexOf("cur:") + 4);
			Settings.me().getVersion().setVersion(Double.parseDouble(vString));
			Settings.me().storeVersion();
		} catch (Exception e) {
			throw new OpsException("更新版本文件失败");
		}
	}
}
