package com.swak.manage.widgets;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.swak.manage.command.Command;

/**
 * 自定义按钮
 * 
 * @author lifeng
 */
public class CommandButton extends SelectionAdapter {

	private final Command command;
	private final Button button;

	public CommandButton(Command command, Composite parent, int style) {
		this.button = new Button(parent, style);
		this.command = command;
		this.init();
	}

	private void init() {
		this.button.addSelectionListener(this);
		this.button.setText(command.name());
	}

	/**
	 * 执行事件
	 */
	@Override
	public void widgetSelected(SelectionEvent arg0) {
		command.exec();
	}

	public Command getCommand() {
		return command;
	}

	public Button getButton() {
		return button;
	}
}