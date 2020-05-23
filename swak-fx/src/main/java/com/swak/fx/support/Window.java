package com.swak.fx.support;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * 自定义的窗体
 * 
 * @author lifeng
 */
public class Window extends AbstractPage {

	@FXML
	protected Pane root;
	@FXML
	protected VBox leftPane;
	@FXML
	protected VBox rightPane;
	@FXML
	protected HBox topPane;
	@FXML
	protected Rectangle bottomPane;
	@FXML
	protected Rectangle nWPane;
	@FXML
	protected Rectangle nEPane;
	@FXML
	protected Label title;

	private double startMoveX = -1;
	private double startMoveY = -1;
	private Boolean dragging = false;
	private Rectangle moveTrackingRect;
	private Popup moveTrackingPopup;
	private double lastX = 0.0d;
	private double lastY = 0.0d;
	private double lastWidth = 0.0d;
	private double lastHeight = 0.0d;

	@FXML
	public void initialize() {
		super.initialize();
		this.title.setText(this.getDefaultTitle());
	}

	// 真实的关闭
	/**
	 * 隐藏， 默认这两个是一致的，但如果设置
	 * Platform.setImplicitExit(false); 就不一致
	 * 
	 * 如果设置了上面的配置则需要使用如下的退出方式
	 * Platform.exit();
	 * 
	 * @param evt
	 */
	@FXML
	public void onHide(MouseEvent evt) {
		this.root.getScene().getWindow().hide();
	}

	/**
	 * 关闭
	 * 
	 * @param evt
	 */
	@FXML
	public void onClose(MouseEvent evt) {
		this.root.getScene().getWindow().hide();
	}

	@FXML
	public void startMoveWindow(MouseEvent evt) {
		startMoveX = evt.getScreenX();
		startMoveY = evt.getScreenY();
		dragging = true;

		moveTrackingRect = new Rectangle();
		moveTrackingRect.setWidth(this.root.getWidth());
		moveTrackingRect.setHeight(this.root.getHeight());
		moveTrackingRect.setStyle("-fx-border-radius:5;-fx-fill: white;-fx-opacity: 0.9;-fx-stroke: darkgray;");

		moveTrackingPopup = new Popup();
		moveTrackingPopup.getContent().add(moveTrackingRect);
		moveTrackingPopup.show(this.root.getScene().getWindow());
		moveTrackingPopup.setOnHidden((e) -> resetMoveOperation());
	}

	@FXML
	public void moveWindow(MouseEvent evt) {
		if (dragging) {
			double endMoveX = evt.getScreenX();
			double endMoveY = evt.getScreenY();
			javafx.stage.Window w = this.root.getScene().getWindow();
			double stageX = w.getX();
			double stageY = w.getY();
			moveTrackingPopup.setX(stageX + (endMoveX - startMoveX));
			moveTrackingPopup.setY(stageY + (endMoveY - startMoveY));
		}
	}

	@FXML
	public void endMoveWindow(MouseEvent evt) {
		if (dragging) {
			double endMoveX = evt.getScreenX();
			double endMoveY = evt.getScreenY();
			javafx.stage.Window w = this.root.getScene().getWindow();
			double stageX = w.getX();
			double stageY = w.getY();
			w.setX(stageX + (endMoveX - startMoveX));
			w.setY(stageY + (endMoveY - startMoveY));
			if (moveTrackingPopup != null) {
				moveTrackingPopup.hide();
				moveTrackingPopup = null;
			}
		}
		resetMoveOperation();
	}

	private void resetMoveOperation() {
		startMoveX = 0;
		startMoveY = 0;
		dragging = false;
		moveTrackingRect = null;
	}

	@FXML
	public void titleDoubleClick(MouseEvent evt) {
		if (evt.getClickCount() != 2) {
            return;
        }
		maximize(evt);
	}

	@FXML
	public void maximize(MouseEvent evt) {
		javafx.stage.Window w = this.root.getScene().getWindow();
		double currentX = w.getX();
		double currentY = w.getY();
		double currentWidth = w.getWidth();
		double currentHeight = w.getHeight();

		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();

		if (currentX != bounds.getMinX() && currentY != bounds.getMinY() && currentWidth != bounds.getWidth()
				&& currentHeight != bounds.getHeight()) { // if not maximized

			w.setX(bounds.getMinX());
			w.setY(bounds.getMinY());
			w.setWidth(bounds.getWidth());
			w.setHeight(bounds.getHeight());

			lastX = currentX; // save old dimensions
			lastY = currentY;
			lastWidth = currentWidth;
			lastHeight = currentHeight;
		} else {
			w.setX(lastX);
			w.setY(lastY);
			w.setWidth(lastWidth);
			w.setHeight(lastHeight);

		}
		evt.consume();
	}

