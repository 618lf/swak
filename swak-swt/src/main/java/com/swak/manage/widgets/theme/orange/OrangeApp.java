package com.swak.manage.widgets.theme.orange;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;

import com.swak.manage.command.Commands;
import com.swak.manage.command.JsCommand;
import com.swak.manage.command.Receiver;
import com.swak.manage.command.Commands.Cmd;
import com.swak.manage.command.Commands.Signal;
import com.swak.manage.config.Settings;
import com.swak.manage.widgets.BaseApp;
import com.swak.manage.widgets.ImageButton;
import com.swak.manage.widgets.ImageButtonGroup;
import com.swak.manage.widgets.MD5s;
import com.swak.manage.widgets.Progress;
import com.swak.manage.widgets.ResourceManager;
import com.swak.manage.widgets.theme.Theme.Action;

/**
 * 橙子主题的系统界面
 * 
 * @author lifeng
 */
public class OrangeApp extends BaseApp implements Receiver, MouseListener, MouseMoveListener, Listener {

	private StackLayout contentStack;
	private Composite content;
	private Composite logComposite;
	private StyledText logText;
	private Browser browser;
	private Progress progress;
	private Thread signalThread;
	private volatile Status status = Status.stop;
	private Clipboard clipboard;
	private Thread clipboardThread;
	private volatile String clipboardMD5 = null;

	private int height_top = 35;
	private int height_tools = 70;
	private int height_bottom = 25;
	private int margin_width = 15;

	// 清除默认样式
	private void clearGridLayout(GridLayout gridLayout) {
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
	}

