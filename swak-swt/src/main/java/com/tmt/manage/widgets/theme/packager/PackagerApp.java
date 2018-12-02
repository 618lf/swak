package com.tmt.manage.widgets.theme.packager;

import java.io.File;
import java.nio.file.Files;

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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;

import com.tmt.manage.config.Settings;
import com.tmt.manage.operation.OpsFile;
import com.tmt.manage.widgets.BaseApp;
import com.tmt.manage.widgets.CommandButton;
import com.tmt.manage.widgets.theme.upgrade.Patch;

/**
 * 简单的打包工具
 * 
 * @author lifeng
 */
public class PackagerApp extends BaseApp {

	StyledText logText;

	@Override
	protected void createContents() {
		PackagerTheme theme = (PackagerTheme) this.theme;
		shell.setLayout(new FillLayout());
		shell.setText(Settings.me().getServer().getName() + "-打包工具");
		if (theme.logo() != null) {
			shell.setImage(theme.logo().image());
		}
		Composite container = new Composite(shell, SWT.NONE);
		container.setLayout(new GridLayout(1, false));

		logText = new StyledText(container, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
		logText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		logText.setDoubleClickEnabled(false);
		logText.setEditable(false);

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite.setLayout(new GridLayout(3, false));

		Text text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		text.setEnabled(false);

		Button selectButton = new CommandButton(() -> {
			DirectoryDialog folder = new DirectoryDialog(shell);
			folder.setText("选择增量包生成目录");
			folder.setFilterPath("SystemDrive");
			folder.setMessage("请选选择增量包生成目录");
			String dir = folder.open();
			if (dir != null) {
				text.setText(dir);
				createStructure(dir);
			}
		}, composite, SWT.BORDER).getButton();
		selectButton.setText("选择目录");
		selectButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		Button startButton = new CommandButton(() -> {
			String dir = text.getText();
			doPackage(dir);
		}, composite, SWT.BORDER).getButton();
		startButton.setText("执行打包");
		startButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
	}

	// 生成目录结构
	protected void createStructure(String dir) {
		logText.setText("");
		logText.append("选择目录：" + dir + "\n");
		logText.append("创建目录PTMS，初始化增量包结构" + "\n");
		logText.append("**********目录结构*********" + "\n");
		logText.append("***PTMS" + "\n");
		logText.append("***└  SQLS" + "\n");
		logText.append("***└  LIBS" + "\n");
		logText.append("***└  STATICS" + "\n");
		logText.append("***└  CONFIGS" + "\n");
		logText.append("*************************" + "\n");
		logText.append("请将相关文件放入相应的目录，填入版本号" + "\n");

		// 主目录
		File makeDir = new File(dir, "PTMS");
		makeDir.mkdir();
		new File(makeDir, OpsFile.SQL).mkdir();
		new File(makeDir, OpsFile.LIB).mkdir();
		new File(makeDir, OpsFile.STATIC).mkdir();
		new File(makeDir, OpsFile.CONFIG).mkdir();
		createVersion(makeDir);
	}

	// 生成版本文件
	protected void createVersion(File makeDir) {
		File version = new File(makeDir, OpsFile.VER);
		if (version.exists()) {
			return;
		}
		try {
			version.createNewFile();
			StringBuilder info = new StringBuilder();
			info.append("min:").append("\r\n");
			info.append("cur:").append("\r\n");
			Files.write(version.toPath(), info.toString().getBytes("utf-8"));
		} catch (Exception e) {
		}
	}

	// 打增量包
	protected void doPackage(String dir) {
		File makeDir = new File(dir, "PTMS");
		if (!makeDir.exists()) {
			return;
		}
		try {
			File patch = OpsFile.ops(Patch.newPatch(makeDir)).compress();
			logText.append("生成补丁文件：" + patch.getName() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void configureShell() {
		shell.addShellListener(new ShellListener() {
			@Override
			public void shellActivated(ShellEvent event) {
			}

			@Override
			public void shellClosed(ShellEvent event) {
			}

			@Override
			public void shellDeactivated(ShellEvent event) {
			}

			@Override
			public void shellDeiconified(ShellEvent event) {
			}

			@Override
			public void shellIconified(ShellEvent event) {
			}
		});
		shell.addDisposeListener(e -> {
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
}