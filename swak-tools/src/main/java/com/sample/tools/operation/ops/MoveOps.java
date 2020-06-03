package com.sample.tools.operation.ops;

import java.io.File;
import java.nio.file.Files;

import com.sample.tools.config.Settings;
import com.sample.tools.operation.AbsOps;
import com.sample.tools.operation.OpsException;
import com.sample.tools.operation.OpsFile;

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
				File doDir = new File(Settings.me().getUpgradePath(), "do");
				if (!doDir.exists()) {
					doDir.mkdirs();
				}
				File source = file.patch().getFile();
				File target = new File(doDir, source.getName());
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
