package com.sample.tools.operation.cmd;

/**
 * 启动服务的命令
 * 
 * @author lifeng
 */
public class StarterCommand implements ExternalCommand {

	@Override
	public void exec() {
		String cmd = this.getCommand("starter");
		this.runExternalCmd(cmd);
	}
}