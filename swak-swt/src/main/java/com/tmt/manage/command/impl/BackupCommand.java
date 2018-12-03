package com.tmt.manage.command.impl;

import com.tmt.manage.command.Command;
import com.tmt.manage.command.Commands.Sign;
import com.tmt.manage.command.Commands.Signal;
import com.tmt.manage.config.Settings;
import com.tmt.manage.config.Settings.Datasource;
import com.tmt.manage.operation.Dialects;

/**
 * 系统备份 - 数据库备份 暂时不支持 mysql 的备份
 * 
 * @author lifeng
 */
public class BackupCommand implements Command {

	/**
	 * 备份
	 */
	@Override
	public void exec() {

		// 数据库链接
		Datasource datasource = Settings.me().getDatasource();

		// 备份数据库
		try {
			Dialects.adapted(datasource.getDb()).backup(datasource.getUrl(), datasource.getUsername(),
					datasource.getPassword());
		} catch (Exception e) {
		}

		// 发送信号
		this.sendSignal(Signal.newSignal(Sign.upgraded));
	}
}