package com.tmt.commands;

import com.tmt.manage.command.Command;
import com.tmt.manage.command.Commands;
import com.tmt.manage.command.Commands.Cmd;
import com.tmt.manage.command.Commands.Sign;
import com.tmt.manage.command.Commands.Signal;

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

	/**
	 * 
	 */
	@Override
	public void exec(Object param) {
		String cmd = param != null ? param.toString() : "index";
		String url = "http://www.baidu.com";
		if ("index".equals(cmd)) {
			url = "https://www.baidu.com";
		} else if ("b1".equals(cmd)) {
			url = "https://www.jd.com";
		} else if ("b2".equals(cmd)) {
			url = "https://www.taobao.com";
		} else if ("b3".equals(cmd)) {
			url = "https://www.2345.com";
		} else if ("b4".equals(cmd)) {
			url = "https://v.6.cn/?src=z9weij1204";
		} else if ("b5".equals(cmd)) {
			url = "https://dianying.2345.com/";
		} else if ("exit".equals(cmd)) {
			Commands.nameCommand(Cmd.exit).exec();
			return;
		}
        
		// 发送打开页面的信号
		this.sendSignal(Signal.newSignal(Sign.browser, url));
	}
}
