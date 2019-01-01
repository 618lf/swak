package com.swak.manage.operation.ops;

import com.swak.manage.config.Settings;
import com.swak.manage.config.Settings.Datasource;
import com.swak.manage.operation.AbsOps;
import com.swak.manage.operation.Dialects;
import com.swak.manage.operation.OpsException;
import com.swak.manage.operation.OpsFile;

/**
 * 每次更新前先备份数据库文件 的操作
 * 
 * @author lifeng
 */
public class BackupOps extends AbsOps {

	/**
	 * 每次更新前先备份数据库文件
	 * 
	 * @throws Exception
	 */
	@Override
	protected void doInnerOps(OpsFile file) throws Exception {
		// 数据库链接
		Datasource datasource = Settings.me().getDatasource();
		// 备份数据库
		try {
			Dialects.adapted(datasource.getDb()).backup(datasource.getUrl(), datasource.getUsername(),
					datasource.getPassword());
		} catch (Exception e) {
			throw new OpsException("更新前先备份数据库文件失败");
		}
	}
}
