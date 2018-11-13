package com.tmt.manage.widgets.theme.orange;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;

import com.tmt.manage.command.Commands;
import com.tmt.manage.command.Commands.Cmd;
import com.tmt.manage.command.Commands.Signal;
import com.tmt.manage.command.Receiver;
import com.tmt.manage.config.Settings;
import com.tmt.manage.widgets.BaseFrame;
import com.tmt.manage.widgets.ImageButton;
import com.tmt.manage.widgets.Progress;
import com.tmt.manage.widgets.ResourceManager;
import com.tmt.manage.widgets.theme.Theme.Action;

/**
 * 橙子主题的系统界面
 * 
 * @author lifeng
 */
public class OrangeApp extends BaseFrame implements Receiver {

	private StackLayout contentStack;
	private Composite content;
	private Composite logComposite;
	private StyledText logText;
	private Browser browser;
	private Progress progress;
	private Thread signalThread;
	private volatile Status status = Status.stop;

	private int height_top = 25;
	private int height_tools = 90;
	private int height_bottom = 25;

	@Override
	protected void createContents() {
		OrangeTheme theme = (OrangeTheme) this.theme;
		shell.setText(Settings.me().getServerName());
		shell.setImage(theme.logo().image());
		shell.setBackgroundImage(theme.background().image());
		GridLayout gl_shell = new GridLayout(1, false);
		gl_shell.marginWidth = 15;
		gl_shell.horizontalSpacing = 0;
		shell.setLayout(gl_shell);
		shell.setBackgroundMode(SWT.INHERIT_DEFAULT);

		// 控制按钮
		Composite top = new Composite(shell, SWT.TRANSPARENCY_ALPHA);
		GridData gd_top = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_top.heightHint = height_top;
		top.setLayoutData(gd_top);
		this.configureTops(top);

		// 快捷菜单
		Composite tools = new Composite(shell, SWT.TRANSPARENCY_ALPHA);
		GridData gd_tools = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_tools.heightHint = height_tools;
		tools.setLayoutData(gd_tools);
		this.configureTools(tools);

		// 内容展示
		content = new Composite(shell, SWT.NONE);
		GridData gd_content = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		content.setLayoutData(gd_content);
		this.configureContent(content);

		// 脚部
		Composite bottom = new Composite(shell, SWT.TRANSPARENCY_ALPHA);
		GridData gd_bottom = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_bottom.heightHint = height_bottom;
		bottom.setLayoutData(gd_bottom);
		this.configureBottoms(bottom);

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

	// 控制按钮的配置
	protected void configureTops(Composite top) {
		GridLayout gl_top = new GridLayout(2, false);
		gl_top.marginWidth = 0;
		gl_top.horizontalSpacing = 0;
		gl_top.marginHeight = 0;
		top.setLayout(gl_top);

		// left
		Label left = new Label(top, SWT.NONE);
		left.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		// close
		GridData gd_close = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_close.widthHint = 20;
		gd_close.heightHint = 20;
		ImageButton.builder(top).image(ResourceManager.getImage(OrangeApp.class, "关闭.png")).layout(gd_close)
				.click(() -> {
					shell.close();
				}).build();
	}

	// 工具栏的配置
	protected void configureTools(Composite tools) {
		GridLayout gl_tools = new GridLayout(2, false);
		gl_tools.horizontalSpacing = 0;
		gl_tools.marginWidth = 0;
		gl_tools.marginHeight = 0;
		tools.setLayout(gl_tools);

		// 主题
		OrangeTheme theme = (OrangeTheme) this.theme;

		// logo
		GridData gd_logo = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_logo.widthHint = height_tools;
		gd_logo.heightHint = height_tools;
		ImageButton.builder(tools).image(theme.logo().image()).hover(theme.logo().imageOn()).layout(gd_logo)
				.click(theme.logo().click()).build();

		// 子部快捷菜单
		Composite childTools = new Composite(tools, SWT.TRANSPARENCY_ALPHA);
		GridData gd_childTools = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_childTools.widthHint = height_tools;
		gd_childTools.heightHint = height_tools;
		childTools.setLayoutData(gd_childTools);

		// 按钮的大小
		int height_button = 64;
		int size = theme.actions().size();

		// 子部快捷菜单 - 布局
		GridLayout gl_childTools = new GridLayout(size, false);
		gl_childTools.horizontalSpacing = 10;
		gl_childTools.marginWidth = 40;
		gl_childTools.marginHeight = 8;
		childTools.setLayout(gl_childTools);

		// 按钮
		for (int i = 0; i < size; i++) {
			Action action = theme.actions().get(i);
			GridData gd_index = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
			gd_index.widthHint = height_button;
			gd_index.heightHint = height_button;
			ImageButton.builder(childTools).image(action.image()).hover(action.imageOn()).layout(gd_index)
					.click(action.click()).build();
		}
	}

	// 控制按钮的配置
	protected void configureContent(Composite content) {
		contentStack = new StackLayout();
		content.setLayout(contentStack);

		// 日志区域
		logComposite = new Composite(content, SWT.NONE);
		logComposite.setBackground(ResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gl_childTools = new GridLayout(1, false);
		gl_childTools.horizontalSpacing = 0;
		gl_childTools.marginWidth = 0;
		gl_childTools.marginHeight = 0;
		logComposite.setLayout(gl_childTools);

		// 日志
		logText = new StyledText(logComposite, SWT.READ_ONLY | SWT.WRAP);
		logText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		logText.setDoubleClickEnabled(false);
		logText.setEditable(false);

		// 进度条
		ProgressBar progressBar = new ProgressBar(logComposite, SWT.HORIZONTAL | SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		progressBar.setVisible(false);
		progressBar.setEnabled(false);
		progress = new Progress(progressBar);

		// 内容展示 - 浏览器
		browser = new Browser(content, SWT.NONE);

		// 默认展示
		contentStack.topControl = this.logComposite;
	}

	// 底部版权
	protected void configureBottoms(Composite bottom) {
		GridLayout gl_bottom = new GridLayout(1, false);
		gl_bottom.marginWidth = 0;
		gl_bottom.horizontalSpacing = 0;
		gl_bottom.marginHeight = 0;
		bottom.setLayout(gl_bottom);

		// left
		Label copyRight = new Label(bottom, SWT.NONE);
		copyRight.setText(Settings.me().getServerVersion());
		copyRight.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		copyRight.setForeground(ResourceManager.getColor(SWT.COLOR_WHITE));
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
	protected Point getInitialSize() {
		return new Point(1024, 768);
	}

	/**
	 * shell 样式
	 */
	@Override
	protected int getShellStyle() {
		return SWT.BORDER;
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

	// 显示日志
	private void showLog() {
		this.contentStack.topControl = this.logComposite;
		this.content.layout();
	}

	// 显示日志
	private void showBrowser(String url) {
		if (this.status == Status.start) {
			if (this.contentStack.topControl != this.browser) {
				this.contentStack.topControl = this.browser;
				this.content.layout();
			}
			this.browser.setUrl(url);
		}
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
			this.showLog();
			progress.start();
			break;
		case server_started:
			this.status = Status.start;
			progress.stop();
			break;
		case server_stoping:
			this.showLog();
			progress.start();
			break;
		case server_stoped:
			this.status = Status.stop;
			progress.stop();
			break;
		case browser_opened:
			break;
		case browser_closed:
			break;
		case window_close:
			shell.close();
			break;
		case window_exit:
			this.status = Status.exit;
			this.close();
			break;
		case browser:
			this.showBrowser(signal.getRemarks());
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