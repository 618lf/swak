package com.swak.fx.support;

import java.awt.SystemTray;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.swak.reactivex.transport.resources.EventLoopFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The Class AbstractApplication.
 *
 * @author Felix Roske
 */
public abstract class AbstractApplication extends Application implements EventListener {

	private static Logger LOGGER = LoggerFactory.getLogger(AbstractApplication.class);
	private static String[] savedArgs = new String[0];
	static Class<? extends AbstractPage> mainView;
	static Class<? extends AbstractPage> splashView;
	private final CompletableFuture<Runnable> splashIsShowing;
	private final EventBus eventBus;

	protected AbstractApplication() {
		splashIsShowing = new CompletableFuture<>();
		eventBus = new AsyncEventBus("app",
				Executors.newFixedThreadPool(1, new EventLoopFactory(true, "app", new AtomicLong())));
		eventBus.register(this);
		Display.setEventBus(eventBus);
	}

	@Override
	public void init() throws Exception {
		CompletableFuture.supplyAsync(() -> this.start(savedArgs)).whenComplete((ctx, throwable) -> {
			if (throwable != null) {
				LOGGER.error("Failed to load spring application context: ", throwable);
				Platform.runLater(() -> showErrorAlert(throwable));
			}
		}).thenAcceptBothAsync(splashIsShowing, (ctx, closeSplash) -> {
			closeSplash.run();
		});
	}

	/**
	 * 执行监听关闭
	 */
	@Override
	public void listen(Event event) {
		if (event == Event.EXIT) {
			try {
				Platform.exit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 启动服务
	 * 
	 * @param savedArgs
	 */
	protected abstract <T> T start(String[] savedArgs);

	/**
	 * 启动
	 */
	@Override
	public void start(final Stage stage) throws Exception {
		Display.setStage(stage);
		Display.setHostServices(this.getHostServices());
		AbstractPage splash = splashView.newInstance();
		final Stage splashStage = new Stage(StageStyle.TRANSPARENT);
		final Scene splashScene = new Scene(splash.getView(), Color.TRANSPARENT);
		this.customStage(splashStage, null);
		splashStage.setScene(splashScene);
		splashStage.initStyle(StageStyle.TRANSPARENT);
		splashStage.show();

		splashIsShowing.complete(() -> {
			splash.close().whenComplete((v, t) -> {
				Display.runUI(() -> {
					showView();
					splashStage.close();
					splashStage.setScene(null);
				});
			});
		});
	}

	/**
	 * Show view.
	 *
	 * @param newView
	 *            the new view
	 */
	public void showView() {
		try {
			AbstractPage page = mainView.newInstance();
			if (Display.getScene() == null) {
				Display.setScene(new Scene(page.getView()));
			} else {
				Display.getScene().setRoot(page.getView());
			}
			Display.getStage().setTitle(page.getDefaultTitle());
			Display.getStage().initStyle(page.getDefaultStyle());
			Display.getStage().setScene(Display.getScene());
			this.customStage(Display.getStage(), Display.getSystemTray());
			Display.getStage().show();
		} catch (Throwable t) {
			LOGGER.error("Failed to load application: ", t);
			showErrorAlert(t);
		}
	}

	/**
	 * 程序定制
	 * 
	 * @param stage
	 */
	protected abstract void customStage(Stage stage, SystemTray tray);

	/**
	 * Show error alert that close app.
	 *
	 * @param throwable
	 *            cause of error
	 */
	private void showErrorAlert(Throwable throwable) {
		Alert alert = new Alert(AlertType.ERROR,
				"Oops! An unrecoverable error occurred.\n" + "Please contact your software vendor.\n\n"
						+ "The application will stop now.\n\n" + "Error: " + throwable.getMessage());
		alert.showAndWait().ifPresent(response -> Platform.exit());
	}

	/**
	 * Launch app.
	 *
	 * @param appClass
	 *            the app class
	 * @param view
	 *            the view
	 * @param splashScreen
	 *            the splash screen
	 * @param args
	 *            the args
	 */
	public static void launch(final Class<? extends Application> appClass,
			final Class<? extends AbstractPage> mainClass, final Class<? extends AbstractPage> splashScreen,
			final String[] args) {
		mainView = mainClass;
		savedArgs = args;
		if (splashScreen != null) {
			AbstractApplication.splashView = splashScreen;
		}
		if (SystemTray.isSupported()) {
			Display.setSystemTray(SystemTray.getSystemTray());
		}

		Application.launch(appClass, args);
	}
}