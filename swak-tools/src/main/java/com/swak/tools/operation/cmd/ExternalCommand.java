package com.swak.tools.operation.cmd;

import com.swak.tools.config.Settings;
import com.swak.tools.operation.Command;

/**
 * 执行外部命令
 * 
 * @author lifeng
 */
public interface ExternalCommand extends Command {

	
	/**
	 * 根据系统类型判断升级的代码
	 * 
	 * @return
	 */
	default String getCommand(String name) {
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

	default boolean isWindows(String OS) {
		return OS.indexOf("windows") >= 0;
	}

	default boolean isMacOS(String OS) {
		return OS.indexOf("mac") >= 0;
	}

	default boolean isLinux(String OS) {
		return OS.indexOf("linux") >= 0;
	}
	
	/**
	 * 执行外部命令
	 * 
	 * @param strcmd
	 * @return
	 */
	default boolean runExternalCmd(String strcmd) {
		// 执行命令
		Runtime rt = Runtime.getRuntime();
		Process ps = null;
		try {
			ps = rt.exec(strcmd);
			ps.waitFor();
		} catch (Exception e) {
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