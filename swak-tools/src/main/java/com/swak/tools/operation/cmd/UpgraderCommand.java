package com.swak.tools.operation.cmd;

/**
 * 升级平台的命令
 * 
 * @author lifeng
 */
public class UpgraderCommand implements ExternalCommand {

	@Override
	public void exec() {
		String cmd = this.getCommand("upgrader");
		this.runExternalCmd(cmd);
	}
}