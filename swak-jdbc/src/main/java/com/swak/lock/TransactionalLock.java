package com.swak.lock;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

import org.springframework.jdbc.support.JdbcUtils;

import com.swak.exception.DataAccessException;
import com.swak.persistence.DataSourceHolder;

/**
 * 数据库的锁, 单独使用连接来锁住表
 * 
 * @author lifeng
 */
public class TransactionalLock implements Lock {

	private static final String LOCK_SQL = "SELECT 1 C FROM SYS_LOCK WHERE NAME = ? FOR UPDATE";
	private String name;

	public TransactionalLock(String name) {
		this.name = name;
	}

	/**
	 * 在锁中执行这段代码
	 */
	@Override
	public <T> T doHandler(Supplier<T> handler) {
		Connection con = DataSourceHolder.getConnection();
		try {
			return this.handleInConnection(handler, con);
		} catch (SQLException ex) {
			throw new DataAccessException("Could not open Connection", ex);
		} finally {
			DataSourceHolder.releaseConnection(con);
		}
	}

	private <T> T handleInConnection(Supplier<T> handler, Connection con) throws SQLException {
		Boolean autoCommit = con.getAutoCommit();
		CallableStatement stmt = null;
		try {
			con.setAutoCommit(Boolean.FALSE);
			stmt = con.prepareCall(LOCK_SQL);
			stmt.setString(1, name);
			stmt.execute();
			Integer locked = stmt.getInt(1);
			if (locked != null && locked > 0) {
				return handler.get();
			}
			throw new DataAccessException("Please Init Lock-Name In Table SYS_LOCK");
		} catch (Exception e) {
			throw new DataAccessException("Could not lock table SYS_LOCK", e);
		} finally {
			con.setAutoCommit(autoCommit);
			JdbcUtils.closeStatement(stmt);
		}
	}

	@Override
	public String name() {
		return name;
	}

	/**
	 * 方法执行完（正常结束，或异常结束）自动的释放锁
	 */
	@Override
	public boolean unlock() {
		return true;
	}
}