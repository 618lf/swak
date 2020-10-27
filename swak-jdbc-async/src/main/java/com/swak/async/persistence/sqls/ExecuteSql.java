package com.swak.async.persistence.sqls;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.Sql;
import com.swak.async.persistence.SqlParam;
import com.swak.async.persistence.SqlResult;
import com.swak.async.persistence.maps.UpdateMapper;
import com.swak.utils.Lists;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

/**
 * 可执行的Sql
 * 
 * @author lifeng
 * @date 2020年10月12日 下午8:53:18
 */
public abstract class ExecuteSql<T> implements Sql<T> {

	protected static Logger logger = LoggerFactory.getLogger(Sql.class);

	/**
	 * 解析脚本
	 * 
	 * @param entity
	 * @param query
	 * @return
	 */
	public abstract String parseScript(SqlParam<T> param);

	/**
	 * 解析参数
	 * 
	 * @param entity
	 * @param query
	 * @return
	 */
	public abstract List<Object> parseParams(SqlParam<T> param);

	/**
	 * 映射
	 * 
	 * @param <U>
	 * @return
	 */
	public abstract <U> RowMapper<U> rowMap();

	/**
	 * 执行： execute
	 * 
	 * @param client
	 * @param sql
	 * @return
	 */
	@Override
	public CompletableFuture<SqlResult> execute(SqlClient client, SqlParam<T> param) {
		String sql = this.parseScript(param);
		List<Object> params = this.parseParams(param);
		if (logger.isDebugEnabled()) {
			logger.debug("Sql:{}、Param：{}", sql, params);
		}
		CompletableFuture<SqlResult> future = new CompletableFuture<>();
		client.preparedQuery(sql).execute(Tuple.wrap(params), (res) -> {
			if (res.cause() != null) {
				logger.error("执行Sql:{}、Param：{}发生异常：", sql, params, res.cause());
				future.completeExceptionally(res.cause());
			} else {
				this.rowMappers(future, res.result(), this.rowMap());
			}
		});
		return future;
	}

	protected <U> void rowMappers(CompletableFuture<SqlResult> future, RowSet<Row> rows, RowMapper<U> rowMapper) {
		try {
			if (rowMapper != null && rowMapper instanceof UpdateMapper) {
				future.complete(new SqlResult(rows.rowCount()));
			} else if (rowMapper != null) {
				List<U> ts = Lists.newArrayList();
				RowIterator<Row> datas = rows.iterator();
				int rowNum = 0;
				while (datas.hasNext()) {
					Row row = datas.next();
					ts.add(rowMapper.mapRow(row, rowNum));
					rowNum++;
				}
				future.complete(new SqlResult(ts));
			} else {
				future.complete(new SqlResult(Lists.newArrayList()));
			}
		} catch (Exception e) {
			logger.error("执行Sql异常：", e);
			future.completeExceptionally(e);
		}
	}
}
