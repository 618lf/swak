package com.swak.async.persistence.sqls;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.Sql;
import com.swak.persistence.QueryCondition;
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

	/**
	 * 解析脚本
	 * 
	 * @param entity
	 * @param query
	 * @return
	 */
	public abstract String parseScript(T entity, QueryCondition query);

	/**
	 * 解析参数
	 * 
	 * @param entity
	 * @param query
	 * @return
	 */
	public abstract List<Object> parseParams(T entity, QueryCondition query);

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
	public <U> CompletableFuture<List<U>> execute(SqlClient client, T entity, QueryCondition query) {
		CompletableFuture<List<U>> future = new CompletableFuture<>();
		client.preparedQuery(this.parseScript(entity, query)).execute(Tuple.wrap(this.parseParams(entity, query)),
				(res) -> {
					if (res.cause() != null) {
						future.completeExceptionally(res.cause());
					} else {
						this.rowMappers(future, res.result(), this.rowMap());
					}
				});
		return future;
	}

	protected <U> void rowMappers(CompletableFuture<List<U>> future, RowSet<Row> rows, RowMapper<U> rowMapper) {
		try {
			List<U> ts = Lists.newArrayList();
			if (rowMapper != null) {
				RowIterator<Row> datas = rows.iterator();
				int rowNum = 0;
				while (datas.hasNext()) {
					Row row = datas.next();
					ts.add(rowMapper.mapRow(row, rowNum));
					rowNum++;
				}
			}
			future.complete(ts);
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
	}
}
