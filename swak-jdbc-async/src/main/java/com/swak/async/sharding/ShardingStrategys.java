package com.swak.async.sharding;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;
import com.swak.annotation.Table;
import com.swak.async.persistence.SqlParam;
import com.swak.utils.StringUtils;

/**
 * 分库分表的策略
 * 
 * @author lifeng
 * @date 2020年10月13日 上午10:21:06
 */
public class ShardingStrategys {

	/**
	 * 脚本执行器
	 */
	static ShardingScriptExecutor shardingScriptExecutor = new ShardingScriptExecutor();

	/**
	 * 算术表达式规则: 连接符号 + 字段/属性 + 运算表达式
	 */
	static Pattern algorithmExpression = Pattern.compile("([-_]?)([a-zA-Z_0-9]+)(.*)");

	/**
	 * 策略
	 */
	static Map<Class<ShardingStrategy>, ShardingStrategy> strategys = Maps.newConcurrentMap();

	/**
	 * 分表
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> String shardingTable(SqlParam<T> param) {

		// 表定义
		Table define = param.table.define;
		if (define == null) {
			return param.table.name;
		}

		// 执行分表表达式 _id%12
		String algorithm = define.shardingAlgorithm();
		if (StringUtils.isNotBlank(algorithm)) {
			return shardingTableWithAlgorithm(algorithm, param);
		}

		// 执行分表类
		Class<?> shardingClass = define.shardingClass();
		if (shardingClass != null && ShardingStrategy.class.isAssignableFrom(shardingClass)) {
			return shardingTableWithClass((Class<ShardingStrategy>) shardingClass, param);
		}

		// 没有分片配置
		return param.table.name;
	}

	private static <T> String shardingTableWithAlgorithm(String algorithm, SqlParam<T> param) {

		Matcher matcher = algorithmExpression.matcher(algorithm);
		if (!matcher.find()) {
			throw new RuntimeException("分片表达式不符合规范：" + algorithm);
		}

		// 相应表达式
		String linkString = matcher.group(1);
		String fieldString = StringUtils.convertColumn2Property(matcher.group(2));
		String algorithmString = matcher.group(3);

		// 取数据
		Object value = param.getFieldValue(fieldString);

		// 没有取到值
		if (value == null) {
			throw new RuntimeException("分表属性值缺失：" + fieldString);
		}
		Map<String, Object> context = Maps.newHashMap();
		context.put("fieldString", value);
		return param.table.name + linkString + shardingScriptExecutor.execute(fieldString + algorithmString, context);
	}

	private static <T> String shardingTableWithClass(Class<ShardingStrategy> shardingClass, SqlParam<T> param) {
		ShardingStrategy strategy = strategys.computeIfAbsent(shardingClass, (key) -> {
			try {
				return shardingClass.newInstance();
			} catch (InstantiationException e) {
				return null;
			} catch (IllegalAccessException e) {
				return null;
			}
		});
		if (strategy != null) {
			return strategy.sharding(param);
		}
		throw new RuntimeException("分片策略类创建失败：" + shardingClass.getName());
	}
}
