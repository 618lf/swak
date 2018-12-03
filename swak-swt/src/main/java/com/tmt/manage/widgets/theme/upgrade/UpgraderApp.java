package com.tmt.manage.widgets.theme.upgrade;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.tmt.manage.command.Commands;
import com.tmt.manage.command.Commands.Cmd;
import com.tmt.manage.command.Commands.Sign;
import com.tmt.manage.command.Commands.Signal;
import com.tmt.manage.command.Receiver;
import com.tmt.manage.command.impl.BackupCommand;
import com.tmt.manage.command.impl.UpgradeCommand;
import com.tmt.manage.config.Settings;
import com.tmt.manage.widgets.BaseApp;
import com.tmt.manage.widgets.ImageButton;
import com.tmt.manage.widgets.ImageButtonGroup;
import com.tmt.manage.widgets.Message;
import com.tmt.manage.widgets.Progress;
import com.tmt.manage.widgets.ResourceManager;
import com.tmt.manage.widgets.theme.Theme.Action;

/**
 * 升级模式
 * 
 * @author lifeng
 */
public class UpgraderApp extends BaseApp implements Receiver {

	private StackLayout contentStack;
	private Composite content;
	private int height_top = 42;
	private int height_tools = 70;
	private Composite oneComposite;
	private Composite twoComposite;
	private Composite threeComposite;
	private ImageButtonGroup group;
	private TableViewer packs;
	private TableViewer backs;
	private Thread signalThread;
	private Progress progress;
	private volatile Status status = Status.idle;

	// 清除默认样式
	private void clearGridLayout(GridLayout gridLayout) {
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
	}

	@Override
	protected void createContents() {
		shell.setText(Settings.me().getServer().getName() + "- 安全模式");
		UpgraderTheme theme = (UpgraderTheme) this.theme;
		if (theme.logo() != null) {
			shell.setImage(theme.logo().image());
		}
		if (theme.background() != null) {
			shell.setBackgroundImage(theme.background().image());
		}
		GridLayout gl_shell = new GridLayout(1, false);
		this.clearGridLayout(gl_shell);
		shell.setLayout(gl_shell);
		shell.setBackgroundMode(SWT.INHERIT_DEFAULT);

		// top
		Composite top = new Composite(shell, SWT.TRANSPARENCY_ALPHA);
		GridData gd_top = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_top.heightHint = height_top;
		top.setLayoutData(gd_top);
		this.configureTops(top);

		// tools
		Composite tools = new Composite(shell, SWT.TRANSPARENCY_ALPHA);
		GridData gd_tools = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_tools.heightHint = height_tools;
		tools.setLayoutData(gd_tools);
		this.configureTools(tools);

		// 内容展示
		content = new Composite(shell, SWT.TRANSPARENCY_ALPHA);
		GridData gd_content = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		content.setLayoutData(gd_content);
		this.configureContent(content);

		// 进度条
		ProgressBar progressBar = new ProgressBar(shell, SWT.HORIZONTAL | SWT.SMOOTH);
		GridData gd_progressBar = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		progressBar.setLayoutData(gd_progressBar);
		progressBar.setEnabled(false);
		progress = new Progress(progressBar);
		progress.hide();

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
		UpgraderTheme theme = (UpgraderTheme) this.theme;
		GridLayout gl_top = new GridLayout(3, false);
		this.clearGridLayout(gl_top);
		gl_top.marginWidth = 5;
		gl_top.marginHeight = 5;
		top.setLayout(gl_top);

		// logo
		if (theme.logo() != null) {
			GridData gd_logo = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
			gd_logo.widthHint = 32;
			gd_logo.heightHint = 32;
			ImageButton.builder(top).image(theme.logo().image()).layout(gd_logo).build();
		}

		// left
		CLabel left = new CLabel(top, SWT.SHADOW_NONE);
		left.setText(Settings.me().getServer().getName() + "- 安全模式");
		left.setForeground(ResourceManager.getColor(SWT.COLOR_WHITE));
		GridData gd_left = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		left.setLayoutData(gd_left);

		// close
		if (theme.close() != null) {
			GridData gd_close = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
			gd_close.widthHint = 32;
			gd_close.heightHint = 32;
			ImageButton.builder(top).image(theme.close().image()).layout(gd_close).click(() -> {
				shell.close();
			}).tip("关闭").build();
		}
	}

