package com.swak.fx.support;

import java.awt.SystemTray;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.Clipboard;
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
		eventBus = new AsyncEventBus("UI.app",
				Executors.newFixedThreadPool(1, new EventLoopFactory(true, "UI.app", new AtomicLong())));
		eventBus.register(this);
		Display.setEventBus(eventBus);
	}

	/**
	 * 定制启动流程
	 */
	@Override
	public void init() throws Exception {
		this.start(savedArgs).whenComplete((ctx, throwable) -> {
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
			this.stop(Display.getStage()).whenComplete((v, t) -> {
				Platform.exit();
				System.exit(0);
			});
		}
	}

	/**
	 * 启动服务
	 * 
	 * @param savedArgs
	 */
	protected abstract CompletableFuture<Void> start(String[] savedArgs);

	/**
	 * 停止服务
	 * 
	 * @param savedArgs
	 */
	protected abstract CompletableFuture<Void> stop(final Stage stage);

	/**
	 * 程序定制
	 * 
	 * @param stage
	 */
	protected abstract void customStage(Stage stage, SystemTray tray);

	/**
	 * 创建页面
	 * 
	 * @param savedArgs
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	protected AbstractPage createPage(Class<? extends AbstractPage> view)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		return view.getDeclaredConstructor().newInstance();
	}

	/**
	 * 启动
	 */
	@Override
	public void start(final Stage stage) throws Exception {
		Display.setStage(stage);
		Display.setHostServices(this.getHostServices());
		Display.setClipboard(Clipboard.getSystemClipboard());
		if (splashView != null) {
			AbstractPage splash = this.createPage(splashView);
			final Stage splashStage = new Stage(StageStyle.TRANSPARENT);
			final Scene splashScene = new Scene(splash.getView(), Color.TRANSPARENT);
			this.customStage(splashStage, null);
			splashStage.setScene(splashScene);
			splashStage.initStyle(StageStyle.TRANSPARENT);
			splashStage.show();

			splashIsShowing.complete(() -> {
				splash.waitClose().whenComplete((v, t) -> {
					Display.runUI(() -> {
						showMainView().whenComplete((v1, t1) -> {
							Display.runUI(() -> {
								v1.run();
								splashStage.close();
								splashStage.setScene(null);
							});
						});
					});
				});
			});
		} else {
			splashIsShowing.complete(() -> {
				Display.runUI(() -> {
					showMainView().whenComplete((v1, t1) -> {
						Display.runUI(() -> {
							v1.run();
						});
					});
				});
			});
		}
	}

	/**
	 * 显示主界面
	 */
	protected CompletableFuture<Runnable> showMainView() {
		CompletableFuture<Runnable> mainViewInited = new CompletableFuture<>();
		try {
			AbstractPage page = this.createPage(mainView);
			if (Display.getScene() == null) {
				Display.setScene(new Scene(page.getView()));
			} else {
				Display.getScene().setRoot(page.getView());
			}
			Display.getStage().setTitle(page.getDefaultTitle());
			Display.getStage().initStyle(page.getDefaultStyle());
			Display.getStage().setScene(Display.getScene());
			this.customStage(Display.getStage(), Display.getSystemTray());
			page.whenInited().thenAcceptAsync(v -> {
				this.postInitialized();
				mainViewInited.complete(() -> {
					Display.getStage().show();
				});
			});
		} catch (Throwable t) {
			LOGGER.error("Failed to load application: ", t);
			mainViewInited.completeExceptionally(t);
			showErrorAlert(t);
		}
		return mainViewInited;
	}

	/**
	 * 所有初始化之后
	 */
	protected void postInitialized() {
	}

	/**
	 * 启动错误
	 * 
	 * @param throwable
	 */
	protected void showErrorAlert(Throwable throwable) {
		Alert alert = new Alert(AlertType.ERROR,
				"Oops! An unrecoverable error occurred.\n" + "Please contact your software vendor.\n\n"
						+ "The application will stop now.\n\n" + "Error: " + throwable.getMessage());
		alert.showAndWait().ifPresent(response -> Platform.exit());
	}

	/**
	 * Launch App
	 * 
	 * @param appClass
	 * @param mainClass
	 * @param splashScreen
	 * @param args
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

	/**
	 * Launch App - No Splash
	 * 
	 * @param appClass
	 * @param mainClass
	 * @param splashScreen
	 * @param args
	 */
	public static void launch(final Class<? extends Application> appClass,
			final Class<? extends AbstractPage> mainClass, final String[] args) {
		mainView = mainClass;
		savedArgs = args;
		if (SystemTray.isSupported()) {
			Display.setSystemTray(SystemTray.getSystemTray());
		}
		Application.launch(appClass, args);
	}
}