package com.tmt.manage.widgets.theme.def;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;

import com.tmt.manage.command.Commands;
import com.tmt.manage.command.Commands.Cmd;
import com.tmt.manage.command.Commands.Signal;
import com.tmt.manage.command.Receiver;
import com.tmt.manage.config.Settings;
import com.tmt.manage.widgets.BaseApp;
import com.tmt.manage.widgets.CommandButton;
import com.tmt.manage.widgets.ConfirmCommandButton;
import com.tmt.manage.widgets.Progress;

/**
 * 
 * 基本的frame
 * 
 * @author lifeng
 */
public class DefApp extends BaseApp implements Receiver {

	private StyledText logText;
	private Button startButton;
	private Button stopButton;
	private Button openButton;
	private Progress progress;
	private Thread signalThread;
	private volatile Status status = Status.stop;

	/**
	 * 设置shell 的内容
	 */
	@Override
	protected void createContents() {
		shell.setLayout(new FillLayout());
		shell.setText(Settings.me().getServerName());
		Composite container = new Composite(shell, SWT.NONE);
		container.setLayout(new GridLayout(1, false));

		logText = new StyledText(container, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		logText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		logText.setDoubleClickEnabled(false);
		logText.setEditable(false);

		ProgressBar progressBar = new ProgressBar(container, SWT.HORIZONTAL | SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		progressBar.setVisible(false);
		progress = new Progress(progressBar);

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite.setLayout(new GridLayout(3, false));

		startButton = new CommandButton(Commands.nameCommand(Cmd.start), composite, SWT.NONE).getButton();
		startButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

		openButton = new CommandButton(Commands.nameCommand(Cmd.open), composite, SWT.NONE).getButton();
		openButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		
		stopButton = new ConfirmCommandButton(Commands.nameCommand(Cmd.stop), composite, SWT.NONE, "确认停止系统？").getButton();
		stopButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

		// 信号处理线程
		signalThread = new Thread(() -> {
			while (status != Status.exit) {
				try {
					Signal signal = Commands.receiveSignal();
					if (signal != null) {
						this.handleSignal(signal);
					}
					Thread.sleep(100);
				} catch (InterruptedException e) {
					break;
				}
			}
		});
		signalThread.setDaemon(true);
		signalThread.start();
	}

	/**
	 * 设置 shell 的事件
	 */
	@Override
	protected void configureShell() {
		shell.addShellListener(new ShellListener() {
			@Override
			public void shellActivated(ShellEvent event) {
				Commands.nameCommand(Cmd.init).exec();
			}

			@Override
			public void shellClosed(ShellEvent event) {
				if (status != Status.exit) {
					event.doit = false;
					int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
					MessageBox messageBox = new MessageBox(shell, style);
					messageBox.setText("提示");
					messageBox.setMessage("确认停止系统？");
					if (messageBox.open() == SWT.YES) {
						Commands.nameCommand(Cmd.exit).exec();
					}
				} else {
					int style = SWT.APPLICATION_MODAL | SWT.YES;
					MessageBox messageBox = new MessageBox(shell, style);
					messageBox.setText("提示");
					messageBox.setMessage("系统停止成功,点击“Yes”关闭小工具！");
					event.doit = messageBox.open() == SWT.YES;
				}
			}

			@Override
			public void shellDeactivated(ShellEvent event) {
				Commands.nameCommand(Cmd.Deactivated).exec();
			}

			@Override
			public void shellDeiconified(ShellEvent event) {
				Commands.nameCommand(Cmd.Deiconified).exec();
			}

			@Override
			public void shellIconified(ShellEvent event) {
				Commands.nameCommand(Cmd.Iconified).exec();
			}
		});
		shell.addDisposeListener(e -> {
			signalThread.interrupt();
			Commands.nameCommand(Cmd.Dispose).exec();
		});
	}

	/**
	 * shell 大小
	 */
	@Override
	protected Point getShellSize(Rectangle clientArea) {
		return new Point(425, 525);
	}

	/**
	 * shell 样式
	 */
	@Override
	protected int getShellStyle() {
		return SWT.CLOSE;
	}

	/**
	 * 处理信号
	 */
	@Override
	public void handleSignal(Signal signal) {
		Display.getDefault().asyncExec(() -> {
			this.doSignal(signal);
		});
	}

	/**
	 * 处理信号
	 * 
	 * @param signal
	 */
	private void doSignal(Signal signal) {
		if (shell.isDisposed() || this.status == Status.exit) {
			return;
		}
		switch (signal.getSign()) {
		case server_starting:
			startButton.setEnabled(false);
			stopButton.setEnabled(false);
			progress.start();
			break;
		case server_started:
			this.status = Status.start;
			startButton.setEnabled(false);
			stopButton.setEnabled(true);
			progress.stop();
			break;
		case server_stoping:
			startButton.setEnabled(false);
			stopButton.setEnabled(false);
			progress.start();
			break;
		case server_stoped:
			this.status = Status.stop;
			stopButton.setEnabled(false);
			startButton.setEnabled(true);
			progress.stop();
			break;
		case browser_opened:
			openButton.setEnabled(false);
			break;
		case browser_closed:
			openButton.setEnabled(true);
			break;
		case window_close:
			shell.close();
			break;
		case window_exit:
			this.status = Status.exit;
			this.close();
			break;
		case browser:
			this.status = Status.exit;
			this.close();
			break;
		case log:
			logText.append(signal.getRemarks());
			logText.setTopIndex(Integer.MAX_VALUE);
			break;
		}
	}

	/**
	 * 系统状态
	 * 
	 * @author lifeng
	 */
	public static enum Status {
		start, stop, exit
	}
}
