package com.tmt.manage.command.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.tmt.manage.command.Command;
import com.tmt.manage.config.Settings;
import com.tmt.manage.operation.Ops;
import com.tmt.manage.operation.OpsFile;
import com.tmt.manage.operation.ops.JarOps;
import com.tmt.manage.operation.ops.LibOps;
import com.tmt.manage.operation.ops.LogOps;
import com.tmt.manage.operation.ops.SqlOps;
import com.tmt.manage.operation.ops.VerifyOps;
import com.tmt.manage.widgets.theme.upgrade.UpgraderTheme.Patch;

/**
 * 升级代码， 代码已经放到固定的目录下
 * 
 * @author lifeng
 */
public class UpgradeCommand implements Command {

	Ops ops;

	public UpgradeCommand() {
		ops = new VerifyOps();
		SqlOps sqlOps = new SqlOps();
		LibOps libOps = new LibOps();
		JarOps jarOps = new JarOps();
		LogOps logOps = new LogOps();
		ops.next(sqlOps);
		sqlOps.next(libOps);
		libOps.next(jarOps);
		jarOps.next(logOps);
	}

	/**
	 * 按顺序更新 待更新增两包下的增量包
	 */
	@Override
	public void exec() {

		// undo 目录
		File undo = new File(Settings.me().getUnUpgradePath());

		// 检查目录是否存在
		if (!undo.exists() && !undo.isDirectory()) {
			return;
		}

		// 执行安装
		List<Patch> undos = this.sorts(undo.listFiles());
		for (Patch file : undos) {
			// 执行升级
			ops.doOps(OpsFile.ops(file));
		}
	}

	// 排序
	private List<Patch> sorts(File[] undos) {
		List<Patch> files = new ArrayList<>();
		for (File file : undos) {
			files.add(Patch.newPatch(file));
		}
		files.sort(Patch.install);
		return files;
	}
}