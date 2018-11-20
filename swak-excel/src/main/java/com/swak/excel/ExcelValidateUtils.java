package com.swak.excel;

import java.io.InputStreamReader;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.swak.Constants;
import com.swak.utils.IOUtils;
import com.swak.utils.StringUtils;

/**
 * Excel 校验工具
 * 
 * @author root
 */
public class ExcelValidateUtils {

	private static ScriptEngineManager SEM = new ScriptEngineManager();
	private static ScriptEngine SE = SEM.getEngineByName("JavaScript");
	static {
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(ExcelValidateUtils.class.getResourceAsStream("excel-validate.js"),
					Constants.DEFAULT_ENCODING);
			SE.eval(reader);
		} catch (Exception e) {
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}

	/**
	 * 校验值的有效性
	 * 
	 * @param value
	 * @param verifyFormat
	 * @return
	 */
	public static String validate(String value, String rules) {
		if (StringUtils.isBlank(rules)) {
			return null;
		}
		Bindings params = SE.getBindings(ScriptContext.ENGINE_SCOPE);
		params.put("value", value);
		params.put("rules", rules);
		try {
			return (String) SE.eval(new StringBuilder("validator.doValidator(value, rules)").toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}