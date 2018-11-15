package com.tmt.manage.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * image button
 * 
 * @author lifeng
 */
public class ImageButton implements PaintListener {

	// 元素组件
	private Label label;
	private Object layout;
	private Image image;
	private Image hoverImage;
	private Runnable click;
	private volatile boolean on = false;

	/**
	 * 创建一个图片按钮
	 * 
	 * @param parent
	 */
	private ImageButton(Composite parent) {
		this.label = new Label(parent, SWT.NONE);
	}

	/**
	 * 自适应图片的显示
	 */
	@Override
	public void paintControl(PaintEvent pe) {

		// 绘制的图片
		Image image = this.drawable();

		// 绘制
		final Rectangle ibounds = image.getBounds();
		int iwidth = ibounds.width; // 图片宽
		int iheight = ibounds.height; // 图片高

		final Rectangle lbounds = label.getBounds();
		int lwidth = lbounds.width; // 图片宽
		int lheight = lbounds.height; // 图片高
		double ratio = 1; // 缩放比率
		double r1 = lwidth * ratio / iwidth;
		double r2 = lheight * ratio / iheight;
		ratio = Math.min(r1, r2);
		pe.gc.drawImage(image, 0, 0, iwidth, iheight, 0, 0, (int) (iwidth * ratio), (int) (iheight * ratio));
	}

	/**
	 * 获得需要绘制的图片
	 * 
	 * @return
	 */
	private Image drawable() {
		if (this.on && hoverImage != null) {
			return hoverImage;
		}
		return image;
	}

	/**
	 * 设置图片
	 * 
	 * @param image
	 * @return
	 */
	public ImageButton image(Image image) {
		this.image = image;
		return this;
	}

	/**
	 * 布局
	 * 
	 * @param layout
	 * @return
	 */
	public ImageButton layout(Object layout) {
		this.layout = layout;
		return this;
	}

	/**
	 * hover
	 * 
	 * @param layout
	 * @return
	 */
	public ImageButton hover(Image image) {
		this.hoverImage = image;
		return this;
	}

	/**
	 * 设置点击事件
	 * 
	 * @param run
	 * @return
	 */
	public ImageButton click(Runnable click) {
		this.click = click;
		return this;
	}

	/**
	 * 创建组件
	 */
	public void build() {
		this.label.setLayoutData(layout);
		this.label.addPaintListener(this);
		
		// hover 事件
		if (this.hoverImage != null) {
			this.label.addMouseTrackListener(new MouseTrackListener() {
				@Override
				public void mouseEnter(MouseEvent arg0) {
					on = true;
					label.redraw();
					label.getShell().setCursor(ResourceManager.getCursor(SWT.CURSOR_HAND));
				}

				@Override
				public void mouseExit(MouseEvent arg0) {
					on = false;
					label.redraw();
					label.getShell().setCursor(ResourceManager.getCursor(SWT.CURSOR_ARROW));
				}

				@Override
				public void mouseHover(MouseEvent arg0) {
				}
			});
		}

		// 点击事件
		if (this.click != null) {
			this.label.addListener(SWT.MouseUp, (e) -> {
				this.click.run();
			});
		}
	}

	/**
	 * 创建一个图片按钮
	 * 
	 * @param parent
	 * @return
	 */
	public static ImageButton builder(Composite parent) {
		ImageButton imageButton = new ImageButton(parent);
		return imageButton;
	}
}
