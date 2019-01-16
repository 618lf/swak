package com.swak.fx.support;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.Sets;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Skin;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

/**
 * 带有下载功能的组件
 * 
 * @author lifeng
 */
@SuppressWarnings("restriction")
public class DownloadPane extends Control {

	/***************************************************************************
	 * 
	 * Static fields
	 * 
	 **************************************************************************/
	public static final ExecutorService factory = Executors.newFixedThreadPool(1,
			new EventLoopFactory(true, "download", new AtomicLong(0)));
	/**
	 * Called when the DownloadPane <b>will</b> be shown.
	 */
	public static final EventType<Event> ON_SHOWING = new EventType<>(Event.ANY, "DOWNLOAD_PANE_ON_SHOWING"); //$NON-NLS-1$

	/**
	 * Called when the DownloadPane shows.
	 */
	public static final EventType<Event> ON_SHOWN = new EventType<>(Event.ANY, "DOWNLOAD_PANE_ON_SHOWN"); //$NON-NLS-1$

	/**
	 * Called when the DownloadPane <b>will</b> be hidden.
	 */
	public static final EventType<Event> ON_HIDING = new EventType<>(Event.ANY, "DOWNLOAD_PANE_ON_HIDING"); //$NON-NLS-1$

	/**
	 * Called when the DownloadPane is hidden.
	 */
	public static final EventType<Event> ON_HIDDEN = new EventType<>(Event.ANY, "DOWNLOAD_PANE_ON_HIDDEN"); //$NON-NLS-1$

	/***************************************************************************
	 * 
	 * Constructors
	 * 
	 **************************************************************************/
	public DownloadPane() {
		this(null);
	}

	public DownloadPane(Node content) {
		setContent(content);
	}

	/***************************************************************************
	 * 
	 * Overriding public API
	 * 
	 **************************************************************************/
	@Override
	protected Skin<?> createDefaultSkin() {
		return new DownloadPaneSkin(this);
	}

	/***************************************************************************
	 * 
	 * Properties
	 * 
	 **************************************************************************/
	// --- content
	private ObjectProperty<Node> content = new SimpleObjectProperty<>(this, "content"); //$NON-NLS-1$

	public final ObjectProperty<Node> contentProperty() {
		return content;
	}

	public final void setContent(Node value) {
		this.content.set(value);
	}

	public final Node getContent() {
		return content.get();
	}

	// --- showing
	private ReadOnlyBooleanWrapper showing = new ReadOnlyBooleanWrapper(this, "showing"); //$NON-NLS-1$

	public final ReadOnlyBooleanProperty showingProperty() {
		return showing.getReadOnlyProperty();
	}

	private final void setShowing(boolean value) {
		this.showing.set(value);
	}

	public final boolean isShowing() {
		return showing.get();
	}

	// --- download
	private SimpleSetProperty<Download> downloads = new SimpleSetProperty<Download>(this, "downloads",
			FXCollections.observableSet(Sets.newLinkedHashSet()));

	public final ReadOnlySetProperty<Download> downloadsProperty() {
		return downloads;
	}

	public final void addDownload(Download value) {
		this.downloads.add(value);
	}

	/***************************************************************************
	 * 
	 * Public API
	 * 
	 **************************************************************************/
	public void download(String title, String url) {
		setShowing(true);
		Download download = new Download(title, url);
		downloads.get().add(download);
	}

	public void clear() {
		setShowing(false);
		downloads.get().clear();
	}

	/**
	 * 
	 * @author lifeng
	 */
	public static class DownloadPaneSkin extends BehaviorSkinBase<DownloadPane, BehaviorBase<DownloadPane>> {

		private DownloadParts downloadParts;
		private Node content;
		private Rectangle clip = new Rectangle();

