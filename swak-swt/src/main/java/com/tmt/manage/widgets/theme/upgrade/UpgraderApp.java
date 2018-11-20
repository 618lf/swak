package com.tmt.manage.widgets.theme.upgrade;

import java.util.List;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.tmt.manage.command.Commands;
import com.tmt.manage.command.Commands.Cmd;
import com.tmt.manage.command.Commands.Signal;
import com.tmt.manage.command.Receiver;
import com.tmt.manage.config.Settings;
import com.tmt.manage.widgets.BaseApp;
import com.tmt.manage.widgets.ImageButton;
import com.tmt.manage.widgets.ImageButtonGroup;
import com.tmt.manage.widgets.ResourceManager;
import com.tmt.manage.widgets.theme.Theme.Action;
import com.tmt.manage.widgets.theme.upgrade.UpgraderTheme.Backup;
import com.tmt.manage.widgets.theme.upgrade.UpgraderTheme.Patch;

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

	// 先阶段只支持两个区域的显示
	private Composite oneComposite;
	private Composite twoComposite;

	// buttons
	private ImageButtonGroup group;

	// 清除默认样式
	private void clearGridLayout(GridLayout gridLayout) {
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
	}

	@Override
	protected void createContents() {
		shell.setText(Settings.me().getServerName() + "- 安全模式");
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
		left.setText(Settings.me().getServerName() + "- 安全模式");
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
					.image(doneAction.image()).blur(doneAction.color()).hover(doneAction.imageOn()).click(() -> {
						contentStack.topControl = this.oneComposite;
						content.layout();
						doneAction.click();
					}).build();

			// 待经安装的补丁
			Action undoneAction = actions.get(1);
			ImageButton.builder(left).text(undoneAction.name()).bounds(new Rectangle(139, 32, 95, 38)).group(group)
					.image(undoneAction.image()).blur(undoneAction.color()).hover(undoneAction.imageOn()).click(() -> {
						contentStack.topControl = this.twoComposite;
						content.layout();
						undoneAction.click();
					}).build();

//			// right
//			Composite right = new Composite(tools, SWT.TRANSPARENCY_ALPHA);
//			GridData gd_right = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
//			gd_right.widthHint = 110;
//			right.setLayoutData(gd_right);
//
//			// 选择补丁包
//			Action selectAction = actions.get(2);
//			ImageButton.builder(right).text(selectAction.name()).bounds(new Rectangle(5, 30, 95, 35))
//					.image(selectAction.image()).hover(selectAction.imageOn()).click(selectAction.click()).build();
			
			// 默认第一个点亮
			this.group.first();
		}
	}

	// 内容区域的配置
	protected void configureContent(Composite content) {
		UpgraderTheme theme = (UpgraderTheme) this.theme;
		contentStack = new StackLayout();
		content.setLayout(contentStack);

		//############ ** 补丁区域 ** ##############
		
		oneComposite = new Composite(content, SWT.NONE);
		oneComposite.setBackground(ResourceManager.getColor(SWT.COLOR_WHITE));
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
		CLabel done_top_right = new CLabel(done_top, SWT.SHADOW_NONE);
		done_top_right.setText("添加补丁");
		GridData gd_done_top_right = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		done_top_right.setLayoutData(gd_done_top_right);

		// 表格
		TableViewer done_tableViewer = new TableViewer(oneComposite,
				SWT.MULTI | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.FULL_SELECTION);
		this.configureDoneTable(done_tableViewer, new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		done_tableViewer.setInput(theme.dones());
		
		//############ ** 备份区域 ** ##############
		twoComposite = new Composite(content, SWT.NONE);
		twoComposite.setBackground(ResourceManager.getColor(SWT.COLOR_WHITE));
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
		backup_top_left.setText("请及时备份系统，并将数据保存到安全的地方");
		GridData gd_backup_top_left = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		backup_top_left.setLayoutData(gd_backup_top_left);
		
		// 操作
		CLabel backup_top_right = new CLabel(backup_top, SWT.SHADOW_NONE);
		backup_top_right.setText("备份系统");
		GridData gd_backup_top_right = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		backup_top_right.setLayoutData(gd_backup_top_right);

		// 表格
		TableViewer undone_tableViewer = new TableViewer(twoComposite,
				SWT.MULTI | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.FULL_SELECTION);
		this.configureUnDoneTable(undone_tableViewer, new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		undone_tableViewer.setInput(theme.backups());

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
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(done_table, SWT.BORDER).setText("安装时间");
		tLayout.addColumnData(new ColumnWeightData(30));
		new TableColumn(done_table, SWT.BORDER).setText("补丁名称");
		tLayout.addColumnData(new ColumnWeightData(50));
		new TableColumn(done_table, SWT.BORDER).setText("补丁描述");
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
					return patch.getCreateDate();
				}
				if (arg1 == 1) {
					return patch.getName();
				}
				if (arg1 == 2) {
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
		tLayout.addColumnData(new ColumnWeightData(20));
		new TableColumn(done_table, SWT.BORDER).setText("备份时间");
		tLayout.addColumnData(new ColumnWeightData(30));
		new TableColumn(done_table, SWT.BORDER).setText("备份文件名称");
		tLayout.addColumnData(new ColumnWeightData(50));
		new TableColumn(done_table, SWT.BORDER).setText("操作");
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
				Backup patch = (Backup) arg0;
				if (arg1 == 0) {
					return patch.getCreateDate();
				}
				if (arg1 == 1) {
					return patch.getName();
				}
				if (arg1 == 2) {
					return "操作";
				}
				return "";
			}
		});
	}

	@Override
	protected void configureShell() {
		shell.addShellListener(new ShellListener() {
			@Override
			public void shellActivated(ShellEvent event) {
				Commands.nameCommand(Cmd.init).exec();
			}

			@Override
			public void shellClosed(ShellEvent event) {

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

	}
}