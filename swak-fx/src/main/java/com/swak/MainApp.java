package com.swak;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;

import com.swak.fx.support.Display;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * fx界面
 * 
 * @author lifeng
 */
public class MainApp extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		Parent content = FXMLLoader.load(Display.load("/fxml/Hello.fxml"));
		Scene scene = new Scene(content);
		scene.getStylesheets().add("/css/bootstrapfx.css");
		scene.getStylesheets().add(Display.load("/css/styles.css").toExternalForm());
		stage.setTitle("个税易客户端");
		stage.getIcons().add(new Image(Display.load("/images/logo.png").toExternalForm()));
		stage.setScene(scene);
		stage.sizeToScene();
		stage.setOnCloseRequest(event -> {
			System.out.println("退出1");
			event.consume();
		});
		Display.setScene(scene);
		Display.setStage(stage);
		stage.show();

		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			java.awt.Image image = Toolkit.getDefaultToolkit().getImage("/images/logo.png");
			PopupMenu popup = new PopupMenu();
			MenuItem item = new MenuItem("Exit");

			popup.add(item);

			TrayIcon trayIcon = new TrayIcon(image, "Amr_Trial", popup);

			ActionListener listener = new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent arg0) {
					System.exit(0);
				}
			};

			ActionListener listenerTray = new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent arg0) {
					Display.runUI(() -> {
						if (stage.isShowing()) {
							stage.hide();
						} else {
							stage.show();
						}
					});
				}
			};

			trayIcon.addActionListener(listenerTray);
			item.addActionListener(listener);

			try {
				tray.add(trayIcon);
			} catch (Exception e) {
				System.err.println("Can't add to tray");
			}
		} else {
			System.err.println("Tray unavailable");
		}
	}

	/**
	 * The main() method is ignored in correctly deployed JavaFX application. main()
	 * serves only as fallback in case the application can not be launched through
	 * deployment artifacts, e.g., in IDEs with limited FX support. NetBeans ignores
	 * main().
	 *
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
