package com.tmt.manage.operation.ops;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.tmt.manage.config.Settings;
import com.tmt.manage.operation.AbsOps;
import com.tmt.manage.operation.OpsException;
import com.tmt.manage.operation.OpsFile;
import com.tmt.manage.operation.OpsFile.OpsEntry;

/**
 * jar 的操作
 * 
 * @author lifeng
 */
public class JarOps extends AbsOps {

	@Override
	protected void doInnerOps(OpsFile file) throws Exception {
		try {
			OpsEntry entry = null;
			if (file.continuAbled() && (entry = file.jar()) != null) {
				File base = new File(Settings.me().getBasePath());
				updateJar(base, entry);
			}
		} catch (Exception e) {
			throw new OpsException("更新JAR失败");
		}
	}

	// 更新 lib
	protected void updateJar(File parent, OpsEntry entry) throws IOException {
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