		@SuppressWarnings("deprecation")
		protected DownloadPaneSkin(DownloadPane control) {
			super(control, new BehaviorBase<>(control, Collections.<KeyBinding>emptyList()));
			downloadParts = new DownloadParts() {
				@Override
				public void requestContainerLayout() {
					control.requestLayout();
				}

				@Override
				public boolean isShowing() {
					return control.isShowing();
				}

				@Override
				public void hide() {
					control.clear();
				}

				@Override
				public double getContainerHeight() {
					return control.getHeight();
				}

				@Override
				public void relocateInParent(double x, double y) {
					downloadParts.relocate(x, y);
				}
			};
			control.setClip(clip);
			updateContent();
			registerChangeListener(control.heightProperty(), "HEIGHT"); //$NON-NLS-1$
			registerChangeListener(control.contentProperty(), "CONTENT"); //$NON-NLS-1$
			registerChangeListener(control.showingProperty(), "SHOWING"); //$NON-NLS-1$
			registerChangeListener(control.downloadsProperty(), "DOWNLOADS"); //$NON-NLS-1$

			// Fix for Issue #522: Prevent DownloadPane from receiving focus
			ParentTraversalEngine engine = new ParentTraversalEngine(getSkinnable());
			getSkinnable().setImpl_traversalEngine(engine);
			engine.setOverriddenFocusTraversability(false);
		}

		private void updateContent() {
			if (content != null) {
				getChildren().remove(content);
			}
			content = getSkinnable().getContent();
			if (content == null) {
				getChildren().setAll(downloadParts);
			} else {
				getChildren().setAll(content, downloadParts);
			}
		}

		/**
		 * 事件更新的回调
		 */
		@Override
		protected void handleControlPropertyChanged(String p) {
			super.handleControlPropertyChanged(p);
			if ("CONTENT".equals(p)) { //$NON-NLS-1$
				updateContent();
			} else if ("SHOWING".equals(p)) { //$NON-NLS-1$
				if (getSkinnable().isShowing()) {
					downloadParts.doShow();
				} else {
					downloadParts.doHide();
				}
			} else if ("HEIGHT".equals(p)) {
				if (getSkinnable().isShowing()) {
					downloadParts.requestLayout();
				}
			} else { // $NON-NLS-1$
				this.getSkinnable().downloadsProperty().get().forEach(d -> {
					downloadParts.newPart(d);
				});
			}
		}

		@Override
		protected void layoutChildren(double x, double y, double w, double h) {
			final double notificationBarHeight = downloadParts.prefHeight(w);
			downloadParts.resize(w, notificationBarHeight);
			if (content != null) {
				content.resizeRelocate(x, y, w, h);
			}
			clip.setX(x);
			clip.setY(y);
			clip.setWidth(w);
			clip.setHeight(h);
		}

		@Override
		protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset,
				double leftInset) {
			return content == null ? 0 : content.minWidth(height);
		};

