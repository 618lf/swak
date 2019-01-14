package com.swak.fx.operation.ops;

import java.io.File;
import java.nio.file.Files;

import com.swak.fx.config.Settings;
import com.swak.fx.operation.AbsOps;
import com.swak.fx.operation.OpsException;
import com.swak.fx.operation.OpsFile;

/**
 * 安装成功，将文件移动到已安装目录
 * 
 * @author lifeng
 */
public class MoveOps extends AbsOps {

	@Override
	protected void doInnerOps(OpsFile file) throws Exception {
		try {
			if (file.continuAbled()) {
				File doDir = new File(Settings.me().getDoUpgradePath());
				if (!doDir.exists()) {
					doDir.mkdirs();
				}
				File source = file.patch().getFile();
				File target = new File(Settings.me().getDoUpgradePath(), source.getName());
				if (!target.exists()) {
					Files.move(source.toPath(), target.toPath());
				} else {
					source.delete();
				}
			}
		} catch (Exception e) {
			throw new OpsException("补丁更新成功，但移动到指定目录时失败");
		}
	}
}
