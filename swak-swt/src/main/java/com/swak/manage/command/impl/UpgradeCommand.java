package com.swak.manage.command.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.swak.manage.command.Command;
import com.swak.manage.command.Commands.Sign;
import com.swak.manage.command.Commands.Signal;
import com.swak.manage.config.Settings;
import com.swak.manage.operation.Ops;
import com.swak.manage.operation.OpsFile;
import com.swak.manage.operation.ops.BackupOps;
import com.swak.manage.operation.ops.ConfigOps;
import com.swak.manage.operation.ops.JarOps;
import com.swak.manage.operation.ops.LibOps;
import com.swak.manage.operation.ops.LogOps;
import com.swak.manage.operation.ops.MoveOps;
import com.swak.manage.operation.ops.SqlOps;
import com.swak.manage.operation.ops.VerifyOps;
import com.swak.manage.widgets.theme.upgrade.Patch;

/**
 * 升级代码， 代码已经放到固定的目录下
 * 
 * @author lifeng
 */
public class UpgradeCommand implements Command {

	Ops ops;

	public UpgradeCommand() {
		ops = new BackupOps();
		VerifyOps verifyOps = new VerifyOps();
		SqlOps sqlOps = new SqlOps();
		LibOps libOps = new LibOps();
//		StaticOps staticOps = new StaticOps();
		ConfigOps configOps = new ConfigOps();
		JarOps jarOps = new JarOps();
		MoveOps moveOps = new MoveOps();
		LogOps logOps = new LogOps();
		ops.next(verifyOps);
		verifyOps.next(sqlOps);
		sqlOps.next(libOps);
//		libOps.next(staticOps);
		libOps.next(configOps);
		configOps.next(jarOps);
		jarOps.next(moveOps);
		moveOps.next(logOps);
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

		// 安装初始化
		this.preUpgrade();
		
		// 执行安装
		List<Patch> undos = this.sorts(undo.listFiles());
		for (Patch file : undos) {
			// 执行升级
			ops.doOps(OpsFile.ops(file));
		}
		
		// 按转之后
		this.postUpgrade();
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

	// 初始化操作
	protected void preUpgrade() {
		File logFile = new File(Settings.me().getLogUpgradePath());
		if (logFile.exists()) {
			logFile.delete();
		}
	}
	
	// 初始化操作
	protected void postUpgrade() {
		this.sendSignal(Signal.newSignal(Sign.upgraded));
	}
}