		@Override
		protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset,
				double leftInset) {
			return content == null ? 0 : content.minHeight(width);
		};

		@Override
		protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset,
				double leftInset) {
			return content == null ? 0 : content.prefWidth(height);
		};

		@Override
		protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset,
				double leftInset) {
			return content == null ? 0 : content.prefHeight(width);
		};

		@Override
		protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset,
				double leftInset) {
			return content == null ? 0 : content.maxWidth(height);
		};

		@Override
		protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset,
				double leftInset) {
			return content == null ? 0 : content.maxHeight(width);
		};
	}

	/**
	 * 所有的下载部件
	 * 
	 * @author lifeng
	 */
	public abstract static class DownloadParts extends Region {
		private static final double MIN_HEIGHT = 40;

		private final GridPane pane;
		Button closeBtn;
		HBox container;
		Set<Download> parts;
		public DoubleProperty transition = new SimpleDoubleProperty() {
			@Override
			protected void invalidated() {
				requestContainerLayout();
			}
		};

		public abstract void hide();

		public abstract boolean isShowing();

		public abstract double getContainerHeight();

		public abstract void relocateInParent(double x, double y);

		public DownloadParts() {
			parts = Sets.newLinkedHashSet();
			pane = new GridPane();
			pane.setAlignment(Pos.BASELINE_LEFT);
			pane.getStyleClass().add("download-panel");

			container = new HBox();
			pane.add(container, 0, 0);
			GridPane.setHgrow(container, Priority.ALWAYS);
			GridPane.setVgrow(container, Priority.ALWAYS);

			// initialise close button area
			closeBtn = new Button();
			closeBtn.setOnAction((event) -> {
				this.remove();
			});
			closeBtn.getStyleClass().setAll("close-button"); //$NON-NLS-1$
			StackPane graphic = new StackPane();
			graphic.getStyleClass().setAll("graphic"); //$NON-NLS-1$
			closeBtn.setGraphic(graphic);
			closeBtn.setMinSize(17, 17);
			closeBtn.setPrefSize(17, 17);
			closeBtn.setFocusTraversable(false);
			closeBtn.opacityProperty().bind(transition);
			GridPane.setMargin(closeBtn, new Insets(0, 0, 0, 8));
			pane.add(closeBtn, 1, 0);

			this.getChildren().setAll(pane);
			setVisible(isShowing());
		}

		public void remove() {
			this.hide();
			this.parts.clear();
			container.getChildren().forEach(node -> {
				if (node instanceof DownloadPart) {
					((DownloadPart) node).clear();
				}
			});
			container.getChildren().clear();
			this.layoutChildren();
		}

		public void newPart(Download download) {
			if (!parts.contains(download)) {
				parts.add(download);
				Display.runUI(() -> {
					DownloadPart part = new DownloadPart(download);
					container.getChildren().add(part);
					HBox.setMargin(part, new Insets(0, 5, 0, 0));
				});
			}
		}

		public void requestContainerLayout() {
			layoutChildren();
		}

		@Override
		protected void layoutChildren() {
			final double w = getWidth();
			final double notificationBarHeight = prefHeight(w);
			pane.resize(w, notificationBarHeight);
			relocateInParent(0, getContainerHeight() - notificationBarHeight);
		}

		@Override
		protected double computeMinHeight(double width) {
			return Math.max(super.computePrefHeight(width), MIN_HEIGHT);
		}

		@Override
		protected double computePrefHeight(double width) {
			return Math.max(pane.prefHeight(width), minHeight(width)) * transition.get();
		}

		public void doShow() {
			transitionStartValue = 0;
			doAnimationTransition();
		}

		public void doHide() {
			transitionStartValue = 1;
			doAnimationTransition();
		}

		// --- animation timeline code
		private final Duration TRANSITION_DURATION = new Duration(350.0);
		private Timeline timeline;
		private double transitionStartValue;

		private void doAnimationTransition() {
			Duration duration;

			if (timeline != null && (timeline.getStatus() != Status.STOPPED)) {
				duration = timeline.getCurrentTime();

				// fix for #70 - the notification pane freezes up as it has zero
				// duration to expand / contract
				duration = duration == Duration.ZERO ? TRANSITION_DURATION : duration;
				transitionStartValue = transition.get();
				// --- end of fix

				timeline.stop();
			} else {
				duration = TRANSITION_DURATION;
			}

			timeline = new Timeline();
			timeline.setCycleCount(1);

			KeyFrame k1, k2;

			if (isShowing()) {
				k1 = new KeyFrame(Duration.ZERO, new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						// start expand
						setCache(true);
						setVisible(true);

						pane.fireEvent(new Event(DownloadPane.ON_SHOWING));
					}
				}, new KeyValue(transition, transitionStartValue));

				k2 = new KeyFrame(duration, new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						// end expand
						pane.setCache(false);

						pane.fireEvent(new Event(DownloadPane.ON_SHOWN));
					}
				}, new KeyValue(transition, 1, Interpolator.EASE_OUT)

				);
			} else {
				k1 = new KeyFrame(Duration.ZERO, new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						// Start collapse
						pane.setCache(true);

						pane.fireEvent(new Event(DownloadPane.ON_HIDING));
					}
				}, new KeyValue(transition, transitionStartValue));

				k2 = new KeyFrame(duration, new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						// end collapse
						setCache(false);
						setVisible(false);

						pane.fireEvent(new Event(DownloadPane.ON_HIDDEN));
					}
				}, new KeyValue(transition, 0, Interpolator.EASE_IN));
			}

			timeline.getKeyFrames().setAll(k1, k2);
			timeline.play();
		}
	}

	/**
	 * 下载部件
	 * 
	 * @author lifeng
	 */
	public static class DownloadPart extends HBox implements Callable<Download> {

		Label title;
		ProgressIndicator progress;

		private final Download download;
		private final DoubleProperty progressProperty;
		private volatile Future<Download> _task;

		public DownloadPart(Download download) {
			this.download = download;
			this.progressProperty = new SimpleDoubleProperty(0);
			title = new Label(download.getName());
			progress = new ProgressIndicator(0);
			this.getChildren().add(title);
			this.getChildren().add(progress);
			title.setPrefHeight(this.getPrefHeight());
			title.setMaxWidth(120);
			title.setWrapText(true);
			this.getStyleClass().add("part");
			this.title.getStyleClass().add("part-title");
			this.progress.getStyleClass().add("part-progress");
			this.progress.heightProperty().addListener((observable, oldValue, newValue) -> {
				Display.runUI(() -> {
					title.setPrefHeight(newValue.doubleValue());
					this.layoutChildren();
				});
			});
			this.progressProperty.addListener((observable, oldValue, newValue) -> {
				Display.runUI(() -> {
					progress.setProgress(newValue.doubleValue());
					this.layoutChildren();
				});
			});
			title.setAlignment(Pos.CENTER);
			HBox.setHgrow(title, Priority.ALWAYS);
			HBox.setHgrow(progress, Priority.NEVER);
			_task = this.start();
			this.setOnMouseClicked(event -> {
				if (!_task.isDone()) {
					Notifys.error("提醒", "文档尚未下载完成！", Pos.CENTER);
					return;
				}
				if (_task.isCancelled() || !this.download.isSuccess()) {
					Notifys.error("提醒", "文档下载失败！", Pos.CENTER);
					return;
				}
				DirectoryChooser chooser = new DirectoryChooser();
				chooser.setTitle("文件另存为");
				File dir = chooser.showDialog(Display.getStage());
				if (dir != null && download.renameto(dir)) {
					Notifys.info("提醒", "文档下载完成！");
				} else if (dir != null) {
					Notifys.error("提醒", "文档下载错误！", Pos.CENTER);
				}
			});
		}

		/**
		 * 开启线程下载数据
		 */
		private Future<Download> start() {
			return factory.submit(this);
		}

		/**
		 * 清除
		 */
		public void clear() {
			if (!_task.isDone() && !_task.isCancelled()) {
				_task.cancel(true);
			}
		}

		/**
		 * 下载文件
		 */
		@Override
		public Download call() throws Exception {
			HttpURLConnection connection = null;
			InputStream read = null;
			OutputStream write = null;
			try {
				URL postUrl = new URL(download.getUrl());
				connection = (HttpURLConnection) postUrl.openConnection();
				connection.setUseCaches(false);
				connection.setRequestProperty("User-Agent",
						" Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36");
				connection.setRequestProperty("Accept-Encoding", "identity");
				connection.setConnectTimeout(15000);
				connection.setReadTimeout(15000);
				connection.connect();
				if (200 == connection.getResponseCode()) {
					read = connection.getInputStream();
					write = new FileOutputStream(download.getFile().toFile());
					long total = connection.getContentLength();
					byte[] buffer = new byte[10];
					int len = 0;
					int count = 0;
					while (-1 != (len = read.read(buffer))) {
						write.write(buffer, 0, len);
						count += len;
						double rate = ((count / (float) total) * 100);
						if (rate >= 1.0) {
							rate = 0.99;
						}
						this.progressProperty.set(rate);
					}
					download.finish();
					this.progressProperty.set(1.0);
				}
				return download;
			} catch (Exception e) {
				return download;
			} finally {
				try {
					if (read != null) {
						read.close();
					}
					if (write != null) {
						write.close();
					}
					connection.disconnect();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 下载的内容
	 * 
	 * @author lifeng
	 */
	public static class Download implements Serializable {

		private static final long serialVersionUID = 1L;
		private static final AtomicInteger nums = new AtomicInteger(0);

		private final String prefix;
		private final String suffix;
		private final String url;
		private Path file;
		private volatile boolean success = false;

		public Download(String name, String url) {
			this.prefix = name.substring(0, name.lastIndexOf('.'));
			this.suffix = (name.substring(name.lastIndexOf('.')));
			this.url = url;
			this.file = createTempFile();
		}

		private Path createTempFile() {
			try {
				return Files.createTempFile("downloading", "." + nums.getAndIncrement() + ".tmp");
			} catch (Exception e) {
				return null;
			}
		}

		public void finish() {
			if (this.renameto(this.file.getParent().toFile())) {
				this.success = true;
			}
		}

		public boolean renameto(File dir) {
			File dest = new File(dir, this.prefix + this.suffix);
			int num = 1;
			while (dest.exists()) {
				dest = new File(dir, String.format("%s(%s)%s", this.prefix, num++, this.suffix));
			}
			try {
				if (this.file.toFile().exists()) {
					Files.copy(this.file, dest.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
					this.file = dest.toPath();
					return true;
				}
				return false;
			} catch (IOException e) {
				return false;
			}
		}

		public String getName() {
			return prefix + suffix;
		}

		public String getUrl() {
			return url;
		}

		public Path getFile() {
			return file;
		}

		public boolean isSuccess() {
			return success;
		}
	}
}