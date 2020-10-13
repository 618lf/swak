package com.swak.async.sharding;

import com.swak.groovy.ScriptExecutor;

/**
 * 表达式
 * 
 * @author lifeng
 * @date 2020年10月13日 下午2:23:13
 */
public class ShardingScriptExecutor extends ScriptExecutor {

	@Override
	protected String getBaseScriptClass() {
		return ShardingScript.class.getName();
	}
}
