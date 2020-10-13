package com.swak.async.persistence;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.DisposableBean;

import com.swak.utils.Lists;

import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

/**
 * 异步jdbc操作模板
 * 
 * @author lifeng
 * @date 2020年9月30日 下午8:06:14
 */
public class JdbcTemplate implements DisposableBean {

	/**
	 * 对应的 sql 操作池
	 */
	private final Pool pool;

	public JdbcTemplate(Pool pool) {
		this.pool = pool;
	}

	@Override
	public void destroy() throws Exception {
		if (pool != null) {
			pool.close();
		}
	}

	/**
	 * 执行Sql
	 * 
	 * @param sql   sql语句
	 * @param param 参数
	 * @return 执行结果
	 */
	public CompletableFuture<Void> update(String sql, List<Object> params) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		try {
			this.pool.preparedQuery(sql).execute(Tuple.wrap(params), (res) -> {
				if (res.cause() != null) {
					future.completeExceptionally(res.cause());
				} else {
					future.complete(null);
				}
			});
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
		return future;
	}

	/**
	 * 查询
	 * 
	 * @param <T>       实体对象
	 * @param sql       查询的sql语句
	 * @param rowMapper 转换映射
	 * @return 查询结果
	 */
	public <T> CompletableFuture<List<T>> query(String sql, RowMapper<T> rowMapper) {
		return this.query(sql, Lists.newArrayList(), rowMapper);
	}

	/**
	 * 查询
	 * 
	 * @param <T>       实体对象
	 * @param sql       查询的sql语句
	 * @param param     参数
	 * @param rowMapper 转换映射
	 * @return 查询结果
	 */
	public <T> CompletableFuture<List<T>> query(String sql, List<Object> params, RowMapper<T> rowMapper) {
		CompletableFuture<List<T>> future = new CompletableFuture<>();
		pool.preparedQuery(sql).execute(Tuple.wrap(params), (res) -> {
			if (res.cause() != null) {
				future.completeExceptionally(res.cause());
			} else {
				this.rowMappers(future, res.result(), rowMapper);
			}
		});
		return future;
	}

	/**
	 * 查询数量
	 * 
	 * @param sql
	 * @param param
	 * @return
	 */
	public CompletableFuture<Integer> count(String sql) {
		return this.count(sql, Lists.newArrayList());
	}

	/**
	 * 查询数量
	 * 
	 * @param sql
	 * @param param
	 * @return
	 */
	public CompletableFuture<Integer> count(String sql, List<Object> params) {
		CompletableFuture<List<Integer>> future = new CompletableFuture<>();
		pool.preparedQuery(sql).execute(Tuple.wrap(params), (res) -> {
			if (res.cause() != null) {
				future.completeExceptionally(res.cause());
			} else {
				this.rowMappers(future, res.result(), (row, i) -> {
					return row.getInteger(1);
				});
			}
		});
		return future.thenApply(ls -> {
			return ls != null && ls.size() > 0 ? ls.get(0) : 0;
		});
	}

	private <T> void rowMappers(CompletableFuture<List<T>> future, RowSet<Row> rows, RowMapper<T> rowMapper) {
		try {
			List<T> ts = Lists.newArrayList();
			RowIterator<Row> datas = rows.iterator();
			int rowNum = 0;
			while (datas.hasNext()) {
				Row row = datas.next();
				ts.add(rowMapper.mapRow(row, rowNum));
				rowNum++;
			}
			future.complete(ts);
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
	}
}