	// 控制按钮的配置
	protected void configureTools(Composite tools) {
		UpgraderTheme theme = (UpgraderTheme) this.theme;
		GridLayout gl_tools = new GridLayout(2, false);
		this.clearGridLayout(gl_tools);
		tools.setLayout(gl_tools);

		// 三个 actions
		List<Action> actions = theme.actions();
		if (actions != null && actions.size() == 3) {

			// left
			Composite left = new Composite(tools, SWT.TRANSPARENCY_ALPHA);
			GridData gd_left = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			left.setLayoutData(gd_left);
			left.setLayout(null);

			// 按钮组
			this.group = new ImageButtonGroup();

			// 已经安装的补丁
			Action doneAction = actions.get(0);
			ImageButton.builder(left).text(doneAction.name()).bounds(new Rectangle(39, 32, 95, 38)).group(group)
					.image(doneAction.image()).blur(doneAction.color()).on(doneAction.colorOn())
					.hover(doneAction.imageOn()).click(() -> {
						contentStack.topControl = this.oneComposite;
						content.layout();
						doneAction.click();
					}).build();

			// 待经安装的补丁
			Action backupAction = actions.get(1);
			ImageButton.builder(left).text(backupAction.name()).bounds(new Rectangle(139, 32, 95, 38)).group(group)
					.image(backupAction.image()).blur(backupAction.color()).on(backupAction.colorOn())
					.hover(backupAction.imageOn()).click(() -> {
						contentStack.topControl = this.twoComposite;
						content.layout();
						backupAction.click();
					}).build();
			
			// 待经安装的补丁
			Action logAction = actions.get(2);
			ImageButton.builder(left).text(logAction.name()).bounds(new Rectangle(239, 32, 95, 38)).group(group)
					.image(logAction.image()).blur(logAction.color()).on(logAction.colorOn())
					.hover(logAction.imageOn()).click(() -> {
						contentStack.topControl = this.threeComposite;
						content.layout();
						logAction.click();
					}).build();

			// 默认第一个点亮
			this.group.first();
		}
		
		// right
		Composite right = new Composite(tools, SWT.TRANSPARENCY_ALPHA);
		GridData gd_right = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_right.widthHint = 110;
		right.setLayoutData(gd_right);

		// 启动系统
		Button selectActionBtn = new Button(right, SWT.NONE);
		selectActionBtn.setText("启动系统");
		selectActionBtn.setBounds(new Rectangle(5, 32, 100, 35));
		selectActionBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Commands.nameCommand(Cmd.starter).exec();
			}
		});
	}

	// 内容区域的配置
	protected void configureContent(Composite content) {
		UpgraderTheme theme = (UpgraderTheme) this.theme;
		contentStack = new StackLayout();
		content.setLayout(contentStack);

		// ############ ** 补丁区域 ** ##############

		oneComposite = new Composite(content, SWT.NONE);
		oneComposite.setBackground(theme.actions().get(0).colorOn());
		GridLayout gl_oneComposite = new GridLayout(1, false);
		this.clearGridLayout(gl_oneComposite);
		oneComposite.setLayout(gl_oneComposite);

		// 提示和操作
		Composite done_top = new Composite(oneComposite, SWT.NONE);
		GridData gd_done_top = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		done_top.setLayoutData(gd_done_top);
		GridLayout gl_done_top = new GridLayout(2, false);
		this.clearGridLayout(gl_done_top);
		gl_done_top.marginWidth = 15;
		gl_done_top.marginHeight = 20;
		done_top.setLayout(gl_done_top);

		// 提示
		CLabel done_top_left = new CLabel(done_top, SWT.SHADOW_NONE);
		done_top_left.setText("请选择需要添加的补丁");
		GridData gd_done_top_left = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		done_top_left.setLayoutData(gd_done_top_left);

		// 操作
		Button done_top_right = new Button(done_top, SWT.NONE);
		done_top_right.setText("安装补丁");
		GridData gd_done_top_right = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		done_top_right.setLayoutData(gd_done_top_right);
		done_top_right.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileselect = new FileDialog(shell, SWT.MULTI);
				fileselect.setFilterExtensions(new String[] { "*.zip", "*.ZIP*" });
				fileselect.open();
				String path = fileselect.getFilterPath();
				if (path != null) {
					String[] fileNames = fileselect.getFileNames();
					List<File> files = new ArrayList<>();
					for (String name : fileNames) {
						files.add(new File(path, name));
					}
					Commands.sendSignal(Signal.newSignal(Sign.upgrade));
					theme.actions().get(0).accept().accept(files);
					refreshPacks();
					new UpgradeCommand().exec();
				}
			}
		});

		// 表格
		packs = new TableViewer(oneComposite,
				SWT.MULTI | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.FULL_SELECTION);
		this.configureDoneTable(packs, new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		packs.setInput(theme.patchs());

		// ############ ** 备份区域 ** ##############
		
		twoComposite = new Composite(content, SWT.NONE);
		twoComposite.setBackground(theme.actions().get(1).colorOn());
		GridLayout gl_twoComposite = new GridLayout(1, false);
		this.clearGridLayout(gl_twoComposite);
		twoComposite.setLayout(gl_twoComposite);

		// 提示和操作
		Composite backup_top = new Composite(twoComposite, SWT.NONE);
		GridData gd_backup_top = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		backup_top.setLayoutData(gd_backup_top);
		GridLayout gl_backup_top = new GridLayout(2, false);
		this.clearGridLayout(gl_backup_top);
		gl_backup_top.marginWidth = 15;
		gl_backup_top.marginHeight = 20;
		backup_top.setLayout(gl_backup_top);

		// 提示
		CLabel backup_top_left = new CLabel(backup_top, SWT.SHADOW_NONE);
		backup_top_left.setText("请及时备份数据，并将数据保存到安全的地方");
		GridData gd_backup_top_left = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		backup_top_left.setLayoutData(gd_backup_top_left);

		// 操作
		Button backup_top_right = new Button(backup_top, SWT.NONE);
		backup_top_right.setText("备份数据");
		GridData gd_backup_top_right = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		backup_top_right.setLayoutData(gd_backup_top_right);
		backup_top_right.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Commands.sendSignal(Signal.newSignal(Sign.upgrade));
				new BackupCommand().exec();
			}
		});

		// 表格
		backs = new TableViewer(twoComposite,
				SWT.MULTI | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.FULL_SELECTION);
		this.configureUnDoneTable(backs, new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		backs.setInput(theme.backups());
		
		
		// ############ ** 日志区域 ** ##############
		threeComposite = new Composite(content, SWT.NONE);
		threeComposite.setBackground(theme.actions().get(2).colorOn());
		GridLayout gl_threeComposite = new GridLayout(1, false);
		this.clearGridLayout(gl_threeComposite);
		threeComposite.setLayout(gl_threeComposite);
		
		// 提示
		// 提示和操作
		Composite log_top = new Composite(threeComposite, SWT.NONE);
		GridData gd_log_top = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		log_top.setLayoutData(gd_log_top);
		GridLayout gl_log_top = new GridLayout(2, false);
		this.clearGridLayout(gl_log_top);
		gl_log_top.marginWidth = 15;
		gl_log_top.marginHeight = 20;
		log_top.setLayout(gl_log_top);

		// 提示
		CLabel log_top_left = new CLabel(log_top, SWT.SHADOW_NONE);
		log_top_left.setText("显示最近两个月的运行日志");
		GridData gd_log_top_left = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		log_top_left.setLayoutData(gd_log_top_left);
		
		// 操作
		Button log_top_right = new Button(log_top, SWT.NONE);
		log_top_right.setText("导出日志");
		GridData gd_log_top_right = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		log_top_right.setLayoutData(gd_log_top_right);
		log_top_right.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog folder = new DirectoryDialog(shell);
				folder.setText("选择日志文件存储目录");
				folder.setFilterPath("SystemDrive");
				folder.setMessage("选择日志文件存储目录");
				String dir = folder.open();
				if (dir != null) {
					theme.actions().get(2).accept().accept(new File(dir));
					Message.success("导出成功！");
				}
			}
		});
		
		// 表格
		TableViewer logs = new TableViewer(threeComposite,
				SWT.MULTI | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.FULL_SELECTION);
		this.configureLogsTable(logs, new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		logs.setInput(theme.logs());

		// 默认展示
		contentStack.topControl = this.oneComposite;
	}

	// 配置表格
	private void configureDoneTable(TableViewer done_tableViewer, GridData gridData) {
		Table done_table = done_tableViewer.getTable();
		done_table.setLayoutData(gridData);
		done_table.setHeaderVisible(true);
		done_table.setLinesVisible(true);
		TableLayout tLayout = new TableLayout();
		done_table.setLayout(tLayout);
		tLayout.addColumnData(new ColumnWeightData(70));
		new TableColumn(done_table, SWT.BORDER).setText("补丁名称");
		tLayout.addColumnData(new ColumnWeightData(30));
		new TableColumn(done_table, SWT.BORDER).setText("备注");
		done_tableViewer.setContentProvider(new IStructuredContentProvider() {
			@SuppressWarnings("rawtypes")
			@Override
			public Object[] getElements(Object arg0) {
				if (arg0 instanceof List) {
					return ((List) arg0).toArray();// 将setInput传过来的List变成一个数组输出
				}
				return new Object[0];
			}
		});
		done_tableViewer.setLabelProvider(new ITableLabelProvider() {

			@Override
			public void addListener(ILabelProviderListener arg0) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public boolean isLabelProperty(Object arg0, String arg1) {
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener arg0) {

			}

			@Override
			public Image getColumnImage(Object arg0, int arg1) {
				return null;
			}

			@Override
			public String getColumnText(Object arg0, int arg1) {
				Patch patch = (Patch) arg0;
				if (arg1 == 0) {
					return patch.getName();
				}
				if (arg1 == 1) {
					return patch.getRemarks();
				}
				return "";
			}
		});
	}

	// 配置表格
	private void configureUnDoneTable(TableViewer done_tableViewer, GridData gridData) {
		Table done_table = done_tableViewer.getTable();
		done_table.setLayoutData(gridData);
		done_table.setHeaderVisible(true);
		done_table.setLinesVisible(true);
		TableLayout tLayout = new TableLayout();
		done_table.setLayout(tLayout);
		tLayout.addColumnData(new ColumnWeightData(70));
		new TableColumn(done_table, SWT.BORDER).setText("备份文件名称");
		tLayout.addColumnData(new ColumnWeightData(30));
		new TableColumn(done_table, SWT.BORDER).setText("操作");
		done_tableViewer.addDoubleClickListener((event) -> {
			StructuredSelection selection = (StructuredSelection) event.getSelection();
			if (!selection.isEmpty()) {
				DirectoryDialog folder = new DirectoryDialog(shell);
				folder.setText("选择备份文件存储目录");
				folder.setFilterPath("SystemDrive");
				folder.setMessage("选择备份文件存储目录");
				String dir = folder.open();
				if (dir != null) {
					Backup file = (Backup) (selection.getFirstElement());
					file.setSave(new File(dir));
					theme.actions().get(1).accept().accept(file);
					Message.success("下载成功！");
				}
			}
		});
		done_tableViewer.setContentProvider(new IStructuredContentProvider() {
			@SuppressWarnings("rawtypes")
			@Override
			public Object[] getElements(Object arg0) {
				if (arg0 instanceof List) {
					return ((List) arg0).toArray();
				}
				return new Object[0];
			}
		});
		done_tableViewer.setLabelProvider(new ITableLabelProvider() {

			@Override
			public void addListener(ILabelProviderListener arg0) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public boolean isLabelProperty(Object arg0, String arg1) {
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener arg0) {

			}

			@Override
			public Image getColumnImage(Object arg0, int arg1) {
				return null;
			}

			@Override
			public String getColumnText(Object arg0, int arg1) {
				Backup patch = (Backup) arg0;
				if (arg1 == 0) {
					return patch.getName();
				}
				if (arg1 == 1) {
					return "双击下载";
				}
				return "";
			}
		});
	}
	
	// 配置表格
	private void configureLogsTable(TableViewer done_tableViewer, GridData gridData) {
		Table done_table = done_tableViewer.getTable();
		done_table.setLayoutData(gridData);
		done_table.setHeaderVisible(true);
		done_table.setLinesVisible(true);
		TableLayout tLayout = new TableLayout();
		done_table.setLayout(tLayout);
		tLayout.addColumnData(new ColumnWeightData(100));
		new TableColumn(done_table, SWT.BORDER).setText("运行日志文件");
		done_tableViewer.setContentProvider(new IStructuredContentProvider() {
			@SuppressWarnings("rawtypes")
			@Override
			public Object[] getElements(Object arg0) {
				if (arg0 instanceof List) {
					return ((List) arg0).toArray();
				}
				return new Object[0];
			}
		});
		done_tableViewer.setLabelProvider(new ITableLabelProvider() {

			@Override
			public void addListener(ILabelProviderListener arg0) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public boolean isLabelProperty(Object arg0, String arg1) {
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener arg0) {

			}

			@Override
			public Image getColumnImage(Object arg0, int arg1) {
				return null;
			}

			@Override
			public String getColumnText(Object arg0, int arg1) {
				Log patch = (Log) arg0;
				if (arg1 == 0) {
					return patch.getName();
				}
				return "";
			}
		});
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
				if (status == Status.doing) {
					event.doit = false;
					int style = SWT.APPLICATION_MODAL | SWT.YES;
					MessageBox messageBox = new MessageBox(shell, style);
					messageBox.setText("提示");
					messageBox.setMessage("系统正在安装补丁或备份数据，请不要关闭！");
					messageBox.open();
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

	@Override
	protected int getShellStyle() {
		return SWT.NONE;
	}

	@Override
	protected Point getShellSize(Rectangle clientArea) {
		int height = clientArea.height;
		int shellHeight = (int) (height * 0.75);
		int shellWight = shellHeight / 10 * 8;
		return new Point(shellWight, shellHeight);
	}

	@Override
	public void handleSignal(Signal signal) {
		Display.getDefault().asyncExec(() -> {
			this.doSignal(signal);
		});
	}

	/**
	 * 刷新表格
	 */
	protected void refreshPacks() {
		UpgraderTheme theme = (UpgraderTheme) this.theme;
		packs.setInput(theme.patchs());
		packs.refresh(true, true);
	}

	/**
	 * 刷新表格
	 */
	protected void refreshBacks() {
		UpgraderTheme theme = (UpgraderTheme) this.theme;
		backs.setInput(theme.backups());
		backs.refresh(true, true);
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
			break;
		case server_started:
			break;
		case server_stoping:
			break;
		case server_stoped:
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
			break;
		case log:
			break;
		case upgrade:
			this.status = Status.doing;
			progress.start();
			break;
		case upgraded:
			this.status = Status.idle;
			progress.stop();
			refreshPacks();
			refreshBacks();
			break;
		}
	}

	/**
	 * 系统状态
	 * 
	 * @author lifeng
	 */
	public static enum Status {
		doing, idle, exit
	}
}