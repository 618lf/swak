package com.swak.swing.support;

import java.awt.SystemTray;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.swak.ui.Event;
import com.swak.ui.EventListener;
import com.swak.ui.EventLoopFactory;

/**
 * 定义基础的启动流程
 * 
 * @author lifeng
 * @date 2020年5月21日 上午10:33:50
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
		eventBus = new AsyncEventBus("UI.eventbus",
				Executors.newFixedThreadPool(1, new EventLoopFactory(true, "UI.eventbus", new AtomicLong())));
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
				Display.runUI(() -> showErrorAlert(throwable));
			}
		}).thenAcceptBothAsync(splashIsShowing, (ctx, closeSplash) -> {
			closeSplash.run();
		});
	}

	@Override
	public void listen(Event event) {
		if (event == Event.EXIT) {
			this.stop().whenComplete((v, t) -> {
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
	protected abstract CompletableFuture<Void> stop();

	/**
	 * 程序定制
	 * 
	 * @param stage
	 */
	protected abstract void customStage(AbstractPage page, SystemTray tray);

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
	public void start() throws Exception {
		if (splashView != null) {
			AbstractPage splash = this.createPage(splashView);
			splash.show();
			this.customStage(splash, null);
			splashIsShowing.complete(() -> {
				splash.waitClose().whenComplete((v, t) -> {
					Display.runUI(() -> {
						showMainView().whenComplete((v1, t1) -> {
							Display.runUI(() -> {
								v1.run();
								splash.close();
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
			this.customStage(page, Display.getSystemTray());
			page.whenInited().thenAcceptAsync(v -> {
				this.postInitialized(page);
				mainViewInited.complete(() -> {
					page.show();
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
	protected void postInitialized(AbstractPage mainPage) {

	}

	/**
	 * 启动错误
	 * 
	 * @param throwable
	 */
	protected void showErrorAlert(Throwable throwable) {

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
