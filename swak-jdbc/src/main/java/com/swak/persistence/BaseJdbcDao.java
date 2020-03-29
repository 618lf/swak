package com.swak.persistence;

import com.swak.Constants;
import com.swak.entity.Page;
import com.swak.entity.Parameters;
import com.swak.persistence.dialect.Dialect;
import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * 简单的基于JDBC的dao实现
 *
 * @author: lifeng
 * @date: 2020/3/28 13:45
 */
public class BaseJdbcDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private Dialect dialect;

    /**
     * 获取数据
     *
     * @param sql       查询的sql
     * @param param     查询的参数
     * @param rowMapper 结果映射
     * @return 查询的数据
     * @author lifeng
     * @date 2020/3/28 13:46
     */
    public <T> T get(String sql, Map<String, ?> param, RowMapper<T> rowMapper) {
        return jdbcTemplate.queryForObject(sql, param, rowMapper);
    }

    /**
     * 查询数据
     *
     * @param sql   语句
     * @param param 参数
     * @author lifeng
     * @date 2020/3/28 13:46
     */
    public void insert(String sql, Map<String, ?> param) {
        jdbcTemplate.update(sql, param);
    }

    /**
     * 更新数据
     *
     * @param sql   语句
     * @param param 参数
     * @author lifeng
     * @date 2020/3/28 13:46
     */
    public void update(String sql, Map<String, ?> param) {
        jdbcTemplate.update(sql, param);
    }

    /**
     * 删除数据
     *
     * @param sql   语句
     * @param param 参数
     * @author lifeng
     * @date 2020/3/28 13:46
     */
    public void delete(String sql, Map<String, ?> param) {
        jdbcTemplate.update(sql, param);
    }

    /**
     * 查询分页数据
     *
     * @param sql       语句
     * @param qc        查询条件
     * @param param     分页参数
     * @param rowMapper 结果映射
     * @return 查询的数据
     * @author lifeng
     * @date 2020/3/28 13:46
     */
    public <T> Page page(String sql, QueryCondition qc, Parameters param, RowMapper<T> rowMapper) {

        // 转大小
        String valueSql = StringUtils.upperCase(sql);

        // 如果已经设置了 WHERE
        if (valueSql.endsWith(Constants.WHERE)) {
            valueSql = String.format("%s 1=1 %s", valueSql, qc.toString());
        } else {
            valueSql = String.format("%s %s", valueSql, qc.toString());
        }

        // 排序条件
        if (StringUtils.isNotBlank(qc.getOrderByClause())) {
            valueSql = String.format("%s ORDER BY %s", valueSql, qc.getOrderByClause());
        }

        // 查询数量
        String countSql = String.format("SELECT COUNT(1) C FROM (%s)", valueSql);

        // 分页参数设置
        int pageNum = param.getPageIndex();
        int pageSize = param.getPageSize();
        Integer count = 0;
        List<T> lst;
        if (pageNum == Parameters.NO_PAGINATION || pageSize == Parameters.NO_PAGINATION) {
            lst = jdbcTemplate.query(valueSql, rowMapper);
        } else {
            count = jdbcTemplate.queryForObject(countSql, Maps.newHashMap(), Integer.class);
            count = count == null ? 0 : count;
            if (count == 0) {
                lst = Lists.newArrayList();
            } else {
                int pageCount = getPageCount(count, pageSize);
                if (pageNum > pageCount) {
                    pageNum = pageCount;
                }
                lst = jdbcTemplate.query(dialect.getLimitString(valueSql, (pageNum - 1) * pageSize, pageSize),
                        rowMapper);
            }
        }
        param.setRecordCount(count);
        return new Page(param, lst);
    }

    /**
     * 总页数
     *
     * @param recordCount 记录数
     * @param pageSize    每页数
     * @return 页数
     * @author lifeng
     * @date 2020/3/28 13:50
     */
    private int getPageCount(int recordCount, int pageSize) {
        if (recordCount == 0) {
            return 0;
        }
        return recordCount % pageSize > 0 ? ((recordCount / pageSize) + 1) : (recordCount / pageSize);
    }

    /**
     * 查询数据
     *
     * @param sql       语句
     * @param param     分页参数
     * @param rowMapper 结果映射
     * @return 查询的数据
     * @author lifeng
     * @date 2020/3/28 13:46
     */
    public <T> List<T> query(String sql, Map<String, ?> param, RowMapper<T> rowMapper) {
        return jdbcTemplate.query(sql, param, rowMapper);
    }

    /**
     * 查询数量
     *
     * @param sql   语句
     * @param param 分页参数
     * @return 查询的数量
     * @author lifeng
     * @date 2020/3/28 13:46
     */
    public Integer count(String sql, Map<String, ?> param) {
        return jdbcTemplate.queryForObject(sql, param, Integer.class);
    }
}
