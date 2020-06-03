package com.sample.tools.operation.ops;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.sample.tools.config.Settings;
import com.sample.tools.config.Settings.Datasource;
import com.sample.tools.operation.AbsOps;
import com.sample.tools.operation.Dialects;
import com.sample.tools.operation.OpsException;
import com.sample.tools.operation.OpsFile;
import com.sample.tools.operation.OpsFile.OpsEntry;

/**
 * 执行 sql 的操作
 * 
 * @author lifeng
 */
public class SqlOps extends AbsOps {

	// SQL 黑名单
	private String[] backlist = new String[] { "delete", "drop" };

	/**
	 * 更新数据库文件
	 * 
	 * @throws Exception
	 */
	@Override
	protected void doInnerOps(OpsFile file) throws Exception {
		try {
			if (file.continuAbled()) {
				List<OpsEntry> sqls = file.sqls();
				if (sqls != null && sqls.size() > 0) {
					this.execSql(sqls);
				}
			}
		} catch (Exception e) {
			throw new OpsException("执行SQL失败");
		}
	}

	// 执行 sql 文件
	private void execSql(List<OpsEntry> entrys) throws SQLException {
		Connection conn = null;
		try {
			conn = this.open();
			Boolean autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(Boolean.FALSE);
			for (OpsEntry entry : entrys) {
				String content = new String(entry.getData(), "utf-8");
				if (content == null || "".equals(content)) {
					continue;
				}
				String[] sqls = content.split(";");
				for (String sql : sqls) {
					execSql(conn, sql);
				}
			}
			conn.setAutoCommit(autoCommit);
		} catch (Exception e) {
			conn.rollback();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
			}
		}
	}

	// 执行sql
	private void execSql(Connection conn, String sql) throws SQLException {
		if (sql == null || "".equals(sql) || inBacklist(sql)) {
			return;
		}
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.executeUpdate();
		stmt.close();
	}

	// 是否再黑名单中
	private boolean inBacklist(String sql) {
		for (String back : backlist) {
			if (sql.toLowerCase().indexOf(back) != -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 打开驱动
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private Connection open() throws ClassNotFoundException, SQLException {

		// 数据库链接
		Datasource datasource = Settings.me().getDatasource();

		// 更具配置返回具体的链接
		return Dialects.adapted(datasource.getDb()).open(datasource.getUrl(), datasource.getUsername(),
				datasource.getPassword());
	}
}
