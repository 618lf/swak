package com.swak.fx.operation.ops;

import com.swak.fx.config.Settings;
import com.swak.fx.config.Settings.Datasource;
import com.swak.fx.operation.AbsOps;
import com.swak.fx.operation.Dialects;
import com.swak.fx.operation.OpsException;
import com.swak.fx.operation.OpsFile;

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
