package com.swak.commands;

import com.swak.manage.command.Command;
import com.swak.manage.command.Commands.Sign;
import com.swak.manage.command.Commands.Signal;

/**
 * 显示url 的命令
 * 
 * @author lifeng
 */
public class UrlCommand implements Command {

	/**
	 * 执行命令, 默认
	 */
	@Override
	public void exec() {
		this.exec("index");
	}

	@Override
	public void exec(Object param) {
		String cmd = param != null ? param.toString() : "index";
		String url = "http://www.baidu.com";
		if ("index".equals(cmd)) {
			url = "https://www.baidu.com";
		} else if ("member".equals(cmd)) {
			url = "https://www.jd.com";
		} else if ("order".equals(cmd)) {
			url = "https://www.taobao.com";
		} else if ("notice".equals(cmd)) {
			url = "https://www.2345.com";
		} else if ("settings".equals(cmd)) {
			url = "https://dianying.2345.com/";
		}
        
		// 发送打开页面的信号
		this.sendSignal(Signal.newSignal(Sign.browser, url));
	}
}
