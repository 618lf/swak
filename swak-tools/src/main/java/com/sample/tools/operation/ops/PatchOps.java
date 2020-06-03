package com.sample.tools.operation.ops;

import com.sample.tools.operation.Ops;
import com.sample.tools.operation.OpsFile;

/**
 * 添加补丁
 * 
 * @author lifeng
 */
public class PatchOps implements Ops {

	final Ops ops;

	public PatchOps() {
		ops = new BackupOps();
		VerifyOps verifyOps = new VerifyOps();
		VersionOps versionOps = new VersionOps();
		SqlOps sqlOps = new SqlOps();
		LibOps libOps = new LibOps();
		StaticOps staticOps = new StaticOps();
		ConfigOps configOps = new ConfigOps();
		JarOps jarOps = new JarOps();
		MoveOps moveOps = new MoveOps();
		ops.next(verifyOps);
		verifyOps.next(versionOps);
		versionOps.next(sqlOps);
		sqlOps.next(libOps);
		libOps.next(staticOps);
		staticOps.next(configOps);
		configOps.next(jarOps);
		jarOps.next(moveOps);
	}

	/**
	 * 执行处理
	 */
	@Override
	public void doOps(OpsFile file) {
		ops.doOps(file);
	}
}