	@FXML
	public void minimize(MouseEvent evt) {
		Stage stage = (Stage) this.root.getScene().getWindow();
		stage.setIconified(true);
	}

	@FXML
	public void leftChangeSize(MouseEvent evt) {
		leftPane.setCursor(Cursor.H_RESIZE);
	}

	@FXML
	public void startChangeLeftSize(MouseEvent evt) {
		this.startChangeSize(evt);
	}

	@FXML
	public void changeLeftSize(MouseEvent evt) {
		if (dragging) {
			double endMoveX = evt.getScreenX();
			// double endMoveY = evt.getScreenY();

			javafx.stage.Window w = this.root.getScene().getWindow();
			double changeW = endMoveX - this.startMoveX;
			// double changeH = endMoveY - this.startMoveY;

			w.setX(lastX + changeW);
			w.setWidth(lastWidth - changeW);
		}
	}

	@FXML
	public void endChangeLeftSize(MouseEvent evt) {
		this.endChangeSize(evt);
	}

	@FXML
	public void rightChangeSize(MouseEvent evt) {
		rightPane.setCursor(Cursor.H_RESIZE);
	}

	@FXML
	public void startChangeRightSize(MouseEvent evt) {
		this.startChangeSize(evt);
	}

	@FXML
	public void changeRightSize(MouseEvent evt) {
		if (dragging) {
			double endMoveX = evt.getScreenX();
			// double endMoveY = evt.getScreenY();
			javafx.stage.Window w = this.root.getScene().getWindow();
			double changeW = endMoveX - this.startMoveX;
			// double changeH = endMoveY - this.startMoveY;
			w.setWidth(lastWidth + changeW);
		}
	}

	@FXML
	public void endChangeRightSize(MouseEvent evt) {
		this.endChangeSize(evt);
	}

	@FXML
	public void bottomChangeSize(MouseEvent evt) {
		bottomPane.setCursor(Cursor.V_RESIZE);
	}

	@FXML
	public void startChangeBottomSize(MouseEvent evt) {
		this.startChangeSize(evt);
	}

	@FXML
	public void changeBottomSize(MouseEvent evt) {
		if (dragging) {
			// double endMoveX = evt.getScreenX();
			double endMoveY = evt.getScreenY();
			javafx.stage.Window w = this.root.getScene().getWindow();
			// double changeW = endMoveX - this.startMoveX;
			double changeH = endMoveY - this.startMoveY;
			w.setHeight(lastHeight + changeH);
		}
	}

	@FXML
	public void endChangeBottomSize(MouseEvent evt) {
		endChangeSize(evt);
	}

	@FXML
	public void nEChangeSize(MouseEvent evt) {
		nEPane.setCursor(Cursor.NE_RESIZE);
	}

	@FXML
	public void startChangeNESize(MouseEvent evt) {
		this.startChangeSize(evt);
	}

	@FXML
	public void changeNESize(MouseEvent evt) {
		if (dragging) {
			double endMoveX = evt.getScreenX();
			double endMoveY = evt.getScreenY();

			javafx.stage.Window w = this.root.getScene().getWindow();
			double changeW = endMoveX - this.startMoveX;
			double changeH = endMoveY - this.startMoveY;
			w.setX(lastX + changeW);
			w.setWidth(lastWidth - changeW);
			w.setHeight(lastHeight + changeH);
		}
	}

	@FXML
	public void endChangeNESize(MouseEvent evt) {
		endChangeSize(evt);
	}

	@FXML
	public void nWChangeSize(MouseEvent evt) {
		nWPane.setCursor(Cursor.NW_RESIZE);
	}

	@FXML
	public void startChangeNWSize(MouseEvent evt) {
		startChangeSize(evt);
	}

	@FXML
	public void changeNWSize(MouseEvent evt) {
		if (dragging) {
			double endMoveX = evt.getScreenX();
			double endMoveY = evt.getScreenY();

			javafx.stage.Window w = this.root.getScene().getWindow();
			double changeW = endMoveX - this.startMoveX;
			double changeH = endMoveY - this.startMoveY;
			w.setHeight(lastHeight + changeH);
			w.setWidth(lastWidth + changeW);
		}
	}

	@FXML
	public void endChangeNWSize(MouseEvent evt) {
		if (dragging) {
			dragging = false;
		}
	}

	private void startChangeSize(MouseEvent evt) {
		startMoveX = evt.getScreenX();
		startMoveY = evt.getScreenY();
		dragging = true;
		javafx.stage.Window w = this.root.getScene().getWindow();
		lastHeight = w.getHeight();
		lastWidth = w.getWidth();
		lastX = w.getX();
		lastY = w.getY();
	}

	private void endChangeSize(MouseEvent evt) {
		if (dragging) {
			dragging = false;
		}
	}
}
