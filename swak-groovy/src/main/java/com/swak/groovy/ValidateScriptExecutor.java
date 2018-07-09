package com.swak.groovy;

/**
 * 只是一个例子
 * @author lifeng
 */
@Deprecated
public class ValidateScriptExecutor extends ScriptExecutor{

	@Override
	protected String getBaseScriptClass() {
		return ValidateScript.class.getName();
	}
}