package com.swak.swing.support;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * 窗口标准的事件处理
 * 
 * @author lifeng
 * @date 2020年5月22日 下午3:09:53
 */
public abstract class Window extends AbstractPage {

	protected JFrame root;
	protected Cursor old;
	private double lastX = 0.0d;
	private double lastY = 0.0d;
	private double lastWidth = 0.0d;
	private double lastHeight = 0.0d;
	private double startMoveX = -1;
	private double startMoveY = -1;
	private Boolean dragging = false;
	protected boolean closed;

	@Override
	public void initialize() {
		super.initialize();
		root.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		root.addWindowListener(new WindowCloseListener());
	}

	/**
	 * 窗口隐藏
	 * 
	 * @param e
	 */
	protected void onHide(MouseEvent e) {
		this.close();
	}

	/**
	 * 窗口关闭
	 * 
	 * @param e
	 */
	protected void onClose(MouseEvent e) {
		this.close();
	}

	/**
	 * 窗口最小化
	 * 
	 * @param e
	 */
	protected void onMinimize(MouseEvent e) {
		this.root.setExtendedState(JFrame.ICONIFIED);// 最小化窗体
	}

	/**
	 * 窗口最大化
	 * 
	 * @param e
	 */
	protected void onMaximize(MouseEvent e) {
		Rectangle w = this.root.getBounds();
		double currentX = w.getX();
		double currentY = w.getY();
		double currentWidth = w.getWidth();
		double currentHeight = w.getHeight();

		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(root.getGraphicsConfiguration());
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		// if not maximized
		if (dim.height - screenInsets.bottom - screenInsets.top != currentHeight
				&& dim.width - screenInsets.left - screenInsets.right != currentWidth) {
			this.root.setLocation(screenInsets.left, screenInsets.top);
			this.root.setSize(dim.width - screenInsets.left - screenInsets.right,
					dim.height - screenInsets.bottom - screenInsets.top);

			// save old dimensions
			lastX = currentX;
			lastY = currentY;
			lastWidth = currentWidth;
			lastHeight = currentHeight;
		} else {
			this.root.setLocation((int) lastX, (int) lastY);
			this.root.setSize((int) lastWidth, (int) lastHeight);
		}
	}

	/**
	 * 咬住
	 * 
	 * @param evt
	 */
	protected void onStartMoveWindow(MouseEvent evt) {
		startMoveX = evt.getX();
		startMoveY = evt.getY();
		dragging = true;
	}

	/**
	 * 移动
	 * 
	 * @param evt
	 */
	protected void onMoveWindow(MouseEvent evt) {
		if (dragging) {
			double endMoveX = evt.getXOnScreen();
			double endMoveY = evt.getYOnScreen();
			root.setLocation((int) ((endMoveX - startMoveX)), (int) ((endMoveY - startMoveY)));
		}
	}

	/**
	 * 释放
	 * 
	 * @param evt
	 */
	protected void onEndMoveWindow(MouseEvent evt) {
		startMoveX = 0;
		startMoveY = 0;
		dragging = false;
	}

	/**
	 * 窗口关闭
	 * 
	 * @author lifeng
	 * @date 2020年5月22日 下午5:05:20
	 */
	public class WindowCloseListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			onClose(null);
		}
	}

	/**
	 * 点击事件
	 * 
	 * @author lifeng
	 * @date 2020年5月22日 下午3:30:10
	 */
	public class MinimizeListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			onMinimize(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			old = root.getCursor();
			root.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (old != null) {
				root.setCursor(old);
			}
		}
	}

	/**
	 * 点击事件
	 * 
	 * @author lifeng
	 * @date 2020年5月22日 下午3:30:10
	 */
	public class ActionListener extends MouseAdapter {

		@Override
		public void mouseEntered(MouseEvent e) {
			old = root.getCursor();
			root.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (old != null) {
				root.setCursor(old);
			}
		}
	}

	/**
	 * 点击事件
	 * 
	 * @author lifeng
	 * @date 2020年5月22日 下午3:30:10
	 */
	public class MaximizeListener extends ActionListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			onMaximize(e);
		}
	}

	/**
	 * 点击事件
	 * 
	 * @author lifeng
	 * @date 2020年5月22日 下午3:30:10
	 */
	public class CloseListener extends ActionListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			onClose(e);
		}
	}

	/**
	 * 拖动
	 * 
	 * @author lifeng
	 * @date 2020年5月22日 下午3:30:10
	 */
	public class MoveListener extends ActionListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() >= 2) {
				onMaximize(e);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			onStartMoveWindow(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			onEndMoveWindow(e);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			super.mouseDragged(e);
			onMoveWindow(e);
		}
	}
}
