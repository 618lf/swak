package com.tmt.commands;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.tmt.manage.command.Command;
import com.tmt.manage.command.Commands.Sign;
import com.tmt.manage.command.Commands.Signal;
import com.tmt.manage.command.impl.ExternalCommand;

/**
 * 启动
 * 
 * @author lifeng
 */
public class StarterCommand extends ExternalCommand implements Command {

	@Override
	public void exec() {
		Display.getDefault().syncExec(() -> {
			int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
			MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell(), style);
			messageBox.setText("提示");
			messageBox.setMessage("系统将进入正常服务，点击确定继续?");
			if (messageBox.open() == SWT.YES) {
				new StopCommand() {
					@Override
					protected void stoped() {
						doStarter();
					}
				}.exec();
			}
		});
	}
	
	/**
	 * 执行一个批处理文件启动升级程序，启动之后发送退出系统的信号
	 */
	private void doStarter() {
		this.log("系统进入升级模式...");

		// 得到外部命令
		String command = this.getCommand("starter");
		if (command == null) {
			this.log("暂不支持此系统的升级");
			return;
		}

		// 执行外部命令
		boolean result = this.runExternalCmd(command);

		// 判断是否执行成功
		if (result) {
			this.log("进入升级模式成功");
			this.sendSignal(Signal.newSignal(Sign.window_close));
		}

		// 执行失败
		else {
			this.log("进入升级模式失败，请重试");
		}
	}
}
