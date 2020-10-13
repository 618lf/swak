package com.swak.async.sharding;

import com.swak.groovy.ScriptExecutor;

import groovy.lang.Script;

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

/**
 * 脚本
 * 
 * @author lifeng
 * @date 2020年10月13日 下午2:35:32
 */
class ShardingScript extends Script {

	@Override
	public Object run() {
		return null;
	}
}
