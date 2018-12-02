package com.tmt.manage.command.impl;

import com.tmt.manage.command.Command;
import com.tmt.manage.config.Settings;

/**
 * 执行外部命令
 * 
 * @author lifeng
 */
public abstract class ExternalCommand implements  Command {


	/**
	 * 更具系统类型判断升级的代码
	 * 
	 * @return
	 */
	protected String getCommand(String name) {
		String command = null;
		String OS = System.getProperty("os.name").toLowerCase();
		if (isWindows(OS)) {
			command = "cmd /c " + Settings.me().getBasePath() + "/" + name + ".bat";
		} else if (isMacOS(OS)) {

		} else if (isLinux(OS)) {
			command = Settings.me().getBasePath() + "/" + name + ".sh";
		} else {

		}
		return command;
	}

	private boolean isWindows(String OS) {
		return OS.indexOf("windows") >= 0;
	}

	private boolean isMacOS(String OS) {
		return OS.indexOf("mac") >= 0;
	}

	private boolean isLinux(String OS) {
		return OS.indexOf("linux") >= 0;
	}

	/**
	 * 执行外部命令
	 * 
	 * @param strcmd
	 */
	protected boolean runExternalCmd(String strcmd) {

		// 执行命令
		Runtime rt = Runtime.getRuntime();
		Process ps = null;
		try {
			ps = rt.exec(strcmd);
			ps.waitFor();
		} catch (Exception e) {
			this.log(e.getMessage());
			if (ps != null) {
				ps.destroy();
			}
			return false;
		}

		// 执行命令 -- 成功
		int i = ps.exitValue(); // 接收执行完毕的返回值
		ps.destroy(); // 销毁子进程
		ps = null;
		return i == 0;
	}
}
