package com.swak.fx;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.controlsfx.dialog.CommandLinksDialog;
import org.controlsfx.dialog.CommandLinksDialog.CommandLinksButtonType;
import org.controlsfx.dialog.ProgressDialog;
import org.controlsfx.dialog.Wizard.LinearFlow;
import org.controlsfx.dialog.WizardPane;

import com.google.common.collect.Lists;

import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * 系统支持的 dialog
 * 
 * @author lifeng
 */
public class Dialogs {

	/**
	 * 错误信息
	 * 
	 * @param title
	 * @param message
	 */
	public static void error(String title, String message) {
		alert(Alert.AlertType.ERROR, title, message);
	}

	/**
	 * 提示信息
	 * 
	 * @param title
	 * @param message
	 */
	public static void info(String title, String message) {
		alert(Alert.AlertType.INFORMATION, title, message);
	}

	/**
	 * 警告
	 * 
	 * @param title
	 * @param message
	 */
	public static void warn(String title, String message) {
		alert(Alert.AlertType.WARNING, title, message);
	}

	/**
	 * 基础
	 * 
	 * @param alertType
	 * @param title
	 * @param message
	 */
	public static void alert(Alert.AlertType alertType, String title, String message) {
		Display.runUI(() -> {
			try {
				Alert alert = new Alert(alertType, message, ButtonType.OK);
				alert.setTitle(title);
				alert.setHeaderText(null);
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().addAll(Display.getStage().getIcons());
				alert.showAndWait();
			} catch (Exception e) {
			}
		});
	}

	/**
	 * 确认
	 * 
	 * @param alertType
	 * @param title
	 * @param message
	 * @param buttonTypes
	 * @return
	 */
	public static ButtonType confirm(String title, String message, ButtonType... buttonTypes) {
		try {
			Alert alert = new Alert(Alert.AlertType.WARNING, message, buttonTypes);
			alert.setTitle(title);
			alert.setHeaderText(null);
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().addAll(Display.getStage().getIcons());
			Optional<ButtonType> result = alert.showAndWait();
			return result.orElse(ButtonType.CANCEL);
		} catch (Exception e) {
			return ButtonType.CANCEL;
		}
	}

	/**
	 * 命令行的对话框
	 * 
	 * @param commands
	 */
	public static ButtonType commands(String title, CommandLinksButtonType... commands) {
		try {
			Dialog<ButtonType> dialog = new CommandLinksDialog(commands);
			dialog.setTitle(title);
			Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
			stage.getIcons().addAll(Display.getStage().getIcons());
			Optional<ButtonType> result = dialog.showAndWait();
			return result.orElse(ButtonType.CANCEL);
		} catch (Exception e) {
			return ButtonType.CANCEL;
		}
	}

	public static <T> void progress(String title, Worker<T> worker) {
		try {
			Dialog<Void> dialog = new ProgressDialog(worker);
			dialog.setTitle(title);
			Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
			stage.getIcons().addAll(Display.getStage().getIcons());
			dialog.showAndWait();
		} catch (Exception e) {
		}
	}

	public static ButtonType wizard(String title, WizardPane... panes) {
		try {
			org.controlsfx.dialog.Wizard wizard = new org.controlsfx.dialog.Wizard(Display.getStage());
			wizard.setTitle(title);
			wizard.setFlow(new LinearFlow(panes));
			Optional<ButtonType> result = wizard.showAndWait();
			return result.orElse(ButtonType.CANCEL);
		} catch (Exception e) {
			return ButtonType.CANCEL;
		}
	}

	/**
	 * 引导创建 Command Dialog
	 * 
	 * @author lifeng
	 */
	public static class Command {

		private List<CommandLinksButtonType> commands = Lists.newArrayList();

		public Command command(String text, String longText) {
			commands.add(new CommandLinksButtonType(text, longText, false));
			return this;
		}

		public Command command(String text, String longText, boolean isDefault) {
			commands.add(new CommandLinksButtonType(text, longText, isDefault));
			return this;
		}

		public ButtonType build(String title) {
			CommandLinksButtonType[] _commands = new CommandLinksButtonType[commands.size()];
			return Dialogs.commands(title, commands.toArray(_commands));
		}

		public static Command create() {
			return new Command();
		}
	}

	/**
	 * 引导创建 Wizard Dialog
	 * 
	 * @author lifeng
	 */
	public static class Wizard {

		private List<WizardPane> panes = Lists.newArrayList();

		public Wizard next(WizardPane pane) {
			panes.add(pane);
			return this;
		}

		public ButtonType build(String title) {
			WizardPane[] _commands = new WizardPane[panes.size()];
			return Dialogs.wizard(title, panes.toArray(_commands));
		}

		public static Wizard create() {
			return new Wizard();
		}
	}

	/**
	 * 构建一个对话框
	 * 
	 * @author lifeng
	 */
	public static class Builder {

		private Parent body;
		private String fxml;
		private String style;
		private String title;
		private List<ButtonType> buttons = Lists.newArrayList();

		public Builder body(Parent dialogBody) {
			this.body = dialogBody;
			return this;
		}

		public Builder body(String fxml) {
			this.fxml = fxml;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder style(String style) {
			this.style = style;
			return this;
		}

		public Builder buttons(ButtonType... buttons) {
			this.buttons = Arrays.asList(buttons);
			return this;
		}

		public Dialog<ButtonType> build() {
			Dialog<ButtonType> dialog = new Dialog<ButtonType>();
			applyTo(dialog);
			return dialog;
		}

		private void applyTo(Dialog<ButtonType> dialog) {
			try {
				if (title != null) {
					dialog.setTitle(title);
				}

				dialog.initOwner(Display.getStage());
				dialog.initModality(Modality.APPLICATION_MODAL);
				dialog.initStyle(StageStyle.TRANSPARENT);

				if (body != null) {
					dialog.getDialogPane().setContent(body);
				} else if (fxml != null) {
					Parent _dialogBody = FXMLLoader.load(Display.load(fxml));
					dialog.getDialogPane().setContent(_dialogBody);
				}

				if (style != null) {
					dialog.getDialogPane().getStylesheets().add(style);
				}

				if (buttons.isEmpty()) {
					buttons.addAll(Arrays.asList(ButtonType.OK, ButtonType.CANCEL));
				}

				Window window = dialog.getDialogPane().getScene().getWindow();
				if (window instanceof Stage) {
					((Stage) window).getIcons().addAll(Display.getStage().getIcons());
				}
				dialog.getDialogPane().getButtonTypes().addAll(buttons);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public static Builder create() {
			return new Builder();
		}
	}
}