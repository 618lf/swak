package com.tmt.manage.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.tmt.manage.command.Command;

/**
 * 待提示
 * 
 * @author lifeng
 */
public class ConfirmCommandButton extends CommandButton {

	private final String message;
	private Shell shell;
	
	public ConfirmCommandButton(Command command, Composite parent, int style, String message) {
		super(command, parent, style);
		this.message = message;
		this.shell = parent.getShell();
	}

	@Override
	public void widgetSelected(SelectionEvent arg0) {
		int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
		MessageBox messageBox = new MessageBox(shell, style);
		messageBox.setText("提示");
		messageBox.setMessage(message);
		if (messageBox.open() == SWT.YES) {
			super.widgetSelected(arg0);
		}
	}
}