	@Override
	protected void createContents() {
		this.configureClipboard();
		OrangeTheme theme = (OrangeTheme) this.theme;
		shell.setText(Settings.me().getServer().getName());
		if (theme.logo() != null) {
			shell.setImage(theme.logo().image());
		}
		if (theme.background() != null && theme.background().image() != null) {
			shell.setBackgroundImage(theme.background().image());
		}
		if (theme.background() != null && theme.background().color() != null) {
			shell.setBackground(theme.background().color());
		}
		GridLayout gl_shell = new GridLayout(1, false);
		this.clearGridLayout(gl_shell);
		shell.setLayout(gl_shell);
		shell.setBackgroundMode(SWT.INHERIT_DEFAULT);

		// 控制按钮
		if (theme.showTop()) {
			Composite top = new Composite(shell, SWT.TRANSPARENCY_ALPHA);
			GridData gd_top = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
			gd_top.heightHint = height_top;
			top.setLayoutData(gd_top);
			this.configureTops(top);
		}

		// 快捷菜单
		if (theme.showTools()) {
			List<Action> actions = theme.actions();
			if (actions != null && actions.size() > 0 && theme.logo() != null) {
				Composite tools = new Composite(shell, SWT.TRANSPARENCY_ALPHA);
				GridData gd_tools = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
				gd_tools.heightHint = height_tools;
				tools.setLayoutData(gd_tools);
				this.configureTools(tools);
			}
		}

		// 内容展示
		content = new Composite(shell, SWT.NONE);
		GridData gd_content = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		content.setLayoutData(gd_content);
		this.configureContent(content);

		// 脚部
		if (theme.showFoot()) {
			Composite bottom = new Composite(shell, SWT.TRANSPARENCY_ALPHA);
			GridData gd_bottom = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
			gd_bottom.heightHint = height_bottom;
			bottom.setLayoutData(gd_bottom);
			this.configureBottoms(bottom);
		}

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

	// 监控剪切板
	protected void configureClipboard() {
		clipboard = new Clipboard(shell.getDisplay());
		clipboardThread = new Thread(() -> {
			while (status != Status.exit) {
				try {
					monitorClipboardChange();
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					break;
				}
			}
		});
		clipboardThread.setDaemon(true);
		clipboardThread.start();
	}

	// 获取监听内容
	protected void monitorClipboardChange() {
		Display.getDefault().asyncExec(() -> {
			try {
				String text = null;
				TransferData[] available = clipboard.getAvailableTypes();
				TextTransfer textTransfer = TextTransfer.getInstance();
				for (int i = 0; i < available.length; i++) {
					if (textTransfer.isSupportedType(available[i])) {
						text = String.valueOf(clipboard.getContents(textTransfer));
						break;
					}
				}

				// 无文本内容不用处理
				if (text == null) {
					return;
				}

				// 比较是否变化
				String md5 = MD5s.encode(text);
				if (this.clipboardMD5 == null || !this.clipboardMD5.equals(md5)) {
					this.clipboardMD5 = md5;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	// 控制按钮的配置
	protected void configureTops(Composite top) {
		OrangeTheme theme = (OrangeTheme) this.theme;
		GridLayout gl_top = new GridLayout(6, false);
		this.clearGridLayout(gl_top);
		gl_top.marginWidth = 5;
		gl_top.marginHeight = 5;
		gl_top.horizontalSpacing = 10;
		top.setLayout(gl_top);

		// logo
		if (theme.logo() != null) {
			GridData gd_logo = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
			gd_logo.widthHint = height_top - 10;
			gd_logo.heightHint = height_top - 10;
			ImageButton.builder(top).image(theme.logo().image()).layout(gd_logo).build();
		}

		// left
		CLabel left = new CLabel(top, SWT.SHADOW_NONE);
		left.setText(Settings.me().getServer().getName());
		left.setForeground(ResourceManager.getColor(SWT.COLOR_WHITE));
		left.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		left.addMouseListener(this);
		left.addMouseMoveListener(this);

		// secure
		if (theme.secure() != null) {
			GridData gd_secure = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
			gd_secure.widthHint = height_top - 10;
			gd_secure.heightHint = height_top - 10;
			ImageButton.builder(top).image(theme.secure().image()).layout(gd_secure).click(() -> {
				Commands.nameCommand(Cmd.upgrader).exec();
			}).tip(theme.secure().name()).build();
		}

		// tray
		if (theme.tray() != null) {
			GridData gd_tray = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
			gd_tray.widthHint = height_top - 10;
			gd_tray.heightHint = height_top - 10;
			ImageButton.builder(top).image(theme.tray().image()).layout(gd_tray).click(() -> {
				this.min();
			}).tip(theme.tray().name()).build();
		}

		// resize
		if (theme.resize() != null) {
			GridData gd_resize = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
			gd_resize.widthHint = height_top - 10;
			gd_resize.heightHint = height_top - 10;
			ImageButton.builder(top).image(theme.resize().image()).on(theme.resize().imageOn()).layout(gd_resize)
					.click(() -> {
						this.resize();
					}).tip(theme.resize().name()).build();
		}

		// close
		if (theme.close() != null) {
			GridData gd_close = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
			gd_close.widthHint = height_top - 10;
			gd_close.heightHint = height_top - 10;
			ImageButton.builder(top).image(theme.close().image()).layout(gd_close).click(() -> {
				shell.close();
			}).tip(theme.close().name()).build();
		}
	}

	// 工具栏的配置
	protected void configureTools(Composite tools) {
		GridLayout gl_tools = new GridLayout(2, false);
		this.clearGridLayout(gl_tools);
		gl_tools.marginWidth = margin_width;
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
		int height_button = 48;
		int size = theme.actions().size();

		// 子部快捷菜单 - 布局
		GridLayout gl_childTools = new GridLayout(size, false);
		gl_childTools.horizontalSpacing = 10;
		gl_childTools.marginWidth = 40;
		gl_childTools.marginHeight = 8;
		childTools.setLayout(gl_childTools);

		// 按钮组
		ImageButtonGroup ibg = new ImageButtonGroup();

		// 按钮
		for (int i = 0; i < size; i++) {
			Action action = theme.actions().get(i);
			GridData gd_index = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
			gd_index.widthHint = height_button;
			gd_index.heightHint = height_button;
			ImageButton.builder(childTools).group(ibg).image(action.image()).hover(action.imageOn()).layout(gd_index)
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
		browser = new Browser(content, SWT.WEBKIT);

		// 注册 JS 命令
		JsCommand.bind(browser);

		// 默认展示
		contentStack.topControl = this.logComposite;
	}

	// 底部版权
	protected void configureBottoms(Composite bottom) {
		GridLayout gl_bottom = new GridLayout(1, false);
		this.clearGridLayout(gl_bottom);
		gl_bottom.marginWidth = margin_width;
		bottom.setLayout(gl_bottom);

		// left
		Label copyRight = new Label(bottom, SWT.NONE);
		copyRight.setText(Settings.me().getServer().getVersion());
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
					event.doit = true;
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
			clipboardThread.interrupt();
			signalThread.interrupt();
			clipboard.dispose();
			Commands.nameCommand(Cmd.Dispose).exec();
		});
	}

	/**
	 * shell 大小
	 */
	@Override
	protected Point getShellSize(Rectangle clientArea) {
		OrangeTheme theme = (OrangeTheme) this.theme;
		return theme.getShellSize(clientArea);
	}

	/**
	 * shell 样式
	 */
	@Override
	protected int getShellStyle() {
		OrangeTheme theme = (OrangeTheme) this.theme;
		return theme.getShellStyle();
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
		case upgrade:
			break;
		case upgraded:
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

	// ----------- 拖动 ------------
	private volatile boolean isDraw = false;
	private volatile Point drawPoint = new Point(0, 0);

	@Override
	public void mouseDown(MouseEvent arg0) {
		isDraw = true;
		drawPoint.x = arg0.x;
		drawPoint.y = arg0.y;
		this.shell.setCursor(ResourceManager.getCursor(SWT.CURSOR_HAND));
	}

	/**
	 * 处理 tracker 的事件
	 * 
	 */
	@Override
	public void handleEvent(Event event) {
		switch (event.type) {
		case SWT.MouseDown:
			System.out.println("down");
			break;
		case SWT.MouseMove:
			System.out.println("move");
			break;
		case SWT.MouseUp:
			System.out.println("up");
			break;
		}
	}

	@Override
	public void mouseMove(MouseEvent arg0) {
		if (isDraw) {
			int shell_x = this.shell.getLocation().x + arg0.x - drawPoint.x;
			int shell_y = this.shell.getLocation().y + arg0.y - drawPoint.y;
			this.shell.setLocation(shell_x, shell_y);
			this.shell.redraw();
		}
	}

	@Override
	public void mouseUp(MouseEvent arg0) {
		isDraw = false;
		this.shell.setCursor(ResourceManager.getCursor(SWT.CURSOR_ARROW));
	}

	@Override
	public void mouseDoubleClick(MouseEvent arg0) {

	}

}