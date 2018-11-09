package com.tmt.manage.widgets;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.tmt.manage.command.Commands;
import com.tmt.manage.command.Commands.Cmd;
import com.tmt.manage.config.Settings;

/**
 * 关闭的时候提示错误，估计是jar的问题，需要研究下jar
 * 
 * @author lifeng
 */
public class MainFrame extends ApplicationWindow {

	private StyledText styledText;

	/**
	 * Create the application window.
	 */
	public MainFrame() {
		super(null);
		setShellStyle(SWT.CLOSE);
		createActions();
		// addToolBar(SWT.FLAT | SWT.WRAP);
		// addMenuBar();
		// addStatusLine();
	}

	/**
	 * Create contents of the application window.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));

		styledText = new StyledText(container, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		styledText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		styledText.setDoubleClickEnabled(false);
		styledText.setEditable(false);

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite.setLayout(new GridLayout(2, false));

		Button btnNewButton = new CommandButton(Commands.nameCommand(Cmd.start), composite, SWT.NONE).getButton();
		btnNewButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

		Button btnNewButton_1 = new CommandButton(Commands.nameCommand(Cmd.stop), composite, SWT.NONE).getButton();
		btnNewButton_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Create the status line manager.
	 * 
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	/**
	 * Configure the shell.
	 * 
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Settings.getSettings().getServerName());
		// 这个地方可以设置监听
		newShell.addShellListener(new ShellListener() {

			// 窗口被激活时
			@Override
			public void shellActivated(ShellEvent arg0) {
				Commands.nameCommand(Cmd.init).exec();
			}

			// 窗口关闭时
			@Override
			public void shellClosed(ShellEvent arg0) {
				Commands.nameCommand(Cmd.stop).exec();
			}

			// 窗口变为非激活状态时
			@Override
			public void shellDeactivated(ShellEvent arg0) {
				Commands.nameCommand(Cmd.Deactivated).exec();
			}

			// 当窗口不是最小化时
			@Override
			public void shellDeiconified(ShellEvent arg0) {
				Commands.nameCommand(Cmd.Deiconified).exec();
			}

			// 当窗口最小化时
			@Override
			public void shellIconified(ShellEvent arg0) {
				Commands.nameCommand(Cmd.Iconified).exec();
			}
		});
		// 释放资源
		newShell.addDisposeListener(e ->{
			Commands.nameCommand(Cmd.Dispose).exec();
		});
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(425, 525);
	}

	/**
	 * 显示日志
	 * 
	 * @param text
	 */
	public void log(String text) {
		styledText.append(text);
		styledText.setSelection(styledText.getCharCount());
		styledText.setTopIndex(Integer.MAX_VALUE);
	}
}
