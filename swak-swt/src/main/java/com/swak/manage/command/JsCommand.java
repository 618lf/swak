package com.swak.manage.command;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.manage.App;
import com.swak.manage.command.Commands.Cmd;

/**
 * 支持 JS 发送命令
 * 
 * @author lifeng
 */
public class JsCommand extends BrowserFunction {

	private static Logger LOGGER = LoggerFactory.getLogger(App.class);
	private static final String COMMAND_NAME = "_JS_COMMAND";

	/**
	 * 创建Js命令
	 * 
	 * @param browser
	 */
	public JsCommand(Browser browser) {
		super(browser, COMMAND_NAME);
	}

	/**
	 * 执行命令, 没有返回值
	 */
	@Override
	public Object function(Object[] arguments) {

		// 基本的校验
		if (!(arguments != null && arguments.length >= 1)) {
			LOGGER.error("Js run command error: arguments[" + arguments + "]");
			return null;
		}
		
		// 执行命令
		try {
			Cmd cmd = Cmd.valueOf(String.valueOf(arguments[0]));
			Object param =  arguments.length >1? arguments[1]: null;
			Commands.nameCommand(cmd).exec(param);
		} catch (Exception e) {
			LOGGER.error("Js run command error: arguments[" + arguments + "]");
		}
		
		// 无返回值
		return null;
	}
	
	/**
	 * 绑定此命令到浏览器对象
	 * 
	 * @param browser
	 * @return
	 */
	public static JsCommand bind(Browser browser) {
		return new JsCommand(browser);
	}
}